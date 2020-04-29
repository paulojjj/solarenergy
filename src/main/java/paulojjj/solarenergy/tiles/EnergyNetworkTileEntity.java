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
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.networks.CapabilityDelegate;
import paulojjj.solarenergy.networks.INetwork;
import paulojjj.solarenergy.networks.INetworkMember;

public abstract class EnergyNetworkTileEntity extends TileEntity implements INetworkMember, ITickable {

	protected double energy = 0;
	protected double maxEnergy = 0;
	protected CapabilityDelegate delegate = new CapabilityDelegate(getNetwork());
	private INetwork<EnergyNetworkTileEntity> network = null;

	private Set<EntityPlayer> playersUsing = new HashSet<>();

	public abstract Class<?> getNetworkClass();

	private boolean unloaded = false;

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
		if(world.isRemote) {
			return;
		}
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
			Main.logger.info("TileEntity Loaded: " + this);
			INetwork.newInstance((Class<INetwork<EnergyNetworkTileEntity>>)getNetworkClass(), this);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(!world.isRemote) {
			if(unloaded) {
				Main.logger.warn("Invalidating unloaded TileEntity: " + this);
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
		return new EnergyNetworkContainerUpdateMessage(energy, maxEnergy, input, output);		
	}

	@Override
	public void update() {
		if(world.isRemote || !world.isBlockLoaded(pos)) {
			return;
		}
		for(EntityPlayer player : playersUsing) {
			PacketManager.sendContainerUpdateMessage((EntityPlayerMP)player, getContainerUpdateMessage());
		}
		if(network != null) {
			network.update();
		}
	}

	public void onContainerOpened(EntityPlayer player) {
		if(!world.isRemote) {
			playersUsing.add(player);
		}
	}

	public void onContainerClosed(EntityPlayer player) {
		if(!world.isRemote) {
			playersUsing.remove(player);
		}
	}
	
	@Override
	public double extractUltraEnergy(double maxExtract, boolean simulate) {
		if(energy == 0 || !canExtract()) {
			return 0;
		}
		double sent = Math.min(maxExtract, energy);
		if(!simulate) {
			energy -= sent;
		}
		return sent;
	}

	@Override
	public double receiveUltraEnergy(double maxReceive, boolean simulate) {
		if(maxEnergy == 0 || energy == maxEnergy || !canReceive()) {
			return 0;
		}
		double received = Math.min(maxReceive, maxEnergy - energy);
		if(!simulate) {
			energy += received;
		}
		return received;
	}

	@Override
	public double getUltraEnergyStored() {
		return energy;
	}

	public void setUltraEnergyStored(double value) {
		energy = value;
	}

	@Override
	public double getMaxUltraEnergyStored() {
		return maxEnergy;
	}

	public void setMaxUltraEnergyStored(double value) {
		maxEnergy = value;
	}
}
