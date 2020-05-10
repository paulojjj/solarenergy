package paulojjj.solarenergy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.EnergyStorageContainer;
import paulojjj.solarenergy.containers.EnergyAssemblerContainer;
import paulojjj.solarenergy.containers.SolarGeneratorContainer;
import paulojjj.solarenergy.tiles.BatteryTileEntity;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class GuiHandler implements IGuiHandler {
	
	public enum GUI {
		BATTERY, SOLAR_GENERATOR, ENERGY_ASSEMBLER, ENERGY_CABLE;

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
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch(GUI.of(ID)) {
		case BATTERY:
			return new EnergyStorageContainer((BatteryTileEntity)tileEntity, player);
		case SOLAR_GENERATOR:
			return new SolarGeneratorContainer((SolarGeneratorTileEntity)tileEntity, player);
		case ENERGY_ASSEMBLER:
			return new EnergyAssemblerContainer((EnergyAssemblerTileEntity)tileEntity, player.inventory);
		case ENERGY_CABLE:
			return new EnergyStorageContainer((EnergyStorageTileEntity)tileEntity, player);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch(GUI.of(ID)) {
		case BATTERY:
			return new BatteryGui(player, (BatteryTileEntity)tileEntity);
		case SOLAR_GENERATOR:
			return new SolarGeneratorGui(player, (SolarGeneratorTileEntity)tileEntity);
		case ENERGY_ASSEMBLER:
			return new EnergyAssemblerGui((EnergyAssemblerTileEntity)tileEntity, player);
		case ENERGY_CABLE:
			return new EnergyCableGui(player, (EnergyStorageTileEntity)tileEntity);
		default:
			return null;
		}
	}

}
