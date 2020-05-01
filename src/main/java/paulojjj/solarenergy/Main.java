package paulojjj.solarenergy;

import java.io.File;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.proxy.CommonProxy;
import paulojjj.solarenergy.proxy.Proxy;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.registry.Items;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
	public static final String MODID = "solarenergy";
	public static final String NAME = "Solar Energy";
	public static final String VERSION = "1.0";

	public static SoundEvent sound = null;
	
	@Instance(value = MODID)
	public static Main instance;
	
	@SidedProxy(clientSide = "paulojjj.solarenergy.proxy.ClientProxy", serverSide = "paulojjj.solarenergy.proxy.CommonProxy")
	private static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		File configFile = event.getSuggestedConfigurationFile();
		Configuration cfg = new Configuration(configFile);
		Config.init(cfg);

		proxy.registerAssets();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerGuiHandler();
		proxy.registerHandlers();
		
		RecipeHandler.addEnergyAssemblerRecipe(net.minecraft.init.Items.IRON_INGOT, new ItemStack(Items.BASIC_ENERGY_CORE.getItem(), 1), 100000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.BASIC_ENERGY_CORE.getItem(), new ItemStack(Items.REGULAR_ENERGY_CORE.getItem(), 1), 1000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.REGULAR_ENERGY_CORE.getItem(), new ItemStack(Items.INTERMEDIATE_ENERGY_CORE.getItem(), 1), 10000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.INTERMEDIATE_ENERGY_CORE.getItem(), new ItemStack(Items.ADVANCED_ENERGY_CORE.getItem(), 1), 100000000);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ADVANCED_ENERGY_CORE.getItem(), new ItemStack(Items.ELITE_ENERGY_CORE.getItem(), 1), 1000000000d);
		RecipeHandler.addEnergyAssemblerRecipe(Items.ELITE_ENERGY_CORE.getItem(), new ItemStack(Items.ULTIMATE_ENERGY_CORE.getItem(), 1), 10000000000d);
		
		PacketManager.init();
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		proxy.registerCommands();
	}
	
	public static Proxy getProxy() {
		return proxy;
	}

}
