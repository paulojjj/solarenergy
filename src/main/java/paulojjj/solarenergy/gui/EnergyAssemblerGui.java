package paulojjj.solarenergy.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.BatteryContainer;
import paulojjj.solarenergy.containers.EnergyAssemblerContainer;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

public class EnergyAssemblerGui extends GuiContainer {
	
	public EnergyAssemblerGui(EnergyAssemblerTileEntity tileEntity, EntityPlayer player) {
		super(new EnergyAssemblerContainer(tileEntity, player.inventory));
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/energy_assembler_gui.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		EnergyAssemblerContainer container = (EnergyAssemblerContainer)inventorySlots;
		EnergyStorageContainerUpdateMessage message = container.getStatusMessage();
		
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ASSET_RESOURCE);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - ySize) / 2;
        drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, 
              xSize, ySize);
        
        int GAUGE_WIDTH = 11;
        int GAUGE_HEIGHT = 23;
        int gaugeMarginX = marginHorizontal + 29;
        int gaugeMarginY = marginVertical + 31;
        
        if(message != null && message.maxEnergyStored > 0) {
        	double gaugeLevel = message.energyStored / message.maxEnergyStored;
        	int gaugeLevelHeight = (int)(gaugeMarginY + gaugeLevel * GAUGE_HEIGHT);
        	if(gaugeLevel > 0) {
        		drawRect(gaugeMarginX, gaugeMarginY, gaugeMarginX + GAUGE_WIDTH, gaugeLevelHeight, 0x7000d0ff);
        	}
        }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		EnergyAssemblerContainer container = (EnergyAssemblerContainer)inventorySlots;
		EnergyStorageContainerUpdateMessage message = container.getStatusMessage();
		
		if(message != null && message.maxEnergyStored > 0) {
			String text = "";
			text += EnergyFormatter.format(message.energyStored);
			text += "/" + EnergyFormatter.format(message.maxEnergyStored);
			fontRenderer.drawString(text, 52, 27, 0x202020);
			fontRenderer.drawString(I18n.format("solarenergy.in") + ": " + EnergyFormatter.format(message.input) + "/t", 52, 47, 0x202020);
		}
	}


}
