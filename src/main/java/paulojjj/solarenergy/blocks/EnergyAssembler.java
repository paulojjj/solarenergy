package paulojjj.solarenergy.blocks;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssembler extends BaseBlock {
	
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    
	public EnergyAssembler() {
		super();
		configBuilder()
			.resistance(3.5f)
			.with(ACTIVE, false)
			.gui(GUI.ENERGY_ASSEMBLER)
			.createTileEntity((x) -> new EnergyAssemblerTileEntity())
			.init();
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
