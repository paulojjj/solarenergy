package paulojjj.solarenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssembler extends Block {
	
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    
	public EnergyAssembler() {
		super(Material.ROCK);
		setResistance(3.5f);
		setHardness(3.5f);
		setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
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
		return new EnergyAssemblerTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer( this, new IProperty[] {ACTIVE});

	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(ACTIVE, meta != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVE) ? 1 : 0;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) {
			return true;
		}
		return GuiHandler.openGui(playerIn, worldIn, GUI.ENERGY_ASSEMBLER, pos);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isRemote) {
			EnergyAssemblerTileEntity te = (EnergyAssemblerTileEntity) worldIn.getTileEntity(pos);
			IItemHandler itemHandler = te.getPlayerHandler();
			for(int i=0; i<itemHandler.getSlots(); i++) {
				ItemStack stack = itemHandler.getStackInSlot(i);
				if(stack != ItemStack.EMPTY) {
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}
	    super.breakBlock(worldIn, pos, state);
	}
	

}
