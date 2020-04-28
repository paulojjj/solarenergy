package paulojjj.solarenergy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.BatteryContainer;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class GuiHandler implements IGuiHandler {
	
	public enum GUI {
		BATTERY, SOLAR_GENERATOR;

		public static GUI of(int id) {
			if(id > GUI.values().length) {
				return null;
			}
			return GUI.values()[id];
		}
	}

	public static boolean openGui(EntityPlayer player, World world, GUI gui, BlockPos pos) {
		if(world.isRemote) {
			return true;
		}
		player.openGui(Main.MODID, gui.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = (BatteryTileEntity)world.getTileEntity(new BlockPos(x, y, z));
		switch(GUI.of(ID)) {
		case BATTERY:
			return new BatteryContainer((BatteryTileEntity)tileEntity, player.inventory);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = (BatteryTileEntity)world.getTileEntity(new BlockPos(x, y, z));
		switch(GUI.of(ID)) {
		case BATTERY:
			return new BatteryGui(player.inventory, (BatteryTileEntity)tileEntity);
		default:
			return null;
		}
	}

}
