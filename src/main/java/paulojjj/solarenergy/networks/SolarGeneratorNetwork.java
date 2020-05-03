package paulojjj.solarenergy.networks;

import java.util.Set;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGeneratorNetwork extends BaseNetwork<SolarGeneratorTileEntity> {
	
	public SolarGeneratorNetwork() {
	}

	@Override
	public Class<SolarGeneratorTileEntity> getTileClass() {
		return SolarGeneratorTileEntity.class;
	}
	
	public EnumFacing[] getPossibleNeighborsPositions(SolarGeneratorTileEntity tile) {
		return EnumFacing.HORIZONTALS;
	}
	
	@Override
	protected Set<IEnergyStorage> getConsumers() {
		return getStorages((t, f, s) -> (f != EnumFacing.UP && s.canReceive()));
	}
	
}
