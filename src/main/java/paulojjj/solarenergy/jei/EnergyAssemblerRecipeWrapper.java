package paulojjj.solarenergy.jei;

import java.awt.Color;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.recipes.EnergyAssemblerRecipe;

public class EnergyAssemblerRecipeWrapper implements IRecipeWrapper {
	
	private EnergyAssemblerRecipe recipe;
	private String energyNeededString;
	
	public EnergyAssemblerRecipeWrapper(EnergyAssemblerRecipe recipe) {
		super();
		this.recipe = recipe;
		energyNeededString = EnergyFormatter.format(recipe.getEnergyNeeded());
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.getInput(), 1));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawString(energyNeededString, 28, 31, Color.gray.getRGB());
	}

}
