package paulojjj.solarenergy.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssembler extends BaseBlock {
	
	public EnergyAssembler() {
		super(BaseBlock.propertiesBuilder().hardness(1.0f).resistance(3.5f).notSolid());
		configBuilder()
			.guiContainer(paulojjj.solarenergy.registry.Containers.ENERGY_ASSEMBLER)
			.renderLayer(RenderLayer.CUTOUT_MIPPED)
			.createTileEntity((x, y) -> new EnergyAssemblerTileEntity(x, y))
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
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(worldIn, pos, state, player);
		if(!worldIn.isClientSide) {
			EnergyAssemblerTileEntity te = (EnergyAssemblerTileEntity) worldIn.getBlockEntity(pos);
			for(ItemStack stack : getItemsDropped(te)) {
				if(stack != ItemStack.EMPTY) {
					Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}

	}
	
}
