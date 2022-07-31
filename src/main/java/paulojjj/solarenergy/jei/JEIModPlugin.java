package paulojjj.solarenergy.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.recipes.EnergyAssemblerRecipe;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.registry.Items;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class JEIModPlugin implements IModPlugin {
	
	private static final ResourceLocation ID = new ResourceLocation(Main.MODID, "main");
	public static final ResourceLocation ENERGY_ASSMBLER_UID = new ResourceLocation(Main.MODID, "energy_assembler");
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IModPlugin.super.registerCategories(registry);
		
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		registry.addRecipeCategories(new EnergyAssemblerCategory(guiHelper));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		IModPlugin.super.registerRecipes(registry);
		
		List<EnergyAssemblerRecipe> recipes = new ArrayList<>(RecipeHandler.getEnergyAssemblerRecipes().values());
		registry.addRecipes(RecipeTypes.ENERGY_ASSEMBLER, recipes);
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(Blocks.ENERGY_ASSEMBLER.getBlock()), RecipeTypes.ENERGY_ASSEMBLER);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}	
}
