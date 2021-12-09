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
import paulojjj.solarenergy.containers.EnergyAssemblerContainer;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class EnergyAssemblerGui extends BaseGui<EnergyAssemblerContainer> {
	
	public EnergyAssemblerGui(EnergyAssemblerContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	public static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/energy_assembler_gui.png");
	
	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		EnergyAssemblerContainer container = (EnergyAssemblerContainer)this.menu;
		EnergyStorageContainerUpdateMessage message = container.getStatusMessage();
		
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindForSetup(ASSET_RESOURCE);
        int marginHorizontal = (width - imageWidth) / 2;
        int marginVertical = (height - imageHeight) / 2;
        blit(matrixStack, marginHorizontal, marginVertical, 0, 0, 
        		imageWidth, imageHeight);
        
        int GAUGE_WIDTH = 11;
        int GAUGE_HEIGHT = 23;
        int gaugeMarginX = marginHorizontal + 29;
        int gaugeMarginY = marginVertical + 31;
        
        if(message != null && message.maxEnergyStored > 0) {
        	double gaugeLevel = message.energyStored / message.maxEnergyStored;
        	int gaugeLevelHeight = (int)(gaugeMarginY + gaugeLevel * GAUGE_HEIGHT);
        	if(gaugeLevel > 0) {
        		fill(matrixStack, gaugeMarginX, gaugeMarginY, gaugeMarginX + GAUGE_WIDTH, gaugeLevelHeight, 0x7000d0ff);
        	}
        }
	}
	
	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		EnergyAssemblerContainer container = (EnergyAssemblerContainer)this.menu;
		EnergyStorageContainerUpdateMessage message = container.getStatusMessage();
		
		if(message != null && message.maxEnergyStored > 0) {
			String text = "";
			text += EnergyFormatter.format(message.energyStored);
			text += "/" + EnergyFormatter.format(message.maxEnergyStored);
			font.draw(matrixStack, text, 52, 27, 0x202020);
			font.draw(matrixStack, I18n.get("solarenergy.in") + ": " + EnergyFormatter.format(message.input) + "/t", 52, 47, 0x202020);
		}
	}


}
