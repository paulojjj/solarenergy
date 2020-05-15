package paulojjj.solarenergy.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientPlayerProvider implements IPlayerProvider {

	@Override
	public PlayerEntity getPlayer(NetworkEvent.Context ctx) {
        if (EffectiveSide.get().isServer()) {
            return ctx.getSender();
        }
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        mc.close();
        return player;			
	}

}
