package paulojjj.solarenergy.jei;

import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.registry.Blocks;

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
		
		List<EnergyAssemblerRecipeWrapper> recipes = RecipeHandler.getEnergyAssemblerRecipes().values().stream().map(x -> new EnergyAssemblerRecipeWrapper(x)).collect(Collectors.toList());
		
		registry.addRecipes(recipes, ENERGY_ASSMBLER_UID);
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(Blocks.ENERGY_ASSEMBLER.getItemBlock().getBlock()), ENERGY_ASSMBLER_UID);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}
	

}
