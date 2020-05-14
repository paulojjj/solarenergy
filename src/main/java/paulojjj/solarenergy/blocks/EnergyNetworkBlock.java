package paulojjj.solarenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulojjj.solarenergy.tiles.EnergyNetworkTileEntity;

public class EnergyNetworkBlock<T extends EnergyNetworkTileEntity> extends BaseBlock {

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote) {
			@SuppressWarnings("unchecked")
			T tileEntity = (T)worldIn.getTileEntity(pos);
			tileEntity.onNeighborChanged(fromPos);
		}
	}
	
}
