package paulojjj.solarenergy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import paulojjj.solarenergy.containers.BatteryContainer;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class GuiHandler implements IGuiHandler {
	
		@Override
		public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			TileEntity tileEntity = (BatteryTileEntity)world.getTileEntity(new BlockPos(x, y, z));
			return new BatteryContainer((BatteryTileEntity)tileEntity, player.inventory);
		}

		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			TileEntity tileEntity = (BatteryTileEntity)world.getTileEntity(new BlockPos(x, y, z));
			return new BatteryGui(player.inventory, (BatteryTileEntity)tileEntity);
		}

}
