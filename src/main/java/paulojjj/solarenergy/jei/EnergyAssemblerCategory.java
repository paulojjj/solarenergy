package paulojjj.solarenergy.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.gui.EnergyAssemblerGui;

public class EnergyAssemblerCategory implements IRecipeCategory<IRecipeWrapper> {

	private IGuiHelper guiHelper;
	private IDrawableAnimated gauge;
	
	public EnergyAssemblerCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
		IDrawableStatic gauge = guiHelper.createDrawable(EnergyAssemblerGui.ASSET_RESOURCE, 176, 0, 11, 23);
		this.gauge = guiHelper.createAnimatedDrawable(gauge, 40, StartDirection.TOP, false);
	}
	
	@Override
	public String getUid() {
		return JEIModPlugin.ENERGY_ASSMBLER_UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tile.energy_assembler.name");
	}

	@Override
	public String getModName() {
		return Main.NAME;
	}

	@Override
	public IDrawable getBackground() {
		return guiHelper.createDrawable(EnergyAssemblerGui.ASSET_RESOURCE, 22, 7, 100, 70);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 3, 1);
		guiItemStacks.init(1, false, 3, 50);
		
		guiItemStacks.set(ingredients);
	}
	
	@Override
	public void drawExtras(Minecraft minecraft) {
		gauge.draw(minecraft, 7, 24);
	}

}
