package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.ModCreativeTab;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryItemBlock extends BlockItem {

	public BatteryItemBlock(Tier tier) {
		super(Blocks.getBattery(tier).getBlock(), new Item.Properties().tab(ModCreativeTab.getInstance()));
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		double energy = 0;
		double capacity = 0;
		CompoundTag nbt = stack.getTag();
		if(nbt != null) {
			energy = nbt.getDouble(NBT.ENERGY);
			capacity = nbt.getDouble(NBT.MAX_ENERGY);
		}
		else {
			Battery block = (Battery)this.getBlock();
			BatteryTileEntity te = (BatteryTileEntity)block.newBlockEntity(new BlockPos(0,0,0), block.defaultBlockState());
			energy = te.getUltraEnergyStored();
			capacity = te.getMaxUltraEnergyStored();
		}
		String strEnergy = String.format("%s: %s", I18n.get(Main.MODID + ".stored_energy"), EnergyFormatter.format(energy)); 
		String strCapacity = String.format("%s: %s", I18n.get(Main.MODID + ".capacity"), EnergyFormatter.format(capacity)); 
		tooltip.add(Component.literal(strEnergy));
		tooltip.add(Component.literal(strCapacity));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.getTag() != null;
	}
	
	protected float getChargePercent(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		if(nbt == null) {
			return 0;
		}
		
		double energy = nbt.getDouble(NBT.ENERGY);
		double capacity = nbt.getDouble(NBT.MAX_ENERGY);
		return (float)(energy/capacity);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		int pixels = Math.round(13*getChargePercent(stack));
		return pixels;
	}

	public int getBarColor(ItemStack stack) {
		return Mth.hsvToRgb(getChargePercent(stack) / 3.0F, 1.0F, 1.0F);
	}


}
