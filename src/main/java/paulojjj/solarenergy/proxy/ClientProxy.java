package paulojjj.solarenergy.proxy;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItem;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import paulojjj.solarenergy.blocks.BaseBlock;
import paulojjj.solarenergy.registry.GUI;
import paulojjj.solarenergy.registry.Textures;
import paulojjj.solarenergy.registry.TileEntities;
import paulojjj.solarenergy.renderers.EnergyAssemblerRenderer;
import paulojjj.solarenergy.renderers.EnergyCableRenderer;
import paulojjj.solarenergy.renderers.SolarGeneratorRenderer;

public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		super.init();

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTextures);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}

	@Override
	public void registerBlock(BlockItem ib) {
		super.registerBlock(ib);
		if(ib.getBlock() instanceof BaseBlock) {
			BaseBlock block = (BaseBlock)ib.getBlock();
			if(block.getRenderLayer() != null) {
				RenderTypeLookup.setRenderLayer(block, block.getRenderLayer());
			}
		}
	}

	@Override
	public ISidedFactory getFactory() {
		return ClientFactory.getInstance();
	}

	@SuppressWarnings("unchecked")
	protected <M extends Container, U extends Screen & IHasContainer<M>> void registerScreen(ContainerType<?> type,
			ScreenManager.IScreenFactory<?, ?> factory) {
		ScreenManager.registerFactory((ContainerType<M>) type, (IScreenFactory<M, U>) factory);
	}

	public void registerScreen(GUI screen) {
		registerScreen(screen.getContainerType(), screen.getFactory());
	}

	private void registerTextures(TextureStitchEvent.Pre evt) {
		if (!evt.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
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

		ClientRegistry.bindTileEntityRenderer(TileEntities.ENERGY_CABLE.getType(), EnergyCableRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TileEntities.SOLAR_GENERATOR.getType(), SolarGeneratorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TileEntities.ENERGY_ASSEMBLER.getType(), EnergyAssemblerRenderer::new);
	}

}
