package paulojjj.solarenergy;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import paulojjj.solarenergy.registry.Items;

public class ModCreativeTab extends CreativeModeTab {

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
		public ItemStack makeIcon() {
			return new ItemStack(Items.BASIC_SOLAR_GENERATOR.getItem(), 1);
		}
}
