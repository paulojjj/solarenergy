package paulojjj.solarenergy.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssembler extends BaseBlock {
	
	public EnergyAssembler() {
		super(BaseBlock.propertiesBuilder().hardness(1.0f).resistance(3.5f).notSolid());
		configBuilder()
			.guiContainer(Containers.ENERGY_ASSEMBLER)
			.renderLayer(RenderLayer.CUTOUT_MIPPED)
			.createTileEntity((x) -> new EnergyAssemblerTileEntity())
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
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.playerWillDestroy(worldIn, pos, state, player);
		if(!worldIn.isClientSide) {
			EnergyAssemblerTileEntity te = (EnergyAssemblerTileEntity) worldIn.getBlockEntity(pos);
			for(ItemStack stack : getItemsDropped(te)) {
				if(stack != ItemStack.EMPTY) {
					InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}

	}
	
}
