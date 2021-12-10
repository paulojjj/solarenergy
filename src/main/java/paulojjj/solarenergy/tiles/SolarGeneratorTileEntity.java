package paulojjj.solarenergy.tiles;

import java.util.Random;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.Config;
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

	public SolarGeneratorTileEntity(BlockPos pos, BlockState state) {
		this(Tier.BASIC, pos, state);
	}

	public SolarGeneratorTileEntity(Tier tier, BlockPos pos, BlockState state) {
		super(TileEntities.SOLAR_GENERATOR.getType(), pos, state);
		setTier(tier);
	}
	
	public Tier getTier() {
		return tier;
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		maxProduction = Math.pow(10, tier.ordinal()) * Config.getInstance().getSolarGeneratorMultiplier(tier);
		setChanged();
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
		
		if(!level.isDay()) {
			return false;
		}
		
		if(!Config.getInstance().getProduceWhileRaining() && level.isRaining()) {
			return false;
		}
		
		if(tick >= nextSkyCheck) {
			nextSkyCheck = nextSkyCheck + random.nextInt(100) + 1;
			canSeeSky = level.canSeeSky(getBlockPos().relative(Direction.UP)); 
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
	public void tick() {
		super.tick();
		if(level.isClientSide || !level.isAreaLoaded(worldPosition, 0)) {
			return;
		}
		
		activeProduction = isSunActive() ? maxProduction : 0;
		
		if(Config.getInstance().getRealisticGeneration()) {
			activeProduction = productionByTick(level.getDayTime(), activeProduction);
		}

		if(activeProduction > 0 && energy < getMaxUltraEnergyStored()) {
			energy += Math.min(activeProduction, getMaxUltraEnergyStored() - energy);
		}
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		int tierValue = compound.getInt(NBT.TIER);
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putInt(NBT.TIER, tier.ordinal());
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt =  super.getUpdateTag();
		nbt.putInt(NBT.TIER, tier.ordinal());
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
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
