package paulojjj.solarenergy.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import paulojjj.solarenergy.tiles.EnergyNetworkTileEntity;

public class EnergyNetworkBlock<T extends EnergyNetworkTileEntity> extends BaseBlock {

	public EnergyNetworkBlock(Block.Properties builder) {
		super(builder);
	}

	public EnergyNetworkBlock(PropertiesBuilder builder) {
		super(builder);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if(!worldIn.isClientSide) {
			@SuppressWarnings("unchecked")
			T tileEntity = (T)worldIn.getBlockEntity(pos);
			tileEntity.onNeighborChanged(fromPos);
		}
	}

}
