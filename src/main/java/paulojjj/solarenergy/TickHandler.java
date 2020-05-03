package paulojjj.solarenergy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

public class TickHandler {
	
	private static long serverTick;
	private static long clientTick;
	
	@SubscribeEvent
	public static void serverTick(TickEvent evt) {
		if(evt.type == Type.SERVER && evt.phase == Phase.START) {
			serverTick++;
		}
		else if(evt.type == Type.CLIENT && evt.phase == Phase.START) {
			clientTick++;
		}
	}
	
	public static long getTick() {
		return serverTick == 0 ? clientTick : serverTick;
	}
}
