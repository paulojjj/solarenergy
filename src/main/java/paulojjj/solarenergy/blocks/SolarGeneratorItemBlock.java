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
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGeneratorItemBlock extends BlockItem {
	
	public SolarGeneratorItemBlock(Tier tier) {
		super(new SolarGenerator(tier), new Item.Properties().group(ModCreativeTab.getInstance()));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		SolarGeneratorTileEntity te = (SolarGeneratorTileEntity)this.getBlock().createTileEntity(this.getBlock().getDefaultState(), worldIn);
		double production = te.getMaxProduction();
		String str = String.format("%s: %s/t", I18n.format(Main.MODID + ".produces"), EnergyFormatter.format(production));
		tooltip.add(new StringTextComponent(str));
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
