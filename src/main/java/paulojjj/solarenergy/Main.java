package paulojjj.solarenergy;

import java.io.File;

import org.apache.logging.log4j.Logger;

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

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
	public static final String MODID = "solarenergy";
	public static final String NAME = "Solar Energy MOD";
	public static final String VERSION = "1.0";

	public static Logger logger;

	public static SoundEvent sound = null;
	
	@Instance(value = MODID)
	public static Main instance;
	
	@SidedProxy(clientSide = "paulojjj.solarenergy.proxy.ClientProxy", serverSide = "paulojjj.solarenergy.proxy.CommonProxy")
	private static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		File configFile = event.getSuggestedConfigurationFile();
		Configuration cfg = new Configuration(configFile);
		Config.init(cfg);

		proxy.registerBlocks();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerGuiHandler();
		proxy.registerHandlers();
		
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
