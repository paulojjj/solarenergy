package paulojjj.solarenergy.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.registry.GUI;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGenerator extends EnergyNetworkBlock<SolarGeneratorTileEntity> {

	public SolarGenerator(Tier tier) {
		super(propertiesBuilder().resistance(1.0f));
		configBuilder()
		.gui(GUI.SOLAR_GENERATOR)
		.createTileEntity((x) -> new SolarGeneratorTileEntity(tier))
		.renderType(BlockRenderType.INVISIBLE)
		.init();
	}
	
	public VoxelShape getShape() {
		return makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return getShape();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return getShape();
	}
	
}
