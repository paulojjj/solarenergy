package paulojjj.solarenergy.tiles;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.Log;
import paulojjj.solarenergy.networks.CapabilityDelegate;
import paulojjj.solarenergy.networks.INetwork;
import paulojjj.solarenergy.networks.INetworkMember;

public abstract class EnergyNetworkTileEntity extends EnergyStorageTileEntity implements INetworkMember, ITickable {

	protected CapabilityDelegate delegate = new CapabilityDelegate(getNetwork());
	private INetwork<EnergyNetworkTileEntity> network = null;

	public abstract Class<?> getNetworkClass();

	private boolean unloaded = false;

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
		}
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

	public void onNeighborChanged(BlockPos neighborPos) {
		if(!world.isRemote) {
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

}
