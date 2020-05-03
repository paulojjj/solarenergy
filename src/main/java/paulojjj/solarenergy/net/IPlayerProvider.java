package paulojjj.solarenergy.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IPlayerProvider {

	EntityPlayer getPlayer(MessageContext ctx);
	
}
