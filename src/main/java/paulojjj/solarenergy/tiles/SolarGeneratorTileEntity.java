package paulojjj.solarenergy.tiles;

import java.util.Random;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.Config;
import paulojjj.solarenergy.IEnergyProducer;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.TickHandler;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.networks.SolarGeneratorNetwork;

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
	
	public Tier getTier() {
		return tier;
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		maxProduction = Math.pow(10, tier.ordinal()) * Config.getInstance().getSolarGeneratorMultiplier(tier);
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
		return sent;		
	}

	@Override
	public double getUltraEnergyStored() {
		return energy;
	}

	@Override
	public double getMaxUltraEnergyStored() {
		return maxProduction;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(facing == EnumFacing.UP) {
			return false;
		}
		return super.hasCapability(capability, facing);
	}
	
	protected boolean isSunActive() {
		long tick = TickHandler.getTick();
		
		if(!world.isDaytime()) {
			return false;
		}
		
		if(!Config.getInstance().getProduceWhileRaining() && world.isRaining()) {
			return false;
		}
		
		if(tick >= nextSkyCheck) {
			nextSkyCheck = nextSkyCheck + random.nextInt(100) + 1;
			canSeeSky = world.canSeeSky(getPos().offset(EnumFacing.UP));
		}
		
		return canSeeSky;
	}
	
	public double productionByTick(long tick, double maxProduction) {
		if(maxProduction == 0) {
			return 0;
		}

		double hour = ((tick + 6000)%24000)/1000.0;
		//Quadractic function with points: (4,0), (12,1), (20,0)
		//double multiplier = -0.015625 * Math.pow(hour, 2) + 0.375 * hour - 1.25;
		//Quadractic function with points: (5.5,0), (12,1), (18.5,0)
		double multiplier = (-4 * Math.pow(hour, 2) + 96 * hour - 407)/169;
		
		if(multiplier <= 0) {
			return 0;
		}
		
		return multiplier * maxProduction;
	}

	@Override
	public void update() {
		super.update();
		if(world.isRemote || !world.isBlockLoaded(pos)) {
			return;
		}
		
		activeProduction = isSunActive() ? maxProduction : 0;
		
		if(Config.getInstance().getRealisticGeneration()) {
			activeProduction = productionByTick(world.getWorldTime(), activeProduction);
		}

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
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt =  super.getUpdateTag();
		nbt.setInteger(NBT.TIER, tier.ordinal());
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		Tier tier =  Tier.values()[tag.getInteger(NBT.TIER)];
		setTier(tier);
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
	
	@Override
	protected IEnergyStorage getNeighborStorage(EnumFacing facing) {
		IEnergyStorage storage = super.getNeighborStorage(facing);
		if(storage != null && storage instanceof SolarGeneratorTileEntity) {
			storage = null;
		}
		return storage;
	}

}
