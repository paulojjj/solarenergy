package paulojjj.solarenergy.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import paulojjj.solarenergy.registry.GUI;

public class GuiHandler {
	
	public static boolean openGui(PlayerEntity player, World world, GUI gui, BlockPos pos) {
		if(world.isRemote) {
			return true;
		}
        NetworkHooks.openGui((ServerPlayerEntity) player, gui.getContainerProvider(), pos);
		return true;
	}

}
