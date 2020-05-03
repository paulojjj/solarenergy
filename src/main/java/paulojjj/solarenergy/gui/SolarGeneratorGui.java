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
import paulojjj.solarenergy.containers.SolarGeneratorContainer;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

@SideOnly(Side.CLIENT)
public class SolarGeneratorGui extends GuiContainer {

	public SolarGeneratorGui(EntityPlayer player, SolarGeneratorTileEntity tileEntity) {
		super(new SolarGeneratorContainer(tileEntity, player));
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/solar_generator_gui.png");
	private static final ResourceLocation SUN_RESOURCE = new ResourceLocation(Main.MODID, "gui/sun.png");


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(ASSET_RESOURCE);
		int marginHorizontal = (width - xSize) / 2;
		int marginVertical = (height - TEXTURE_HEIGHT) / 2;
		drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, xSize, TEXTURE_HEIGHT);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(SUN_RESOURCE);
		drawTexturedModalRect(marginHorizontal + 65, marginVertical + 3, 0, 0, 50, 50);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		SolarGeneratorContainer container = (SolarGeneratorContainer)inventorySlots;
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRenderer.drawString(I18n.format("solarenergy.producing") + ": " + EnergyFormatter.format(container.getActiveProduction()) + "/t", 15, 92, 0x202020);
		fontRenderer.drawString(I18n.format("solarenergy.output") + ": " + EnergyFormatter.format(container.getOutput()) + "/t", 15, 107, 0x202020);
	}
}
