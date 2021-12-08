package paulojjj.solarenergy.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.SolarGeneratorContainer;

@OnlyIn(Dist.CLIENT)
public class SolarGeneratorGui extends BaseGui<SolarGeneratorContainer> {

	public SolarGeneratorGui(SolarGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/empty_gui.png");
	private static final ResourceLocation SUN_RESOURCE = new ResourceLocation(Main.MODID, "gui/sun.png");


	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(ASSET_RESOURCE);
		int marginHorizontal = (width - imageWidth) / 2;
		int marginVertical = (height - TEXTURE_HEIGHT) / 2;
		blit(matrixStack, marginHorizontal, marginVertical, 0, 0, imageWidth, TEXTURE_HEIGHT);

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(SUN_RESOURCE);
		blit(matrixStack, marginHorizontal + 65, marginVertical + 3, 0, 0, 50, 50);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		SolarGeneratorContainer container = (SolarGeneratorContainer)this.menu;

		font.draw(matrixStack, I18n.get("solarenergy.producing") + ": " + EnergyFormatter.format(container.getActiveProduction()) + "/t", 15, 92, 0x202020);
		font.draw(matrixStack, I18n.get("solarenergy.output") + ": " + EnergyFormatter.format(container.getOutput()) + "/t", 15, 107, 0x202020);
	}
}
