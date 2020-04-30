package paulojjj.solarenergy.recipes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeHandler {
	
	private static Map<Item, EnergyAssemblerRecipe> energyAssemblerRecipes;
	
	public static class ItemComparator implements Comparator<Item> {

		@Override
		public int compare(Item o1, Item o2) {
			return Item.getIdFromItem(o1) - Item.getIdFromItem(o2);
		}
		
	}
	
	static {
		energyAssemblerRecipes = new HashMap<>();
	}
	
	public static void addEnergyAssemblerRecipe(Item input, ItemStack output, double energyNeeded) {
		energyAssemblerRecipes.put(input, new EnergyAssemblerRecipe(input, output, energyNeeded));
	}
	
	public static void addEnergyAssemblerRecipe(Block input, ItemStack output, double energyNeeded) {
		addEnergyAssemblerRecipe(Item.getItemFromBlock(input), output, energyNeeded);
	}
	
	public static Map<Item, EnergyAssemblerRecipe> getEnergyAssemblerRecipes() {
		return energyAssemblerRecipes;
	}

	public static Optional<EnergyAssemblerRecipe> getEnergyAssemblerRecipe(Item input) {
		return Optional.ofNullable(energyAssemblerRecipes.get(input));
	}

}
