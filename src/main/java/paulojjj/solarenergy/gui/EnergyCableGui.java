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
import paulojjj.solarenergy.containers.EnergyCableContainer;

@OnlyIn(Dist.CLIENT)
public class EnergyCableGui extends BaseGui<EnergyCableContainer> {

	
	public EnergyCableGui(EnergyCableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/empty_gui.png");

	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(ASSET_RESOURCE);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - TEXTURE_HEIGHT) / 2;
        blit(matrixStack, marginHorizontal, marginVertical, 0, 0, 
              xSize, TEXTURE_HEIGHT);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.drawString(matrixStack, I18n.format("solarenergy.throughput") + ": " + EnergyFormatter.format(container.getOutput()) + "/t", 35, 78, 0x202020);
	}
}
