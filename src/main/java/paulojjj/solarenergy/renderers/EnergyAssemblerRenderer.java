package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3f;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssemblerRenderer extends TileEntityRenderer<EnergyAssemblerTileEntity> {
	
	public EnergyAssemblerRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(EnergyAssemblerTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		Item item = tile.getResultItem();
		if(item == null || item == Items.AIR) {
			return;
		}
		
		matrixStack.push();

		matrixStack.translate(0.5, 0.5, 0.5);
		
		float scale = 0.5f;
		matrixStack.scale(scale, scale, scale);
		
		if(tile.getMaxUltraEnergyStored() > 0) {
			float progress = (float)(tile.getUltraEnergyStored() / tile.getMaxUltraEnergyStored());
			float angle = (progress * 360 * 50) % 360;
			matrixStack.rotate(Vector3f.YP.rotationDegrees(-angle));
		}
		
		Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(item), TransformType.NONE, combinedLight, combinedOverlay, matrixStack, buffer);
		
		matrixStack.pop();
	}

}
