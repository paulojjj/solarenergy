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
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryItemBlock extends ItemBlock {
	
	public BatteryItemBlock(Tier tier) {
		super(new Battery(tier));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		double energy = 0;
		double capacity = 0;
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			energy = nbt.getDouble("energy");
			capacity = nbt.getDouble("capacity");
		}
		else {
			BatteryTileEntity te = (BatteryTileEntity)this.getBlock().createTileEntity(worldIn, this.getBlock().getDefaultState());
			energy = te.getUltraEnergyStored();
			capacity = te.getMaxUltraEnergyStored();
		}
		tooltip.add(String.format("%s: %s", I18n.format(Main.MODID + ".stored_energy"), EnergyFormatter.format(energy)));
		tooltip.add(String.format("%s: %s", I18n.format(Main.MODID + ".capacity"), EnergyFormatter.format(capacity)));
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTagCompound() != null;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			double energy = nbt.getDouble("energy");
			double capacity = nbt.getDouble("capacity");
			return 1 - (energy/capacity);
		}
		return super.getDurabilityForDisplay(stack);
	}	
	
}
