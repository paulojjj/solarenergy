package paulojjj.solarenergy.tiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.Log;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.networks.CapabilityDelegate;
import paulojjj.solarenergy.networks.INetwork;
import paulojjj.solarenergy.networks.INetworkMember;

public abstract class EnergyNetworkTileEntity extends EnergyStorageTileEntity implements INetworkMember, ITickableTileEntity, IMessageListener<EnergyNetworkUpdateMessage> {

	public EnergyNetworkTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public static final int BLOCK_UPDATE = 2;

	public static final int UPDATE_BLOCK = 1;
	public static final int SEND_TO_CLIENT = 2;

	protected CapabilityDelegate delegate = new CapabilityDelegate(getNetwork());
	private INetwork<EnergyNetworkTileEntity> network = null;

	public abstract Class<?> getNetworkClass();

	private boolean unloaded = false;

	private Set<Direction> neighborStorages = new HashSet<>();
	
	private boolean loaded = false;

	@Override
	public INetwork<?> getNetwork() {
		return network;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setNetwork(INetwork<?> network) {
		if(world.isRemote) {
			return;
		}
		this.network = (INetwork<EnergyNetworkTileEntity>)network;
		this.delegate.setTarget(network);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return LazyOptional.of(() -> world.isRemote ? (T) this : (T) delegate);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	//World.getTileEntity in unloaded chunks triggers TileEntity.onLoad
	public void onLoad() {
		super.onLoad();
		if(!world.isRemote) {
			loaded = false;
		}
	}

	protected IEnergyStorage getNeighborStorage(Direction facing) {
		BlockPos neighbosPos = pos.offset(facing);
		IEnergyStorage storage = null;
		if(world.isAreaLoaded(neighbosPos, 0)) {
			TileEntity te = world.getTileEntity(neighbosPos);
			if(te == null) {
				return null;
			}
			storage = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).orElse(null);
		}
		return storage;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		if(!world.isRemote) {
			if(unloaded) {
				Log.warn("Invalidating unloaded TileEntity: " + this);
			}
			if(!unloaded && network != null) {
				network.onBlockRemoved(this);
			}
		}
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		if(!world.isRemote) {
			if(!unloaded) {
				unloaded = true;
				if(network != null) {
					network.onChunkUnload(pos);
				}
			}
		}
	}

	protected Direction getNeighborFacing(BlockPos neighbor) {
		for(Direction facing : Direction.values()) {
			if(pos.offset(facing).equals(neighbor)) {
				return facing;
			}
		}
		throw new RuntimeException("Invalid neighbor " + neighbor + " from " + pos);
	}

	protected void updateClientTileEntity() {
		EnergyNetworkUpdateMessage message = new EnergyNetworkUpdateMessage();
		Collection<Byte> neighbors = new ArrayList<>();
		synchronized(this) {
			for(Direction facing : neighborStorages) {
				neighbors.add((byte)facing.ordinal());
			}
		}
		message.setNeighborStorages(neighbors);

		PacketManager.sendToAllTracking(this, message);
	}

	public void onNeighborChanged(BlockPos neighborPos) {
		if(!world.isRemote) {
			if(neighborPos.equals(pos)) {
				return;
			}
			Log.info(pos + " neighbor changed: " + neighborPos);
			Direction facing = getNeighborFacing(neighborPos);
			IEnergyStorage storage = getNeighborStorage(facing);
			synchronized(this) {
				if(storage == null) {
					neighborStorages.remove(facing);
				}
				else {
					neighborStorages.add(facing);
				}
			}
			updateClientTileEntity();			

			network.onNeighborChanged(this, neighborPos);
		}
	}

	protected Object getContainerUpdateMessage() {
		double energy = getNetwork().getUltraEnergyStored();
		double maxEnergy = getNetwork().getMaxUltraEnergyStored();
		double input = getNetwork().getEnergyInput();
		double output = getNetwork().getEnergyOutput();
		return new EnergyStorageContainerUpdateMessage(energy, maxEnergy, input, output);		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void tick() {
		super.tick();
		if(world.isRemote || !world.isAreaLoaded(pos, 0)) {
			return;
		}
		if(!loaded) {
			loaded = true;
			Log.info("TileEntity Loaded: " + this);
			INetwork.newInstance((Class<INetwork<EnergyNetworkTileEntity>>)getNetworkClass(), this);
			
			for(Direction facing : Direction.values()) {
				IEnergyStorage storage = getNeighborStorage(facing);
				if(storage != null) {
					synchronized (this) {
						neighborStorages.add(facing);
					}
				}
				
				//Calls onNeighborChanged so neighbors that are still not loaded can be detected after loaded (on neighborChanged is not called by default when world loads)
				BlockPos neighborPos = pos.offset(facing);
				if(world.isAreaLoaded(neighborPos, 0)) {
					TileEntity te = world.getTileEntity(neighborPos);
					if(te instanceof EnergyNetworkTileEntity) {
						EnergyNetworkTileEntity ente = (EnergyNetworkTileEntity)te;
						if(ente.network != null) {
							ente.onNeighborChanged(pos);
						}
					}
				}
			}
			BlockState bs = world.getBlockState(pos);
			int flags = SEND_TO_CLIENT;
			updateClientTileEntity();
			world.notifyBlockUpdate(pos, bs, bs, flags);			
		}
		
		if(network != null) {
			network.update();
		}
	}

	public synchronized boolean hasStorage(Direction facing) {
		return neighborStorages.contains(facing);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();

		synchronized(this) {
			int size = neighborStorages.size();
			byte[] neighbors = new byte[size];
			int i=0;
			for(Direction facing : neighborStorages) {
				neighbors[i++] = (byte)facing.ordinal();
			}
			nbt.putByteArray("storages", neighbors);
		}
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState blockState, CompoundNBT tag) {
		super.handleUpdateTag(blockState, tag);
		byte[] storages = tag.getByteArray("storages");
		synchronized(this) {
			neighborStorages.clear();
			for(int i=0; i<storages.length; i++) {
				neighborStorages.add(Direction.byIndex(storages[i]));
			}
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
	}

	@Override
	public synchronized void onMessage(EnergyNetworkUpdateMessage message) {
		neighborStorages.clear();
		for(Byte f : message.getNeighborStorages()) {
			neighborStorages.add(Direction.byIndex(f));
		}
	}

}
