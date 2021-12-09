package paulojjj.solarenergy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import paulojjj.solarenergy.registry.TileEntities;
import paulojjj.solarenergy.renderers.EnergyAssemblerRenderer;
import paulojjj.solarenergy.renderers.EnergyCableRenderer;
import paulojjj.solarenergy.renderers.SolarGeneratorRenderer;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientSubscribers {
	
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers renderers) {
		renderers.registerBlockEntityRenderer(TileEntities.ENERGY_CABLE.getType(), EnergyCableRenderer::new);
		renderers.registerBlockEntityRenderer(TileEntities.SOLAR_GENERATOR.getType(), SolarGeneratorRenderer::new);
		renderers.registerBlockEntityRenderer(TileEntities.ENERGY_ASSEMBLER.getType(), EnergyAssemblerRenderer::new);
	}

}
