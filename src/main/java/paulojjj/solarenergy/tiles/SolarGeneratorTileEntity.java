package paulojjj.solarenergy.tiles;

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
		return production * 1;
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
		if(world.isRemote) {
			return;
		}
		if(!world.isDaytime() || !world.canSeeSky(getPos().offset(EnumFacing.UP))) {
			return;
		}

		if(energy < getMaxUltraEnergyStored()) {
			energy += Math.min(production, getMaxUltraEnergyStored() - energy);
		}

/*		for(EnumFacing facing : EnumFacing.values()) {
			if(facing != EnumFacing.UP) {
				TileEntity tile = world.getTileEntity(getPos().offset(facing));
				if(tile != null && !tile.getClass().equals(SolarGenerator.class) && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
					IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
					if(energyStorage.canReceive()) {
						double maxExtract = getNetwork().getUltraEnergyStored();
						if(maxExtract == 0) {
							break;
						}
						if(energyStorage instanceof IUltraEnergyStorage) {
							getNetwork().extractUltraEnergy(((IUltraEnergyStorage)energyStorage).receiveUltraEnergy(maxExtract, false), false);
						}
						else {
							int maxExtractInt = (int)Math.min(maxExtract, Integer.MAX_VALUE);
							getNetwork().extractEnergy(energyStorage.receiveEnergy(maxExtractInt, false), false);
						}
					}
				}
			}
		}
 */
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
		return "SolarGeneratorTileEntity [position=" + pos + "]";
	}

	@Override
	public Class<?> getNetworkClass() {
		return SolarGeneratorNetwork.class;
	}

}
