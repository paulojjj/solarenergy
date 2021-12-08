package paulojjj.solarenergy;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import paulojjj.solarenergy.registry.Blocks;

public class ModCreativeTab extends ItemGroup {

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
			return new ItemStack(Blocks.BASIC_SOLAR_GENERATOR.getItemBlock(), 1);
		}
}
