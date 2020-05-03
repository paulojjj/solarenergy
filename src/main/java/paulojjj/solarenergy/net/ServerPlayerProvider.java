package paulojjj.solarenergy.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerPlayerProvider implements IPlayerProvider {

	@Override
	public EntityPlayer getPlayer(MessageContext ctx) {
        return ctx.getServerHandler().player;
	}
	
	

}
