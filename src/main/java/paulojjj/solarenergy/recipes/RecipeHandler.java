package paulojjj.solarenergy.recipes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RecipeHandler {
	
	private static Map<Item, EnergyAssemblerRecipe> energyAssemblerRecipes;
	
	public static class ItemComparator implements Comparator<Item> {

		@Override
		public int compare(Item o1, Item o2) {
			return Item.getId(o1) - Item.getId(o2);
		}
		
	}
	
	static {
		energyAssemblerRecipes = new HashMap<>();
	}
	
	public static void addEnergyAssemblerRecipe(Item input, ItemStack output, double energyNeeded) {
		energyAssemblerRecipes.put(input, new EnergyAssemblerRecipe(input, output, energyNeeded));
	}
	
	public static void addEnergyAssemblerRecipe(Block input, ItemStack output, double energyNeeded) {
		addEnergyAssemblerRecipe(Item.BY_BLOCK.getOrDefault(input, Items.AIR), output, energyNeeded);
	}
	
	public static Map<Item, EnergyAssemblerRecipe> getEnergyAssemblerRecipes() {
		return energyAssemblerRecipes;
	}

	public static Optional<EnergyAssemblerRecipe> getEnergyAssemblerRecipe(Item input) {
		return Optional.ofNullable(energyAssemblerRecipes.get(input));
	}

}
