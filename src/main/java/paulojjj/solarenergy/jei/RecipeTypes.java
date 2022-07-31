package paulojjj.solarenergy.jei;

import mezz.jei.api.recipe.RecipeType;
import paulojjj.solarenergy.recipes.EnergyAssemblerRecipe;

public class RecipeTypes {
	
	public static final RecipeType<EnergyAssemblerRecipe> ENERGY_ASSEMBLER = new RecipeType<>(JEIModPlugin.ENERGY_ASSMBLER_UID, EnergyAssemblerRecipe.class);

}
