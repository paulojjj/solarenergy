package paulojjj.solarenergy.tiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.Log;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.networks.CapabilityDelegate;
import paulojjj.solarenergy.networks.INetwork;
import paulojjj.solarenergy.networks.INetworkMember;

public abstract class EnergyNetworkTileEntity extends EnergyStorageTileEntity implements INetworkMember, ITickable, IMessageListener<EnergyNetworkUpdateMessage> {

	public static final int BLOCK_UPDATE = 2;

	public static final int UPDATE_BLOCK = 1;
	public static final int SEND_TO_CLIENT = 2;

	protected CapabilityDelegate delegate = new CapabilityDelegate(getNetwork());
	private INetwork<EnergyNetworkTileEntity> network = null;

	public abstract Class<?> getNetworkClass();

	private boolean unloaded = false;

	private Set<EnumFacing> neighborStorages = new HashSet<>();

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
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return world.isRemote ? (T) this : (T) delegate;
		}
		return super.getCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	//World.getTileEntity in unloaded chunks triggers TileEntity.onLoad
	public void onLoad() {
		super.onLoad();
		if(!world.isRemote) {
			Log.info("TileEntity Loaded: " + this);
			INetwork.newInstance((Class<INetwork<EnergyNetworkTileEntity>>)getNetworkClass(), this);

			for(EnumFacing facing : EnumFacing.values()) {
				IEnergyStorage storage = getNeighborStorage(facing);
				if(storage != null) {
					synchronized (this) {
						neighborStorages.add(facing);
					}
				}
				
				//Calls onNeighborChanged so neighbors that are still not loaded can be detected after loaded (on neighborChanged is not called by default when world loads)
				BlockPos neighborPos = pos.offset(facing);
				if(world.isBlockLoaded(neighborPos)) {
					TileEntity te = world.getTileEntity(neighborPos);
					if(te instanceof EnergyNetworkTileEntity) {
						EnergyNetworkTileEntity ente = (EnergyNetworkTileEntity)te;
						if(ente.network != null) {
							ente.onNeighborChanged(pos);
						}
					}
				}
			}
			IBlockState bs = world.getBlockState(pos);
			int flags = SEND_TO_CLIENT;
			updateClientTileEntity();
			world.notifyBlockUpdate(pos, bs, bs, flags);			
		}
	}

	protected IEnergyStorage getNeighborStorage(EnumFacing facing) {
		BlockPos neighbosPos = pos.offset(facing);
		IEnergyStorage storage = null;
		if(world.isBlockLoaded(neighbosPos)) {
			TileEntity te = world.getTileEntity(neighbosPos);
			if(te == null) {
				return null;
			}
			if(te.hasCapability(CapabilityEnergy.ENERGY, facing)) {
				storage = te.getCapability(CapabilityEnergy.ENERGY, facing);
			}
		}
		return storage;
	}

	@Override
	public void invalidate() {
		super.invalidate();
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
	public void onChunkUnload() {
		super.onChunkUnload();
		if(!world.isRemote) {
			if(!unloaded) {
				unloaded = true;
				if(network != null) {
					network.onChunkUnload(pos);
				}
			}
		}
	}

	protected EnumFacing getNeighborFacing(BlockPos neighbor) {
		for(EnumFacing facing : EnumFacing.values()) {
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
			for(EnumFacing facing : neighborStorages) {
				neighbors.add((byte)facing.ordinal());
			}
		}
		message.setNeighborStorages(neighbors);

		PacketManager.sendToAllTracking(this, message);
	}

	public void onNeighborChanged(BlockPos neighborPos) {
		if(!world.isRemote) {
			Log.info("Neighbor changed at " + neighborPos);
			EnumFacing facing = getNeighborFacing(neighborPos);
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

	@Override
	public void update() {
		super.update();
		if(world.isRemote || !world.isBlockLoaded(pos)) {
			return;
		}
		if(network != null) {
			network.update();
		}
	}

	public synchronized boolean hasStorage(EnumFacing facing) {
		return neighborStorages.contains(facing);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();

		synchronized(this) {
			int size = neighborStorages.size();
			byte[] neighbors = new byte[size];
			int i=0;
			for(EnumFacing facing : neighborStorages) {
				neighbors[i++] = (byte)facing.ordinal();
			}
			nbt.setByteArray("storages", neighbors);
		}
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		byte[] storages = tag.getByteArray("storages");
		synchronized(this) {
			neighborStorages.clear();
			for(int i=0; i<storages.length; i++) {
				neighborStorages.add(EnumFacing.getFront(storages[i]));
			}
		}
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public synchronized void onMessage(EnergyNetworkUpdateMessage message) {
		neighborStorages.clear();
		for(Byte f : message.getNeighborStorages()) {
			neighborStorages.add(EnumFacing.getFront(f));
		}
	}

}
