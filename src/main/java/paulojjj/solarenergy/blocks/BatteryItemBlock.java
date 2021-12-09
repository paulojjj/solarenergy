package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.ModCreativeTab;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.tiles.BatteryTileEntity;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class BatteryItemBlock extends BlockItem {
	
	public BatteryItemBlock(Tier tier) {
		super(new Battery(tier), new Item.Properties().tab(ModCreativeTab.getInstance()));
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
		tooltip.add(new TextComponent(strEnergy));
		tooltip.add(new TextComponent(strCapacity));
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTag() != null;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		if(nbt != null) {
			double energy = nbt.getDouble(NBT.ENERGY);
			double capacity = nbt.getDouble(NBT.MAX_ENERGY);
			return 1 - (energy/capacity);
		}
		return super.getDurabilityForDisplay(stack);
	}	
	
}
