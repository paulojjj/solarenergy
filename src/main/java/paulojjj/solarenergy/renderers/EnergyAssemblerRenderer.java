package paulojjj.solarenergy.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssemblerRenderer extends TileEntitySpecialRenderer<EnergyAssemblerTileEntity> {
	
	
	@Override
	public void render(EnergyAssemblerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		Item item = te.getResultItem();
		if(item == null || item == Items.AIR) {
			return;
		}
		
		GlStateManager.pushMatrix();

		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableCull();
		
		float scale = 0.5f;
		
		GlStateManager.scale(scale, scale, scale);
		
		if(te.getMaxUltraEnergyStored() > 0) {
			float progress = (float)(te.getUltraEnergyStored() / te.getMaxUltraEnergyStored());
			float angle = (progress * 360 * 50) % 360;
			GlStateManager.rotate(-angle, 0, 1, 0);
		}
		
		Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(item), TransformType.NONE);
		
		GlStateManager.popMatrix();
	}
	

}
