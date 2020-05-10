package paulojjj.solarenergy.networks;

import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

public class EnergyCableNetwork extends BaseNetwork<EnergyCableTileEntity> {

	public EnergyCableNetwork() {
		canExtract = false;
		canReceive = true;
	}

	@Override
	public Class<EnergyCableTileEntity> getTileClass() {
		return EnergyCableTileEntity.class;
	}
	
	@Override
	public double receiveUltraEnergy(double maxReceive, boolean simulate) {
		if(maxReceive == 0) {
			return 0;
		}
		double sent = sendToConsumers(maxReceive, simulate);
		if(sent == 0) {
			return 0;
		}
		if(!simulate) {
			sentSinceLastTick += sent;
			receivedSinceLastTick += sent;
		}
		return sent;
	}
	
	@Override
	public double getMaxUltraEnergyStored() {
		return 0;
	}

}
