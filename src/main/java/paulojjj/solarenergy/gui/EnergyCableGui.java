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
import paulojjj.solarenergy.containers.EnergyStorageContainer;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity;

@SideOnly(Side.CLIENT)
public class EnergyCableGui extends GuiContainer {
	
	public EnergyCableGui(EntityPlayer player, EnergyStorageTileEntity tileEntity) {
		super(new EnergyStorageContainer(tileEntity, player));
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/empty_gui.png");

	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;
		
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ASSET_RESOURCE);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - TEXTURE_HEIGHT) / 2;
        drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, 
              xSize, TEXTURE_HEIGHT);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		EnergyStorageContainer container = (EnergyStorageContainer)inventorySlots;

        fontRenderer.drawString(I18n.format("solarenergy.throughput") + ": " + EnergyFormatter.format(container.getOutput()) + "/t", 35, 78, 0x202020);
	}
}
