package paulojjj.solarenergy.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

public class EnergyCable extends Block {
	
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
		super(Material.ROCK);
		setResistance(1.0f);
		setHardness(3.5f);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new EnergyCableTileEntity();
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		super.onBlockExploded(world, pos, explosion);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.getHeldItemMainhand().getItem() != Blocks.ENERGY_CABLE.getItemBlock()) {
			return GuiHandler.openGui(playerIn, worldIn, GUI.ENERGY_CABLE, pos);
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote) {
			EnergyCableTileEntity tileEntity = (EnergyCableTileEntity)worldIn.getTileEntity(pos);
			tileEntity.onNeighborChanged(fromPos);
		}
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
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

    @Nonnull
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
	
	
	
}
