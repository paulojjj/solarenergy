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
import paulojjj.solarenergy.containers.BatteryContainer;

@OnlyIn(Dist.CLIENT)
public class BatteryGui extends BaseGui<BatteryContainer> {

	
	public BatteryGui(BatteryContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/battery_gui.png");

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		String text = "";
		text += EnergyFormatter.format(menu.getEnergy());
		text += "/" + EnergyFormatter.format(menu.getMaxEnergy());
        font.draw(matrixStack, text, 50, 60, 0x202020);
        font.draw(matrixStack, I18n.get("solarenergy.in") + ": " + EnergyFormatter.format(menu.getInput()) + "/t", 50, 80, 0x202020);
        font.draw(matrixStack, I18n.get("solarenergy.out") + ": " + EnergyFormatter.format(menu.getOutput()) + "/t", 50, 90, 0x202020);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		getMinecraft().getTextureManager().bindForSetup(ASSET_RESOURCE);
        int marginHorizontal = (width - imageWidth) / 2;
        int marginVertical = (height - TEXTURE_HEIGHT) / 2;
        blit(matrixStack, marginHorizontal, marginVertical, 0, 0, 
        		imageWidth, TEXTURE_HEIGHT);
        
        int GAUGE_WIDTH = 32;
        int GAUGE_HEIGHT = 58;
        int gaugeMarginX = marginHorizontal + 7;
        int gaugeMarginY = marginVertical + 14;
        
        double gaugeLevel = menu.getEnergy() / menu.getMaxEnergy();
        int gaugeRectTop = (int)(gaugeMarginY + (1 - gaugeLevel) * GAUGE_HEIGHT);
        if(gaugeLevel > 0) {
        	fill(matrixStack, gaugeMarginX, gaugeRectTop, gaugeMarginX + GAUGE_WIDTH, gaugeMarginY + GAUGE_HEIGHT, 0x7000d0ff);
        }
	}

}
