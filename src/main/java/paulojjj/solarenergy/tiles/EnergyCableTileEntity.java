package paulojjj.solarenergy.tiles;

import paulojjj.solarenergy.networks.EnergyCableNetwork;

public class EnergyCableTileEntity extends EnergyNetworkTileEntity {

	public EnergyCableTileEntity() {
		super();
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
	
	@Override
	public Class<?> getNetworkClass() {
		return EnergyCableNetwork.class;
	}
	
}
