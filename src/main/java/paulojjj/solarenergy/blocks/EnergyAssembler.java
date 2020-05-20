package paulojjj.solarenergy.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssembler extends BaseBlock {
	
	public EnergyAssembler() {
		super();
		configBuilder()
			.resistance(3.5f)
			.gui(GUI.ENERGY_ASSEMBLER)
			.createTileEntity((x) -> new EnergyAssemblerTileEntity())
			.blockLayer(BlockRenderLayer.CUTOUT_MIPPED)
			.init();
	}
	
	protected List<ItemStack> getItemsDropped(EnergyAssemblerTileEntity te) {
		List<ItemStack> drops = new ArrayList<>();
		IItemHandler itemHandler = te.getPlayerHandler();

		for(int i=0; i<itemHandler.getSlots(); i++) {
			ItemStack stack = itemHandler.getStackInSlot(i);
			if(stack != ItemStack.EMPTY) {
				drops.add(stack);
			}
		}
		Item assembling = te.getAssemblingItem();
		if(assembling != null && assembling != Items.AIR) {
			drops.add(new ItemStack(assembling));
		}
		return drops;
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isRemote) {
			EnergyAssemblerTileEntity te = (EnergyAssemblerTileEntity) worldIn.getTileEntity(pos);
			for(ItemStack stack : getItemsDropped(te)) {
				if(stack != ItemStack.EMPTY) {
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}
	    super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public boolean isFullBlock(IBlockState state) {
		return true;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
}
