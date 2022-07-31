package paulojjj.solarenergy.jei;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
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
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.BASIC_SOLAR_GENERATOR.getItem()));
	}
	
	@Override
	public Component getTitle() {
		return Component.literal(I18n.get("block.solarenergy.energy_assembler"));
	}

	@Override
	public IDrawable getBackground() {
		return guiHelper.createDrawable(EnergyAssemblerGui.ASSET_RESOURCE, 22, 7, 100, 70);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, EnergyAssemblerRecipe recipe, IFocusGroup features) {
		builder.addSlot(RecipeIngredientRole.INPUT, 3, 1)
			.addItemStack(new ItemStack(recipe.getInput(), 1));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 3, 50)
			.addItemStack(recipe.getOutput());
	}
	
	@Override
	public void draw(EnergyAssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		gauge.draw(stack, 7, 24);
		
		String energyNeededString = EnergyFormatter.format(recipe.getEnergyNeeded());
		Minecraft mc = Minecraft.getInstance(); 
		mc.font.draw(stack, energyNeededString, 28, 31, Color.gray.getRGB());
	}
	
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public RecipeType<EnergyAssemblerRecipe> getRecipeType() {
		return RecipeTypes.ENERGY_ASSEMBLER;
	}
	
	

}
