package paulojjj.solarenergy;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
