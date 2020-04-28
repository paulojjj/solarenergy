package paulojjj.solarenergy.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.networks.CapabilityDelegate;
import paulojjj.solarenergy.networks.INetwork;
import paulojjj.solarenergy.networks.INetworkMember;

public abstract class EnergyNetworkTileEntity extends TileEntity implements INetworkMember, ITickable {

	protected double energy = 0;
	private CapabilityDelegate delegate = new CapabilityDelegate(getNetwork());
	private INetwork<EnergyNetworkTileEntity> network = null;


	public abstract Class<?> getNetworkClass();

	@Override
	public INetwork<?> getNetwork() {
		return network;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setNetwork(INetwork<?> network) {
		this.network = (INetwork<EnergyNetworkTileEntity>)network;
		this.delegate.setTarget(network);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return (T) delegate;
		}
		return super.getCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoad() {
		super.onLoad();
		if(!world.isRemote) {
			INetwork.newInstance((Class<INetwork<EnergyNetworkTileEntity>>)getNetworkClass(), this);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(!world.isRemote) {
			network.onBlockRemoved(this);
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(!world.isRemote) {
			network.destroy();
		}
	}

	public void onNeighborChanged(BlockPos neighborPos) {
		if(!world.isRemote) {
			network.onNeighborChanged(this, neighborPos);
		}
	}

	@Override
	public void update() {
		if(world.isRemote) {
			return;
		}
		network.update();
	}


}
