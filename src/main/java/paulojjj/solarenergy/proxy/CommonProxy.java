package paulojjj.solarenergy.proxy;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.TickHandler;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.registry.Items;
import paulojjj.solarenergy.registry.TileEntities;

public class CommonProxy implements Proxy {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Main.MODID);
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Main.MODID);
	
	public void init() {
		
	}
	
	@Override
	public void registerAssets() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
		TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

		Items.values();
		Blocks.values();
		Containers.values();
		TileEntities.values();
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
