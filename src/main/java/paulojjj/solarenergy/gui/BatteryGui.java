package paulojjj.solarenergy.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.BatteryContainer;

@OnlyIn(Dist.CLIENT)
public class BatteryGui extends BaseGui<BatteryContainer> {

	
	public BatteryGui(BatteryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/battery_gui.png");

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		super.renderLabels(mouseX, mouseY);
		
		String text = "";
		text += EnergyFormatter.format(menu.getEnergy());
		text += "/" + EnergyFormatter.format(menu.getMaxEnergy());
        font.draw(text, 50, 60, 0x202020);
        font.draw(I18n.get("solarenergy.in") + ": " + EnergyFormatter.format(menu.getInput()) + "/t", 50, 80, 0x202020);
        font.draw(I18n.get("solarenergy.out") + ": " + EnergyFormatter.format(menu.getOutput()) + "/t", 50, 90, 0x202020);
	}

	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(ASSET_RESOURCE);
        int marginHorizontal = (width - imageWidth) / 2;
        int marginVertical = (height - TEXTURE_HEIGHT) / 2;
        blit(marginHorizontal, marginVertical, 0, 0, 
        		imageWidth, TEXTURE_HEIGHT);
        
        int GAUGE_WIDTH = 32;
        int GAUGE_HEIGHT = 58;
        int gaugeMarginX = marginHorizontal + 7;
        int gaugeMarginY = marginVertical + 14;
        
        double gaugeLevel = menu.getEnergy() / menu.getMaxEnergy();
        int gaugeRectTop = (int)(gaugeMarginY + (1 - gaugeLevel) * GAUGE_HEIGHT);
        if(gaugeLevel > 0) {
        	fill(gaugeMarginX, gaugeRectTop, gaugeMarginX + GAUGE_WIDTH, gaugeMarginY + GAUGE_HEIGHT, 0x7000d0ff);
        }
	}

}
