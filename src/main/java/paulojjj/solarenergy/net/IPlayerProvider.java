package paulojjj.solarenergy.net;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public interface IPlayerProvider {

	Player getPlayer(NetworkEvent.Context ctx);
	
}
