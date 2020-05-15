package paulojjj.solarenergy.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class ServerPlayerProvider implements IPlayerProvider {

	@Override
	public PlayerEntity getPlayer(NetworkEvent.Context ctx) {
        return ctx.getSender();
	}
	
	

}
