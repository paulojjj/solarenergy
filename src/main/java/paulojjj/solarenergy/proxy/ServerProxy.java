package paulojjj.solarenergy.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerProxy extends CommonProxy {
	
	protected static long tick = 0;	
	protected static Object tickLock = new Object();
	protected static int waitingCount = 0;
	
	public ServerProxy() {
		System.out.println("Server proxy started");
	}
	
	@Override
	public void registerAssets() {
		super.registerAssets();
	}

	@Override
	public void registerHandlers() {
		super.registerHandlers();
		MinecraftForge.EVENT_BUS.register(this);		
	}
	
	public static void waitNextTick() {
		/*synchronized(tickLock) {
			if(waitingCount > 1) {
				return;
			}
			waitingCount++;
			try {
				System.out.println("WAITING");
				tickLock.wait();
			} catch (InterruptedException e) {
			}
			waitingCount--;
		}*/
	}
	
	@SubscribeEvent
	public void serverTick(ServerTickEvent evt) {
		tick++;
		synchronized(tickLock) {
			System.out.println("NOTIFY");
			tickLock.notifyAll();
		}
	}
}
