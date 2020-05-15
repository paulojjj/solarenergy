package paulojjj.solarenergy.tiles;

import java.util.Random;
import java.util.Set;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.IEnergyProducer;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.TickHandler;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.networks.SolarGeneratorNetwork;
import paulojjj.solarenergy.registry.TileEntities;

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
		super(TileEntities.SOLAR_GENERATOR.getType());
		setTier(tier);
	}
	
	public Tier getTier() {
		return tier;
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		maxProduction = Math.pow(10, tier.ordinal());
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
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability ==  CapabilityEnergy.ENERGY && facing == Direction.UP) {
			return LazyOptional.empty();
		}
		return super.getCapability(capability, facing);
	}
	
	protected boolean isSunActive() {
		long tick = TickHandler.getTick();
		
		if(!world.isDaytime()) {
			return false;
		}
		
		if(tick >= nextSkyCheck) {
			nextSkyCheck = nextSkyCheck + random.nextInt(100) + 1;
			canSeeSky = world.canSeeSky(getPos().offset(Direction.UP)); 
		}
		
		return canSeeSky;
	}

	@Override
	public void tick() {
		super.tick();
		if(world.isRemote || !world.isBlockLoaded(pos)) {
			return;
		}
		
		activeProduction = isSunActive() ? maxProduction : 0;

		if(activeProduction > 0 && energy < getMaxUltraEnergyStored()) {
			energy += Math.min(activeProduction, getMaxUltraEnergyStored() - energy);
		}
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		int tierValue = compound.getInt(NBT.TIER);
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound = super.write(compound);
		compound.putInt(NBT.TIER, tier.ordinal());
		return compound;
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt =  super.getUpdateTag();
		nbt.putInt(NBT.TIER, tier.ordinal());
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
		Tier tier =  Tier.values()[tag.getInt(NBT.TIER)];
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
	protected IEnergyStorage getNeighborStorage(Direction facing) {
		IEnergyStorage storage = super.getNeighborStorage(facing);
		if(storage != null && storage instanceof SolarGeneratorTileEntity) {
			storage = null;
		}
		return storage;
	}

}
