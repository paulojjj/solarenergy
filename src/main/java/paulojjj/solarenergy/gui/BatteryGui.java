package paulojjj.solarenergy.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.BatteryContainer;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

@SideOnly(Side.CLIENT)
public class BatteryGui extends GuiContainer {
	
	public BatteryGui(EntityPlayer player, BatteryTileEntity tileEntity) {
		super(new BatteryContainer(tileEntity, player));
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/battery_gui.png");

	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;
		
		BatteryContainer container = (BatteryContainer)inventorySlots;
		
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ASSET_RESOURCE);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - TEXTURE_HEIGHT) / 2;
        drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, 
              xSize, TEXTURE_HEIGHT);
        
        int GAUGE_WIDTH = 32;
        int GAUGE_HEIGHT = 58;
        int gaugeMarginX = marginHorizontal + 7;
        int gaugeMarginY = marginVertical + 14;
        
        double gaugeLevel = container.getEnergy() / container.getMaxEnergy();
        int gaugeRectTop = (int)(gaugeMarginY + (1 - gaugeLevel) * GAUGE_HEIGHT);
        if(gaugeLevel > 0) {
        	drawRect(gaugeMarginX, gaugeRectTop, gaugeMarginX + GAUGE_WIDTH, gaugeMarginY + GAUGE_HEIGHT, 0x7000d0ff);
        }
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		BatteryContainer container = (BatteryContainer)inventorySlots;

		String text = "";
		text += EnergyFormatter.format(container.getEnergy());
		text += "/" + EnergyFormatter.format(container.getMaxEnergy());
        fontRenderer.drawString(text, 50, 60, 0x202020);
        fontRenderer.drawString(I18n.format("solarenergy.in") + ": " + EnergyFormatter.format(container.getInput()) + "/t", 50, 80, 0x202020);
        fontRenderer.drawString(I18n.format("solarenergy.out") + ": " + EnergyFormatter.format(container.getOutput()) + "/t", 50, 90, 0x202020);
	}
}
