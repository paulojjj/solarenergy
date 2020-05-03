package paulojjj.solarenergy.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientPlayerProvider implements IPlayerProvider {

	@Override
	public EntityPlayer getPlayer(MessageContext ctx) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            return ctx.getServerHandler().player;
        }
        return Minecraft.getMinecraft().player;			
	}

}
