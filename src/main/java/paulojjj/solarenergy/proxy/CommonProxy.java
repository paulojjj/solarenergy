package paulojjj.solarenergy.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import paulojjj.solarenergy.TickHandler;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.registry.Items;
import paulojjj.solarenergy.registry.Items.ItemType;
import paulojjj.solarenergy.registry.TileEntities;

public class CommonProxy implements Proxy {
	
	@Override
	public void registerAssets() {
		for(Blocks blocks : Blocks.values()) {
			registerBlock(blocks.getItemBlock());
		}
		for(Items item : Items.values()) {
			registerItem(item);
		}
		for(TileEntities tile : TileEntities.values()) {
			registerTileEntity(tile);
		}
		for(Containers container : Containers.values()) {
			registerContainer(container);
		}
	}
	
	public void registerBlock(BlockItem ib) {
		Block block = ib.getBlock();

		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(ib);
	}
	
	public void registerItem(Items item) {
		ForgeRegistries.ITEMS.register(item.getItem());		
		if(item.getType() == ItemType.FORGE_ORE_DICT) {
			//OreDictionary.registerOre(item.getRegistryName(), item.getItem());			
		}
	}

	public void registerTileEntity(TileEntities tile) {
		ForgeRegistries.TILE_ENTITIES.register(tile.getType());
	}
	
	public void registerContainer(Containers container) {
		ForgeRegistries.CONTAINERS.register(container.getType());
	}
	
	@Override
	public void registerCommands() {
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
