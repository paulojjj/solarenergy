package paulojjj.solarenergy.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import paulojjj.solarenergy.networks.EnergyCableNetwork;
import paulojjj.solarenergy.registry.TileEntities;

public class EnergyCableTileEntity extends EnergyNetworkTileEntity {

	public EnergyCableTileEntity(BlockPos pos, BlockState state) {
		super(TileEntities.ENERGY_CABLE.getType(), pos, state);
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
