package paulojjj.solarenergy;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.registry.Items;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class CommonSubscribers {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void subscribeRecipes(FMLLoadCompleteEvent event) {
		RecipeHandler.addEnergyAssemblerRecipe(Items.BASIC_ENERGY_CORE.getItem(), new ItemStack(Items.REGULAR_ENERGY_CORE.getItem(), 1), 100000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.REGULAR_ENERGY_CORE.getItem(), new ItemStack(Items.INTERMEDIATE_ENERGY_CORE.getItem(), 1), 1000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.INTERMEDIATE_ENERGY_CORE.getItem(), new ItemStack(Items.ADVANCED_ENERGY_CORE.getItem(), 1), 10000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ADVANCED_ENERGY_CORE.getItem(), new ItemStack(Items.ELITE_ENERGY_CORE.getItem(), 1), 100000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ELITE_ENERGY_CORE.getItem(), new ItemStack(Items.ULTIMATE_ENERGY_CORE.getItem(), 1), 1000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(net.minecraft.world.item.Items.IRON_INGOT, new ItemStack(Items.LEAD_INGOT.getItem(), 1), 1000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ULTIMATE_ENERGY_CORE.getItem(), new ItemStack(Items.BASIC_DENSE_ENERGY_CORE.getItem(), 1), 10000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.BASIC_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.REGULAR_DENSE_ENERGY_CORE.getItem(), 1), 100000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.REGULAR_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.INTERMEDIATE_DENSE_ENERGY_CORE.getItem(), 1), 1000000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.INTERMEDIATE_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.ADVANCED_DENSE_ENERGY_CORE.getItem(), 1), 10000000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ADVANCED_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.ELITE_DENSE_ENERGY_CORE.getItem(), 1), 100000000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ELITE_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.ULTIMATE_DENSE_ENERGY_CORE.getItem(), 1), 1000000000000000d);
		
	}

}
