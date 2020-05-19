package paulojjj.solarenergy.blocks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import paulojjj.solarenergy.registry.GUI;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

public class EnergyCable extends EnergyNetworkBlock<EnergyCableTileEntity> {
	
	private static Map<Boxes, VoxelShape> boxShapes = new HashMap<>();

	public enum Boxes {
		DOWN(new AxisAlignedBB(0.33, 0.0, 0.33, 0.67, 0.33, 0.67)),
		UP(new AxisAlignedBB(0.33, 0.67, 0.33, 0.67, 1.0, 0.67)), 
		NORTH(new AxisAlignedBB(0.33, 0.33, 0.0, 0.67, 0.67, 0.33)),
		SOUTH(new AxisAlignedBB(0.33, 0.33, 0.67, 0.67, 0.67, 1.0)),
		WEST(new AxisAlignedBB(0.0, 0.33, 0.33, 0.33, 0.67, 0.67)),
		EAST(new AxisAlignedBB(0.67, 0.33, 0.33, 1.0, 0.67, 0.67)),
		CENTER(new AxisAlignedBB(0.33, 0.33, 0.33, 0.67, 0.67, 0.67));

		private AxisAlignedBB bb;

		private Boxes(AxisAlignedBB bb) {
			this.bb = bb;
		}

		public AxisAlignedBB getBoundingBox() {
			return bb;
		}

		public static Boxes getBox(Direction facing) {
			return Boxes.values()[facing.ordinal()];
		}
	}

	public EnergyCable() {
		super(propertiesBuilder().resistance(1.0f).hardness(1.0f).notSolid());
		configBuilder()
			.gui(GUI.ENERGY_CABLE)
			.createTileEntity((x) -> new EnergyCableTileEntity())
			.renderType(BlockRenderType.INVISIBLE)
			.init();
	}
	
	protected VoxelShape getVoxelShape(Boxes box) {
		VoxelShape shape = boxShapes.get(box);
		if(shape == null) {
			AxisAlignedBB bb = box.getBoundingBox();
			shape = makeCuboidShape(bb.minX * 16, bb.minY * 16, bb.minZ * 16, bb.maxX * 16, bb.maxY * 16, bb.maxZ * 16);
			boxShapes.put(box, shape);
		}
		return shape;
	}

	public VoxelShape getShape(IBlockReader world, BlockPos pos) {
		EnergyCableTileEntity te = (EnergyCableTileEntity)world.getTileEntity(pos);
		
		if(te == null) {
			return VoxelShapes.empty();
		}
		
		VoxelShape shape = getVoxelShape(Boxes.CENTER);
		for(Direction facing : Direction.values()) {
			if(te.hasStorage(facing)) {
				Boxes box = Boxes.getBox(facing);
				VoxelShape boxShape = getVoxelShape(box);
				shape = VoxelShapes.combine(shape, boxShape, IBooleanFunction.OR);
			}
		}
		return shape;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return getShape(worldIn, pos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return getShape(worldIn, pos);
	}
	
}
