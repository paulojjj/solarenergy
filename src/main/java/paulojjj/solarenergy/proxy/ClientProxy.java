package paulojjj.solarenergy.proxy;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import paulojjj.solarenergy.registry.GUI;
import paulojjj.solarenergy.registry.Items;
import paulojjj.solarenergy.registry.TileEntities;
import paulojjj.solarenergy.renderers.EnergyCableRenderer;
import paulojjj.solarenergy.renderers.SolarGeneratorRenderer;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerBlock(BlockItem ib) {
		super.registerBlock(ib);
		registerModelResourceLocation(ib);
	}
	
	@Override
	public void registerItem(Items item) {
		super.registerItem(item);
		registerModelResourceLocation(item.getItem());
	}
	
	protected void registerModelResourceLocation(Item item) {
		String id = item.getRegistryName().toString();
		ModelResourceLocation mrl = new ModelResourceLocation(id, "inventory");
		ModelLoader.addSpecialModel(mrl);		
	}
	
	@Override
	public ISidedFactory getFactory() {
		return ClientFactory.getInstance();
	}
	
	@SuppressWarnings("unchecked")
	protected <M extends Container, U extends Screen & IHasContainer<M>> void registerScreen(ContainerType<?> type, ScreenManager.IScreenFactory<?, ?> factory) {
		ScreenManager.registerFactory((ContainerType<M>)type, (IScreenFactory<M,U>)factory);
	}
	
	public void registerScreen(GUI screen) {
		registerScreen(screen.getContainerType(), screen.getFactory());
	}
	
	
	@Override
	public void registerAssets() {
		super.registerAssets();
		
		for(GUI gui : GUI.values()) {
			registerScreen(gui);
		}
		
		ClientRegistry.bindTileEntityRenderer(TileEntities.SOLAR_GENERATOR.getType(), SolarGeneratorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TileEntities.ENERGY_CABLE.getType(), EnergyCableRenderer::new);
	}
	
}
