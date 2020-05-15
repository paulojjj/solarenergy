package paulojjj.solarenergy.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IPlayerProvider {

	PlayerEntity getPlayer(NetworkEvent.Context ctx);
	
}
