package paulojjj.solarenergy.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.TickHandler;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.registry.Items;
import paulojjj.solarenergy.registry.Items.ItemType;
import paulojjj.solarenergy.tiles.BatteryTileEntity;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class CommonProxy implements Proxy {
	
	@Override
	public void registerAssets() {
		for(Blocks blocks : Blocks.values()) {
			registerBlock(blocks.getItemBlock());
		}
		for(Items item : Items.values()) {
			registerItem(item);
		}

		GameRegistry.registerTileEntity(SolarGeneratorTileEntity.class, new ResourceLocation(Main.MODID, "solar_generator_tile_entity"));
		GameRegistry.registerTileEntity(BatteryTileEntity.class, new ResourceLocation(Main.MODID,"battery_tile_entity"));
		GameRegistry.registerTileEntity(EnergyAssemblerTileEntity.class, new ResourceLocation(Main.MODID,"energy_assembler_tile_entity"));
		GameRegistry.registerTileEntity(EnergyCableTileEntity.class, new ResourceLocation(Main.MODID,"energy_cable_tile_entity"));
	}
	
	public void registerBlock(ItemBlock ib) {
		Block block = ib.getBlock();

		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(ib);
	}
	
	public void registerItem(Items item) {
		ForgeRegistries.ITEMS.register(item.getItem());		
		if(item.getType() == ItemType.FORGE_ORE_DICT) {
			OreDictionary.registerOre(item.getRegistryName(), item.getItem());			
		}
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
		MinecraftForge.EVENT_BUS.register(TickHandler.class);		
	}

	@Override
	public ISidedFactory getFactory() {
		return ServerFactory.getInstance();
	}
	
}
