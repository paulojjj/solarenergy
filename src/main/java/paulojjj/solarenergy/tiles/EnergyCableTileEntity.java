package paulojjj.solarenergy.tiles;

import paulojjj.solarenergy.networks.EnergyCableNetwork;
import paulojjj.solarenergy.registry.TileEntities;

public class EnergyCableTileEntity extends EnergyNetworkTileEntity {

	public EnergyCableTileEntity() {
		super(TileEntities.ENERGY_CABLE.getType());
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
