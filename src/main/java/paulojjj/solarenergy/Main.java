package paulojjj.solarenergy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.proxy.ClientProxy;
import paulojjj.solarenergy.proxy.CommonProxy;
import paulojjj.solarenergy.proxy.Proxy;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.registry.Items;

@Mod(Main.MODID)
public class Main {
	public static final String MODID = "solarenergy";

	public static SoundEvent sound = null;
	
	public static Main instance;
	
	private static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	
	public Main() {
		instance = this;
		preInit();
		init();
		proxy.registerCommands();
	}
	
	public void preInit()
	{
		Config.init();

		proxy.init();
		proxy.registerAssets();
	}

	public void init()
	{
		proxy.registerHandlers();
		
		/*RecipeHandler.addEnergyAssemblerRecipe(Items.BASIC_ENERGY_CORE.getItem(), new ItemStack(Items.REGULAR_ENERGY_CORE.getItem(), 1), 100000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.REGULAR_ENERGY_CORE.getItem(), new ItemStack(Items.INTERMEDIATE_ENERGY_CORE.getItem(), 1), 1000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.INTERMEDIATE_ENERGY_CORE.getItem(), new ItemStack(Items.ADVANCED_ENERGY_CORE.getItem(), 1), 10000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ADVANCED_ENERGY_CORE.getItem(), new ItemStack(Items.ELITE_ENERGY_CORE.getItem(), 1), 100000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ELITE_ENERGY_CORE.getItem(), new ItemStack(Items.ULTIMATE_ENERGY_CORE.getItem(), 1), 1000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(net.minecraft.world.item.Items.IRON_INGOT, new ItemStack(Items.LEAD_INGOT.getItem(), 1), 1000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ULTIMATE_ENERGY_CORE.getItem(), new ItemStack(Items.BASIC_DENSE_ENERGY_CORE.getItem(), 1), 10000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.BASIC_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.REGULAR_DENSE_ENERGY_CORE.getItem(), 1), 100000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.REGULAR_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.INTERMEDIATE_DENSE_ENERGY_CORE.getItem(), 1), 1000000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.INTERMEDIATE_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.ADVANCED_DENSE_ENERGY_CORE.getItem(), 1), 10000000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ADVANCED_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.ELITE_DENSE_ENERGY_CORE.getItem(), 1), 100000000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ELITE_DENSE_ENERGY_CORE.getItem(), new ItemStack(Items.ULTIMATE_DENSE_ENERGY_CORE.getItem(), 1), 1000000000000000d);*/
		
		PacketManager.init();
	}
	
	
	public static Proxy getProxy() {
		return proxy;
	}

}
