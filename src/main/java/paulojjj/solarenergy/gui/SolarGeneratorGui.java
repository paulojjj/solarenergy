package paulojjj.solarenergy.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.SolarGeneratorContainer;

@OnlyIn(Dist.CLIENT)
public class SolarGeneratorGui extends BaseGui<SolarGeneratorContainer> {

	public SolarGeneratorGui(SolarGeneratorContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/empty_gui.png");
	private static final ResourceLocation SUN_RESOURCE = new ResourceLocation(Main.MODID, "gui/sun.png");


	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, ASSET_RESOURCE);
		int marginHorizontal = (width - imageWidth) / 2;
		int marginVertical = (height - TEXTURE_HEIGHT) / 2;
		blit(matrixStack, marginHorizontal, marginVertical, 0, 0, imageWidth, TEXTURE_HEIGHT);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, SUN_RESOURCE);
		blit(matrixStack, marginHorizontal + 65, marginVertical + 3, 0, 0, 50, 50);
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		SolarGeneratorContainer container = (SolarGeneratorContainer)this.menu;

		font.draw(matrixStack, I18n.get("solarenergy.producing") + ": " + EnergyFormatter.format(container.getActiveProduction()) + "/t", 15, 92, 0x202020);
		font.draw(matrixStack, I18n.get("solarenergy.output") + ": " + EnergyFormatter.format(container.getOutput()) + "/t", 15, 107, 0x202020);
	}
}
