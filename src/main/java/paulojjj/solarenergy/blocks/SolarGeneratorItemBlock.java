package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGeneratorItemBlock extends ItemBlock {
	
	public SolarGeneratorItemBlock(Tier tier) {
		super(new SolarGenerator(tier));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		SolarGeneratorTileEntity te = (SolarGeneratorTileEntity)this.getBlock().createTileEntity(worldIn, this.getBlock().getDefaultState());
		double production = te.getMaxProduction();
		tooltip.add(String.format("%s: %s/t", I18n.format(Main.MODID + ".produces"), EnergyFormatter.format(production)));
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTagCompound() != null;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			double energy = nbt.getDouble(NBT.ENERGY);
			double capacity = nbt.getDouble(NBT.MAX_ENERGY);
			return 1 - (energy/capacity);
		}
		return super.getDurabilityForDisplay(stack);
	}	

}
