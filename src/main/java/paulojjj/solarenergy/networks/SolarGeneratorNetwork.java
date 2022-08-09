package paulojjj.solarenergy.networks;

import java.util.Set;

import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGeneratorNetwork extends BaseNetwork<SolarGeneratorTileEntity> {
	
	private static Direction[] POSSIBLE_NEIGHBORS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
	
	public SolarGeneratorNetwork() {
	}

	@Override
	public Class<SolarGeneratorTileEntity> getTileClass() {
		return SolarGeneratorTileEntity.class;
	}
	
	public Direction[] getPossibleNeighborsPositions(SolarGeneratorTileEntity tile) {
		return POSSIBLE_NEIGHBORS;
	}
	
	@Override
	protected Set<IEnergyStorage> getConsumers() {
		return getStorages((t, f, s) -> (f != Direction.UP && s.canReceive()));
	}
	
}
