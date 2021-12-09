package paulojjj.solarenergy.net;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ClientPlayerProvider implements IPlayerProvider {

	@Override
	public Player getPlayer(NetworkEvent.Context ctx) {
        if (EffectiveSide.get().isServer()) {
            return ctx.getSender();
        }
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        return player;			
	}

}
