package paulojjj.solarenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulojjj.solarenergy.tiles.EnergyNetworkTileEntity;

public class EnergyNetworkBlock<T extends EnergyNetworkTileEntity> extends BaseBlock {

	public EnergyNetworkBlock(Block.Properties builder) {
		super(builder);
	}

	public EnergyNetworkBlock(PropertiesBuilder builder) {
		super(builder);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if(!worldIn.isClientSide) {
			@SuppressWarnings("unchecked")
			T tileEntity = (T)worldIn.getBlockEntity(pos);
			tileEntity.onNeighborChanged(fromPos);
		}
	}

}
