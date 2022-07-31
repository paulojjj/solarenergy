package paulojjj.solarenergy;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import paulojjj.solarenergy.blocks.BaseBlock;
import paulojjj.solarenergy.blocks.BaseBlock.RenderLayer;
import paulojjj.solarenergy.registry.Blocks;
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
	
	protected static RenderType getRenderType(RenderLayer renderLayer) {
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

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		/**
		 * Register renderLayers for blocks inheriting BaseBlock
		 */
		for(Blocks block : Blocks.values()) {
			if(block.getBlock() instanceof BaseBlock) {
				BaseBlock base = (BaseBlock)block.getBlock();
				if(base.getRenderLayer() != null) {
					RenderType renderType = getRenderType(base.getRenderLayer());
					ItemBlockRenderTypes.setRenderLayer(base, renderType);
				}
			}
		}
	}
	

}
