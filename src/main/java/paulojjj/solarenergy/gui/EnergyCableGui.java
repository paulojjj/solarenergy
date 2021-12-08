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
import paulojjj.solarenergy.containers.EnergyCableContainer;

@OnlyIn(Dist.CLIENT)
public class EnergyCableGui extends BaseGui<EnergyCableContainer> {

	
	public EnergyCableGui(EnergyCableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	private static final ResourceLocation ASSET_RESOURCE = new ResourceLocation(Main.MODID, "gui/empty_gui.png");

	
	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		int TEXTURE_HEIGHT = 85;
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(ASSET_RESOURCE);
        int marginHorizontal = (width - imageWidth) / 2;
        int marginVertical = (height - TEXTURE_HEIGHT) / 2;
        blit(marginHorizontal, marginVertical, 0, 0, 
              imageWidth, TEXTURE_HEIGHT);
	}
	
	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		super.renderLabels(mouseX, mouseY);
		
        font.draw(I18n.get("solarenergy.throughput") + ": " + EnergyFormatter.format(menu.getOutput()) + "/t", 35, 78, 0x202020);
	}
}
