package paulojjj.solarenergy.tiles;

import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import paulojjj.solarenergy.IEnergyProducer;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.networks.SolarGeneratorNetwork;

public class SolarGeneratorTileEntity extends EnergyNetworkTileEntity implements IEnergyProducer {

	private double production = 1;

	private Tier tier;

	public SolarGeneratorTileEntity() {
		this(Tier.BASIC);
	}

	public SolarGeneratorTileEntity(Tier tier) {
		super();
		setTier(tier);
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		production = (int) Math.pow(10, tier.ordinal());
		markDirty();
	}

	public static class SolarGeneratorContainerUpdateMessage {
		public double production;
		public double output;

		public SolarGeneratorContainerUpdateMessage() {
		}

		public SolarGeneratorContainerUpdateMessage(double production,
				double output) {
			super();
			this.production = production;
			this.output = output;
		}
	}

	@Override
	public double getProduction() {
		return production;
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
		return production;
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
	public void update() {
		super.update();
		if(world.isRemote || !world.isBlockLoaded(pos)) {
			return;
		}
		if(!world.isDaytime() || !world.canSeeSky(getPos().offset(EnumFacing.UP))) {
			return;
		}

		if(energy < getMaxUltraEnergyStored()) {
			energy += Math.min(production, getMaxUltraEnergyStored() - energy);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int tierValue = compound.getInteger("tier");
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setInteger("tier", tier.ordinal());
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

	@Override
	protected Object getContainerUpdateMessage() {
		double output = getNetwork().getEnergyOutput();
		double production = getNetwork().getTiles().stream().map(x -> ((SolarGeneratorTileEntity)x).getProduction()).collect(Collectors.summingDouble(x -> x));
		return new SolarGeneratorContainerUpdateMessage(production, output);
	}

}
