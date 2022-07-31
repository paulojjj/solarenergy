package paulojjj.solarenergy;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.proxy.ClientProxy;
import paulojjj.solarenergy.proxy.CommonProxy;
import paulojjj.solarenergy.proxy.Proxy;

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
		
		PacketManager.init();
	}
	
	
	public static Proxy getProxy() {
		return proxy;
	}

}
