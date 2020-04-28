package paulojjj.solarenergy;

import net.minecraftforge.energy.IEnergyStorage;

public interface IUltraEnergyStorage extends IEnergyStorage {
	
    double receiveUltraEnergy(double maxReceive, boolean simulate);

    double extractUltraEnergy(double maxExtract, boolean simulate);

    double getUltraEnergyStored();

    double getMaxUltraEnergyStored();
    
    @Override
    default int getMaxEnergyStored() {
		return (int)Math.min(Integer.MAX_VALUE, getMaxUltraEnergyStored());
    }
    
    @Override
    default int getEnergyStored() {
		return (int)Math.min(Integer.MAX_VALUE, getUltraEnergyStored());
    }
    
    @Override
    default int receiveEnergy(int maxReceive, boolean simulate) {
    	return (int)receiveUltraEnergy(maxReceive, simulate);
    }
    
    @Override
    default int extractEnergy(int maxExtract, boolean simulate) {
    	return (int)extractUltraEnergy(maxExtract, simulate);
    }

}
