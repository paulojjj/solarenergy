package paulojjj.solarenergy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import paulojjj.solarenergy.registry.Blocks;

public class ModCreativeTab extends CreativeTabs {

		private static ModCreativeTab instance;

		public static ModCreativeTab getInstance() {
			if(instance == null) {
				instance = new ModCreativeTab();
			}
			return instance;
		}
		
		private ModCreativeTab() {
			super(Main.MODID);
		}

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Blocks.BASIC_SOLAR_GENERATOR.getItemBlock(), 1);
		}

}
