package paulojjj.solarenergy.jei;

import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.registry.Blocks;

@JEIPlugin
@SideOnly(Side.CLIENT)
public class JEIModPlugin implements IModPlugin {
	
	public static final String ENERGY_ASSMBLER_UID = "solarenergy.energy_assembler";
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IModPlugin.super.registerCategories(registry);
		
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		registry.addRecipeCategories(new EnergyAssemblerCategory(guiHelper));
	}
	
	@Override
	public void register(IModRegistry registry) {
		IModPlugin.super.register(registry);
		
		List<EnergyAssemblerRecipeWrapper> recipes = RecipeHandler.getEnergyAssemblerRecipes().values().stream().map(x -> new EnergyAssemblerRecipeWrapper(x)).collect(Collectors.toList());
		
		registry.addRecipes(recipes, ENERGY_ASSMBLER_UID);
		registry.addRecipeCatalyst(new ItemStack(Blocks.ENERGY_ASSEMBLER.getItemBlock().getBlock()), ENERGY_ASSMBLER_UID);
	}
	

}
