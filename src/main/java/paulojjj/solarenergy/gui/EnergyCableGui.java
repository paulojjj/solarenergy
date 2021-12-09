package paulojjj.solarenergy.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.EnergyCableContainer;

@OnlyIn(Dist.CLIENT)
public class EnergyCableGui extends BaseGui<EnergyCableContainer> {

	
	public EnergyCableGui(EnergyCableContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/empty_gui.png");

	
	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;
		
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindForSetup(ASSET_RESOURCE);
        int marginHorizontal = (width - imageWidth) / 2;
        int marginVertical = (height - TEXTURE_HEIGHT) / 2;
        blit(matrixStack, marginHorizontal, marginVertical, 0, 0, 
              imageWidth, TEXTURE_HEIGHT);
	}
	
	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, I18n.get("solarenergy.throughput") + ": " + EnergyFormatter.format(menu.getOutput()) + "/t", 35, 78, 0x202020);
	}
}
