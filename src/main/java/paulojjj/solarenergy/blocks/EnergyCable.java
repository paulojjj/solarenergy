package paulojjj.solarenergy.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

public class EnergyCable extends EnergyNetworkBlock<EnergyCableTileEntity> {

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

		public static Boxes getBox(EnumFacing facing) {
			return Boxes.values()[facing.ordinal()];
		}
	}

	public EnergyCable() {
		super();
		configBuilder()
			.resistance(1.0f)
			.hardness(1.0f)
			.gui(GUI.ENERGY_CABLE)
			.createTileEntity((x) -> new EnergyCableTileEntity())
			.init();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.getHeldItemMainhand().getItem() == Blocks.ENERGY_CABLE.getItemBlock()) {
			return false;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}	

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		EnergyCableTileEntity te = (EnergyCableTileEntity)worldIn.getTileEntity(pos);

		List<AxisAlignedBB> collisionBoxes = new ArrayList<>();
		collisionBoxes.add(Boxes.CENTER.getBoundingBox());
		for(EnumFacing facing : EnumFacing.values()) {
			if(te.hasStorage(facing)) {
				Boxes box = Boxes.getBox(facing);
				collisionBoxes.add(box.getBoundingBox());
			}
		}

		AxisAlignedBB offsetEntityBox = entityBox.offset(-pos.getX(), -pos.getY(), -pos.getZ());
		for(AxisAlignedBB collisionBox : collisionBoxes) {
			if(offsetEntityBox.intersects(collisionBox)) {
				collidingBoxes.add(collisionBox.offset(pos));
			}
		}
	}

	@Override
	@Deprecated
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

}
