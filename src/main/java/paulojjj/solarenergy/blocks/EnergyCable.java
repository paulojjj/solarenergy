package paulojjj.solarenergy.blocks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.registry.Items;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

public class EnergyCable extends EnergyNetworkBlock<EnergyCableTileEntity> {
	
	private static Map<Boxes, VoxelShape> boxShapes = new HashMap<>();

	public enum Boxes {
		DOWN(new AABB(0.33, 0.0, 0.33, 0.67, 0.33, 0.67)),
		UP(new AABB(0.33, 0.67, 0.33, 0.67, 1.0, 0.67)), 
		NORTH(new AABB(0.33, 0.33, 0.0, 0.67, 0.67, 0.33)),
		SOUTH(new AABB(0.33, 0.33, 0.67, 0.67, 0.67, 1.0)),
		WEST(new AABB(0.0, 0.33, 0.33, 0.33, 0.67, 0.67)),
		EAST(new AABB(0.67, 0.33, 0.33, 1.0, 0.67, 0.67)),
		CENTER(new AABB(0.33, 0.33, 0.33, 0.67, 0.67, 0.67));

		private AABB bb;

		private Boxes(AABB bb) {
			this.bb = bb;
		}

		public AABB getBoundingBox() {
			return bb;
		}

		public static Boxes getBox(Direction facing) {
			return Boxes.values()[facing.ordinal()];
		}
	}

	public EnergyCable() {
		super(propertiesBuilder().resistance(1.0f).hardness(1.0f).notSolid());
		configBuilder()
			.guiContainer(Containers.ENERGY_CABLE)
			.createTileEntity((x, y) -> new EnergyCableTileEntity(x, y))
			.renderType(RenderShape.INVISIBLE)
			.init();
	}
	
	protected VoxelShape getVoxelShape(Boxes box) {
		VoxelShape shape = boxShapes.get(box);
		if(shape == null) {
			AABB bb = box.getBoundingBox();
			shape = box(bb.minX * 16, bb.minY * 16, bb.minZ * 16, bb.maxX * 16, bb.maxY * 16, bb.maxZ * 16);
			boxShapes.put(box, shape);
		}
		return shape;
	}
	
	public VoxelShape getShape(BlockGetter world, BlockPos pos) {
		EnergyCableTileEntity te = (EnergyCableTileEntity)world.getBlockEntity(pos);
		
		if(te == null) {
			return Shapes.empty();
		}
		
		VoxelShape shape = getVoxelShape(Boxes.CENTER);
		for(Direction facing : Direction.values()) {
			if(te.hasStorage(facing)) {
				Boxes box = Boxes.getBox(facing);
				VoxelShape boxShape = getVoxelShape(box);
				shape = Shapes.joinUnoptimized(shape, boxShape, BooleanOp.OR);
			}
		}
		return shape;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return getShape(worldIn, pos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos,
			CollisionContext context) {
		return getShape(worldIn, pos);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
			InteractionHand handIn, BlockHitResult hit) {
		if(player.getMainHandItem().getItem() == Items.ENERGY_CABLE.getItem()) {
			return InteractionResult.PASS;
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
}
