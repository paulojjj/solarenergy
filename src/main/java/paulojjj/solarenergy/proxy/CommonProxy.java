package paulojjj.solarenergy.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.tiles.BatteryTileEntity;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class CommonProxy implements Proxy {
	
	protected static long tick = 0;	
	
	@Override
	public void registerBlocks() {
		for(Blocks blocks : Blocks.values()) {
			registerBlock(blocks.getItemBlock());
		}
	}
	
	public void registerBlock(ItemBlock ib) {
		//SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(null);
		//wrapper.
		
		Block block = ib.getBlock();

		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(ib);
		
		GameRegistry.registerTileEntity(SolarGeneratorTileEntity.class, new ResourceLocation("solar_generator_tile_entity"));
		GameRegistry.registerTileEntity(BatteryTileEntity.class, new ResourceLocation("battery_tile_entity"));
	}

	@Override
	public void registerCommands() {
	}
	
	@Override
	public void registerGuiHandler() {
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
	}

	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(this);		
	}
	
	@SubscribeEvent
	public void serverTick(ServerTickEvent evt) {
		tick++;
	}
	
	public static long getTick() {
		return tick;
	}
	
}
