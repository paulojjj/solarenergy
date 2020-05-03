package paulojjj.solarenergy.tiles;

import java.util.Random;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import paulojjj.solarenergy.IEnergyProducer;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.networks.SolarGeneratorNetwork;
import paulojjj.solarenergy.proxy.CommonProxy;

public class SolarGeneratorTileEntity extends EnergyNetworkTileEntity implements IEnergyProducer {

	private double maxProduction = 1;
	private double activeProduction = 0;

	private Tier tier;
	
	private boolean canSeeSky;
	private long nextSkyCheck;
	private static Random random = new Random();

	public SolarGeneratorTileEntity() {
		this(Tier.BASIC);
	}

	public SolarGeneratorTileEntity(Tier tier) {
		super();
		setTier(tier);
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		maxProduction = (int) Math.pow(10, tier.ordinal());
		markDirty();
	}

	public static class SolarGeneratorContainerUpdateMessage {
		public double activeProduction;
		public double maxProduction;
		public double output;
		public boolean sunActive;

		public SolarGeneratorContainerUpdateMessage() {
		}

		public SolarGeneratorContainerUpdateMessage(double activeProduction, double maxProduction,
				double output) {
			super();
			this.activeProduction = activeProduction;
			this.maxProduction = maxProduction;
			this.output = output;
		}
	}

	public double getMaxProduction() {
		return maxProduction;
	}

	public double getActiveProduction() {
		return activeProduction;
	}
	
	@Override
	public double getProduction() {
		return getActiveProduction();
	}
	
	@Override
	public double receiveUltraEnergy(double maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public double extractUltraEnergy(double maxExtract, boolean simulate) {
		double sent = Math.min(maxExtract, energy);
		if(!simulate) {
			energy -= sent;
		}
		return (int)sent;		
	}

	@Override
	public double getUltraEnergyStored() {
		return energy;
	}

	@Override
	public double getMaxUltraEnergyStored() {
		return activeProduction;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
	
	protected boolean isSunActive() {
		long tick = CommonProxy.getTick();
		
		if(!world.isDaytime()) {
			return false;
		}
		
		if(tick >= nextSkyCheck) {
			nextSkyCheck = nextSkyCheck + random.nextInt(100) + 1;
			canSeeSky = world.canSeeSky(getPos().offset(EnumFacing.UP)); 
		}
		
		return canSeeSky;
	}

	@Override
	public void update() {
		super.update();
		if(world.isRemote || !world.isBlockLoaded(pos)) {
			return;
		}
		
		activeProduction = isSunActive() ? maxProduction : 0;

		if(activeProduction > 0 && energy < getMaxUltraEnergyStored()) {
			energy += Math.min(activeProduction, getMaxUltraEnergyStored() - energy);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int tierValue = compound.getInteger(NBT.TIER);
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setInteger(NBT.TIER, tier.ordinal());
		return compound;
	}

	@Override
	public String toString() {
		return super.toString() + " [position=" + pos + "]";
	}

	@Override
	public Class<?> getNetworkClass() {
		return SolarGeneratorNetwork.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object getContainerUpdateMessage() {
		double output = getNetwork().getEnergyOutput();
		double activeProduction = 0;
		double maxProduction = 0;
		for(SolarGeneratorTileEntity tile : (Set<SolarGeneratorTileEntity>)getNetwork().getTiles()) {
			activeProduction += tile.getActiveProduction();
			maxProduction += tile.getMaxProduction();
		}
		return new SolarGeneratorContainerUpdateMessage(activeProduction, maxProduction, output);
	}

}
