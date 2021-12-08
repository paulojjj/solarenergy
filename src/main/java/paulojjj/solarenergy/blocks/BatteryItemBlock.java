package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.ModCreativeTab;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryItemBlock extends BlockItem {
	
	public BatteryItemBlock(Tier tier) {
		super(new Battery(tier), new Item.Properties().tab(ModCreativeTab.getInstance()));
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		double energy = 0;
		double capacity = 0;
		CompoundNBT nbt = stack.getTag();
		if(nbt != null) {
			energy = nbt.getDouble(NBT.ENERGY);
			capacity = nbt.getDouble(NBT.MAX_ENERGY);
		}
		else {
			BatteryTileEntity te = (BatteryTileEntity)this.getBlock().createTileEntity(this.getBlock().defaultBlockState(), worldIn);
			energy = te.getUltraEnergyStored();
			capacity = te.getMaxUltraEnergyStored();
		}
		String strEnergy = String.format("%s: %s", I18n.get(Main.MODID + ".stored_energy"), EnergyFormatter.format(energy)); 
		String strCapacity = String.format("%s: %s", I18n.get(Main.MODID + ".capacity"), EnergyFormatter.format(capacity)); 
		tooltip.add(new StringTextComponent(strEnergy));
		tooltip.add(new StringTextComponent(strCapacity));
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTag() != null;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		CompoundNBT nbt = stack.getTag();
		if(nbt != null) {
			double energy = nbt.getDouble(NBT.ENERGY);
			double capacity = nbt.getDouble(NBT.MAX_ENERGY);
			return 1 - (energy/capacity);
		}
		return super.getDurabilityForDisplay(stack);
	}	
	
}
