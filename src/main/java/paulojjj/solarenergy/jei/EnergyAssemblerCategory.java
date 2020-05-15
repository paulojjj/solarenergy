package paulojjj.solarenergy.jei;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import paulojjj.solarenergy.gui.EnergyAssemblerGui;
import paulojjj.solarenergy.registry.Blocks;

public class EnergyAssemblerCategory implements IRecipeCategory<EnergyAssemblerRecipeWrapper> {

	private IGuiHelper guiHelper;
	private IDrawableAnimated gauge;
	private IDrawable icon;
	
	public EnergyAssemblerCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
		IDrawableStatic gauge = guiHelper.createDrawable(EnergyAssemblerGui.ASSET_RESOURCE, 176, 0, 11, 23);
		this.gauge = guiHelper.createAnimatedDrawable(gauge, 40, StartDirection.TOP, false);
		this.icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.BASIC_SOLAR_GENERATOR.getItemBlock()));
	}
	
	@Override
	public String getTitle() {
		return I18n.format("tile.energy_assembler.name");
	}

	@Override
	public IDrawable getBackground() {
		return guiHelper.createDrawable(EnergyAssemblerGui.ASSET_RESOURCE, 22, 7, 100, 70);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, EnergyAssemblerRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 3, 1);
		guiItemStacks.init(1, false, 3, 50);
		
		guiItemStacks.set(ingredients);
	}
	
	@Override
	public void draw(EnergyAssemblerRecipeWrapper recipe, double mouseX, double mouseY) {
		gauge.draw(7, 24);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIModPlugin.ENERGY_ASSMBLER_UID;
	}

	@Override
	public Class<? extends EnergyAssemblerRecipeWrapper> getRecipeClass() {
		return EnergyAssemblerRecipeWrapper.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(EnergyAssemblerRecipeWrapper recipe, IIngredients ingredients) {
		recipe.getIngredients(ingredients);
	}

}
