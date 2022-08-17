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
		if(level.isClientSide) {
			return;
		}
		this.network = (INetwork<EnergyNetworkTileEntity>)network;
		this.delegate.setTarget(network);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return LazyOptional.of(() -> level.isClientSide ? (T) this : (T) delegate);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	//World.getTileEntity in unloaded chunks triggers TileEntity.onLoad
	public void onLoad() {
		super.onLoad();
		if(!level.isClientSide) {
			loaded = false;
			Log.debug("TileEntity Loaded: " + this);
		}
	}

	protected IEnergyStorage getNeighborStorage(Direction facing) {
		BlockPos neighbosPos = worldPosition.relative(facing);
		IEnergyStorage storage = null;
		if(level.isAreaLoaded(neighbosPos, 0)) {
			TileEntity te = level.getBlockEntity(neighbosPos);
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
		if(!level.isClientSide) {
			if(unloaded) {
				Log.debug("Invalidating unloaded TileEntity: " + this);
			}
			if(!unloaded && network != null) {
				network.onBlockRemoved(this);
			}
		}
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		if(!level.isClientSide) {
			if(!unloaded) {
				unloaded = true;
				if(network != null) {
					network.onChunkUnload(worldPosition);
				}
			}
		}
	}

	protected Direction getNeighborFacing(BlockPos neighbor) {
		for(Direction facing : Direction.values()) {
			if(worldPosition.relative(facing).equals(neighbor)) {
				return facing;
			}
		}
		throw new RuntimeException("Invalid neighbor " + neighbor + " from " + worldPosition);
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
		if(!level.isClientSide) {
			if(neighborPos.equals(worldPosition)) {
				return;
			}
			Log.debug(worldPosition + " neighbor changed: " + neighborPos);
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

			if(network != null) {
				network.onNeighborChanged(this, neighborPos);
			}
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
		if(level.isClientSide || !level.isAreaLoaded(worldPosition, 0)) {
			return;
		}
		if(!loaded) {
			loaded = true;
			Log.debug("TileEntity Loaded: " + this);
			INetwork.newInstance((Class<INetwork<EnergyNetworkTileEntity>>)getNetworkClass(), this);
			
			for(Direction facing : Direction.values()) {
				IEnergyStorage storage = getNeighborStorage(facing);
				if(storage != null) {
					synchronized (this) {
						neighborStorages.add(facing);
					}
				}
				
				//Calls onNeighborChanged so neighbors that are still not loaded can be detected after loaded (on neighborChanged is not called by default when world loads)
				BlockPos neighborPos = worldPosition.relative(facing);
				if(level.isAreaLoaded(neighborPos, 0)) {
					TileEntity te = level.getBlockEntity(neighborPos);
					if(te instanceof EnergyNetworkTileEntity) {
						EnergyNetworkTileEntity ente = (EnergyNetworkTileEntity)te;
						if(ente.network != null) {
							ente.onNeighborChanged(worldPosition);
						}
					}
				}
			}
			BlockState bs = level.getBlockState(worldPosition);
			int flags = SEND_TO_CLIENT;
			updateClientTileEntity();
			level.sendBlockUpdated(worldPosition, bs, bs, flags);			
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
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
		byte[] storages = tag.getByteArray("storages");
		synchronized(this) {
			neighborStorages.clear();
			for(int i=0; i<storages.length; i++) {
				neighborStorages.add(Direction.from3DDataValue(storages[i]));
			}
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getTag());
	}

	@Override
	public synchronized void onMessage(EnergyNetworkUpdateMessage message) {
		neighborStorages.clear();
		for(Byte f : message.getNeighborStorages()) {
			neighborStorages.add(Direction.from3DDataValue(f));
		}
	}

}
