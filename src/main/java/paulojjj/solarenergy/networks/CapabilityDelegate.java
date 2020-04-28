package paulojjj.solarenergy.networks;

import paulojjj.solarenergy.IUltraEnergyStorage;

public class CapabilityDelegate implements IUltraEnergyStorage {
	
	private IUltraEnergyStorage target;
	
	public CapabilityDelegate(IUltraEnergyStorage target) {
		setTarget(target);
	}
	
	public void setTarget(IUltraEnergyStorage target) {
		this.target = target;
	}

	public double receiveUltraEnergy(double maxReceive, boolean simulate) {
		return target == null ? 0 : target.receiveUltraEnergy(maxReceive, simulate);
	}

	public double extractUltraEnergy(double maxExtract, boolean simulate) {
		return target == null ? 0 : target.extractUltraEnergy(maxExtract, simulate);
	}

	public double getUltraEnergyStored() {
		return target == null ? 0 : target.getUltraEnergyStored();
	}

	public double getMaxUltraEnergyStored() {
		return target == null ? 0 : target.getMaxUltraEnergyStored();
	}

	public boolean canExtract() {
		return target == null ? false : target.canExtract();
	}

	public boolean canReceive() {
		return target == null ? false : target.canReceive();
	}

}
