package paulojjj.solarenergy.net;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ServerPlayerProvider implements IPlayerProvider {

	@Override
	public Player getPlayer(NetworkEvent.Context ctx) {
        return ctx.getSender();
	}
	
	

}
