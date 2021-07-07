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
import paulojjj.solarenergy.containers.EnergyAssemblerContainer;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class EnergyAssemblerGui extends BaseGui<EnergyAssemblerContainer> {
	
	public EnergyAssemblerGui(EnergyAssemblerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	public static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/energy_assembler_gui.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		EnergyAssemblerContainer container = (EnergyAssemblerContainer)this.container;
		EnergyStorageContainerUpdateMessage message = container.getStatusMessage();
		
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(ASSET_RESOURCE);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - ySize) / 2;
        blit(matrixStack, marginHorizontal, marginVertical, 0, 0, 
              xSize, ySize);
        
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
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		EnergyAssemblerContainer container = (EnergyAssemblerContainer)this.container;
		EnergyStorageContainerUpdateMessage message = container.getStatusMessage();
		
		if(message != null && message.maxEnergyStored > 0) {
			String text = "";
			text += EnergyFormatter.format(message.energyStored);
			text += "/" + EnergyFormatter.format(message.maxEnergyStored);
			font.drawString(matrixStack, text, 52, 27, 0x202020);
			font.drawString(matrixStack, I18n.format("solarenergy.in") + ": " + EnergyFormatter.format(message.input) + "/t", 52, 47, 0x202020);
		}
	}


}
