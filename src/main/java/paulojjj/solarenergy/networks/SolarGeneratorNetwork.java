package paulojjj.solarenergy.networks;

import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGeneratorNetwork extends BaseNetwork<SolarGeneratorTileEntity> {
	
	public SolarGeneratorNetwork() {
	}

	@Override
	protected Class<SolarGeneratorTileEntity> getTileClass() {
		return SolarGeneratorTileEntity.class;
	}
	
}
