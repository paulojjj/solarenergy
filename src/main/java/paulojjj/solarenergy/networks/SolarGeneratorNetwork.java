package paulojjj.solarenergy.networks;

import net.minecraft.util.EnumFacing;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGeneratorNetwork extends BaseNetwork<SolarGeneratorTileEntity> {
	
	public SolarGeneratorNetwork() {
	}

	@Override
	protected Class<SolarGeneratorTileEntity> getTileClass() {
		return SolarGeneratorTileEntity.class;
	}
	
	public EnumFacing[] getPossibleNeighborsPositions(SolarGeneratorTileEntity tile) {
		return EnumFacing.HORIZONTALS;
		
	}	
	
}
