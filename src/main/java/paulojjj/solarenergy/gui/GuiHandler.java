package paulojjj.solarenergy.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import paulojjj.solarenergy.registry.Containers;

public class GuiHandler {
	
	public static boolean openGui(Player player, Level world, Containers guiContainer, BlockPos pos) {
		if(world.isClientSide) {
			return true;
		}
        NetworkHooks.openGui((ServerPlayer) player, guiContainer.getContainerProvider(pos), pos);
		return true;
	}

}
