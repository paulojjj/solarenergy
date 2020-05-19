package paulojjj.solarenergy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import paulojjj.solarenergy.registry.GUI;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssembler extends BaseBlock {
	
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    
	public EnergyAssembler() {
		super(BaseBlock.propertiesBuilder().resistance(3.5f));
		configBuilder()
			.property(ACTIVE)
			.gui(GUI.ENERGY_ASSEMBLER)
			.createTileEntity((x) -> new EnergyAssemblerTileEntity())
			.init();
	}
	

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);
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
	}

}
