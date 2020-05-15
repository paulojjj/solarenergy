package paulojjj.solarenergy.jei;

import java.awt.Color;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.recipes.EnergyAssemblerRecipe;

public class EnergyAssemblerRecipeWrapper {
	
	private EnergyAssemblerRecipe recipe;
	private String energyNeededString;
	
	public EnergyAssemblerRecipeWrapper(EnergyAssemblerRecipe recipe) {
		super();
		this.recipe = recipe;
		energyNeededString = EnergyFormatter.format(recipe.getEnergyNeeded());
	}

	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.getInput(), 1));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
	}
	
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawString(energyNeededString, 28, 31, Color.gray.getRGB());
	}

}
