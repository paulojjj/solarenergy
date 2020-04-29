package paulojjj.solarenergy.tiles;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.networks.CapabilityDelegate;
import paulojjj.solarenergy.networks.INetwork;
import paulojjj.solarenergy.networks.INetworkMember;

public abstract class EnergyNetworkTileEntity extends TileEntity implements INetworkMember, ITickable {

	protected double energy = 0;
	private CapabilityDelegate delegate = new CapabilityDelegate(getNetwork());
	private INetwork<EnergyNetworkTileEntity> network = null;

	private Set<EntityPlayer> playersUsing = new HashSet<>();

	public abstract Class<?> getNetworkClass();

	public static class EnergyNetworkContainerUpdateMessage {
		public double energyStored;
		public double maxEnergyStored;
		public double input;
		public double output;
		
		public EnergyNetworkContainerUpdateMessage() {
		}
		
		public EnergyNetworkContainerUpdateMessage(double energyStored, double maxEnergyStored, double input,
				double output) {
			super();
			this.energyStored = energyStored;
			this.maxEnergyStored = maxEnergyStored;
			this.input = input;
			this.output = output;
		}
	}

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
	
	protected Object getContainerUpdateMessage() {
		double energy = getNetwork().getUltraEnergyStored();
		double maxEnergy = getNetwork().getMaxUltraEnergyStored();
		double input = getNetwork().getEnergyInput();
		double output = getNetwork().getEnergyOutput();
		return new EnergyNetworkContainerUpdateMessage(energy, maxEnergy, input, output);		
	}

	@Override
	public void update() {
		if(world.isRemote) {
			return;
		}
		for(EntityPlayer player : playersUsing) {
			PacketManager.sendContainerUpdateMessage((EntityPlayerMP)player, getContainerUpdateMessage());
		}
		network.update();
	}

	public void onContainerOpened(EntityPlayer player) {
		playersUsing.add(player);
	}

	public void onContainerClosed(EntityPlayer player) {
		playersUsing.remove(player);
	}

}
