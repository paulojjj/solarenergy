package paulojjj.solarenergy.proxy;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import paulojjj.solarenergy.blocks.BaseBlock;
import paulojjj.solarenergy.blocks.BaseBlock.RenderLayer;
import paulojjj.solarenergy.registry.GUI;
import paulojjj.solarenergy.registry.Textures;

public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		super.init();

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTextures);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	
	protected RenderType getRenderType(RenderLayer renderLayer) {
		switch(renderLayer) {
			case CUTOUT:
				return RenderType.cutout();
			case CUTOUT_MIPPED:
				return RenderType.cutoutMipped();
			case TRANSLUCENT:
				return RenderType.translucent();
			case SOLID:
			default:
				return RenderType.solid();
		}
	}

	@Override
	public void registerBlock(BlockItem ib) {
		super.registerBlock(ib);
		if(ib.getBlock() instanceof BaseBlock) {
			BaseBlock block = (BaseBlock)ib.getBlock();
			if(block.getRenderLayer() != null) {
				RenderType renderType = getRenderType(block.getRenderLayer());
				ItemBlockRenderTypes.setRenderLayer(block, renderType);
			}
		}
	}

	@Override
	public ISidedFactory getFactory() {
		return ClientFactory.getInstance();
	}

	@SuppressWarnings("unchecked")
	protected <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<?> type,
			MenuScreens.ScreenConstructor<?, ?> factory) {
		MenuScreens.register((MenuType<M>) type, (ScreenConstructor<M, U>) factory);
	}

	public void registerScreen(GUI screen) {
		registerScreen(screen.getContainerType(), screen.getFactory());
	}

	private void registerTextures(TextureStitchEvent.Pre evt) {
		if (!evt.getMap().location().equals(InventoryMenu.BLOCK_ATLAS)) {
			return;
		}

		for (Textures texture : Textures.values()) {
			evt.addSprite(texture.getResourceLocation());
		}
	}

	private void clientSetup(FMLClientSetupEvent evt) {
		for (GUI gui : GUI.values()) {
			registerScreen(gui);
		}
	}
}
