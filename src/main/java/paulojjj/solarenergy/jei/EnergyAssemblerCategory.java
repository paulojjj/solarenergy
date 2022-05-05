package paulojjj.solarenergy.jei;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.gui.EnergyAssemblerGui;
import paulojjj.solarenergy.recipes.EnergyAssemblerRecipe;
import paulojjj.solarenergy.registry.Items;

public class EnergyAssemblerCategory implements IRecipeCategory<EnergyAssemblerRecipe> {

	private IGuiHelper guiHelper;
	private IDrawableAnimated gauge;
	private IDrawable icon;
	
	public EnergyAssemblerCategory(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
		IDrawableStatic gauge = guiHelper.createDrawable(EnergyAssemblerGui.ASSET_RESOURCE, 176, 0, 11, 23);
		this.gauge = guiHelper.createAnimatedDrawable(gauge, 40, StartDirection.TOP, false);
		this.icon = guiHelper.createDrawableIngredient(new ItemStack(Items.BASIC_SOLAR_GENERATOR.getItem()));
	}
	
	@Override
	public Component getTitle() {
		return new TextComponent(I18n.get("block.solarenergy.energy_assembler"));
	}

	@Override
	public IDrawable getBackground() {
		return guiHelper.createDrawable(EnergyAssemblerGui.ASSET_RESOURCE, 22, 7, 100, 70);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, EnergyAssemblerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 3, 1);
		guiItemStacks.init(1, false, 3, 50);
		
		guiItemStacks.set(ingredients);
	}
	
	@Override
	public void draw(EnergyAssemblerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		gauge.draw(matrixStack, 7, 24);
		
		String energyNeededString = EnergyFormatter.format(recipe.getEnergyNeeded());
		Minecraft mc = Minecraft.getInstance(); 
		mc.font.draw(matrixStack, energyNeededString, 28, 31, Color.gray.getRGB());
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEIModPlugin.ENERGY_ASSMBLER_UID;
	}

	@Override
	public Class<? extends EnergyAssemblerRecipe> getRecipeClass() {
		return EnergyAssemblerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(EnergyAssemblerRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.getInput(), 1));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
	}

}
