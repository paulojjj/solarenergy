package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;

public class EnergyAssemblerRenderer implements BlockEntityRenderer<EnergyAssemblerTileEntity> {
	
	public EnergyAssemblerRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(EnergyAssemblerTileEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Item item = tile.getResultItem();
		if(item == null || item == Items.AIR) {
			return;
		}
		
		matrixStack.pushPose();

		matrixStack.translate(0.5, 0.5, 0.5);
		
		float scale = 0.5f;
		matrixStack.scale(scale, scale, scale);
		
		if(tile.getMaxUltraEnergyStored() > 0) {
			float progress = (float)(tile.getUltraEnergyStored() / tile.getMaxUltraEnergyStored());
			float angle = (progress * 360 * 50) % 360;
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(-angle));
		}
		
		Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(item), TransformType.NONE, combinedLight, combinedOverlay, matrixStack, buffer, 0);
		
		matrixStack.popPose();
	}

}
