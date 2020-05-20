package paulojjj.solarenergy.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import paulojjj.solarenergy.registry.Containers;

public class GuiHandler {
	
	public static boolean openGui(PlayerEntity player, World world, Containers guiContainer, BlockPos pos) {
		if(world.isRemote) {
			return true;
		}
        NetworkHooks.openGui((ServerPlayerEntity) player, guiContainer.getContainerProvider(pos), pos);
		return true;
	}

}
