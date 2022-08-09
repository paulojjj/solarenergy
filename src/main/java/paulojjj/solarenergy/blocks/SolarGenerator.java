package paulojjj.solarenergy.blocks;

import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGenerator extends EnergyNetworkBlock<SolarGeneratorTileEntity> {
	
	public static final VoxelShape SHAPE = box(0, 0, 0, 16, 4, 16);

	public SolarGenerator(Tier tier) {
		super(propertiesBuilder().resistance(1.0f).notSolid());
		configBuilder()
		.guiContainer(Containers.SOLAR_GENERATOR)
		.createTileEntity((x, y) -> new SolarGeneratorTileEntity(tier, x, y))
		.renderType(RenderShape.MODEL)
		.init();
	}
	
	public VoxelShape getShape() {
		return SHAPE;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return getShape();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos,
			CollisionContext context) {
		return getShape();
	}
	
	@Override
	public int getLightBlock(BlockState p_60585_, BlockGetter p_60586_, BlockPos p_60587_) {
		return 1;
	}
}
