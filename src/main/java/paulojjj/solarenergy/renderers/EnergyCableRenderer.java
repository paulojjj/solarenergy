package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.blocks.EnergyCable.Boxes;
import paulojjj.solarenergy.registry.Textures;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

@OnlyIn(Dist.CLIENT)
public class EnergyCableRenderer implements BlockEntityRenderer<EnergyCableTileEntity> {

	public EnergyCableRenderer(BlockEntityRendererProvider.Context context) {
	}

	private static TextureAtlasSprite CENTER_TEXTURE;
	private static TextureAtlasSprite SIDES_TEXTURE_HORIZONTAL;
	private static TextureAtlasSprite SIDES_TEXTURE_VERTICAL;

	@Override
	public void render(EnergyCableTileEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if(CENTER_TEXTURE == null) {
			CENTER_TEXTURE = Textures.ENERGY_CABLE_CENTER.getSprite();
			SIDES_TEXTURE_HORIZONTAL = Textures.ENERGY_CABLE_HORIZONTAL.getSprite();
			SIDES_TEXTURE_VERTICAL = Textures.ENERGY_CABLE_VERTICAL.getSprite();
		}
		
		EnergyCableTileEntity te = (EnergyCableTileEntity)tile;
		matrixStack.pushPose();
		
		VertexConsumer builder = buffer.getBuffer(RenderType.cutoutMipped());
		Matrix4f matrix4f = matrixStack.last().pose();

		Render.drawCubeFaces(matrix4f, builder, CENTER_TEXTURE, combinedLight, combinedOverlay, Boxes.CENTER.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN);

		for(Direction facing : Direction.values()) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			if(facing == Direction.NORTH) {
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_HORIZONTAL, combinedLight, combinedOverlay, Boxes.NORTH.getBoundingBox(), Direction.EAST, Direction.WEST);
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_VERTICAL, combinedLight, combinedOverlay, Boxes.NORTH.getBoundingBox(), Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.SOUTH) {
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_HORIZONTAL, combinedLight, combinedOverlay, Boxes.SOUTH.getBoundingBox(), Direction.EAST, Direction.WEST);
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_VERTICAL, combinedLight, combinedOverlay, Boxes.SOUTH.getBoundingBox(), Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.EAST) {
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_HORIZONTAL, combinedLight, combinedOverlay, Boxes.EAST.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.WEST) {
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_HORIZONTAL, combinedLight, combinedOverlay, Boxes.WEST.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.UP) {
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_VERTICAL, combinedLight, combinedOverlay, Boxes.UP.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
			}
			else if(facing == Direction.DOWN) {
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE_VERTICAL, combinedLight, combinedOverlay, Boxes.DOWN.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
			}
		}
		
		matrixStack.popPose();
	}
	
	

}
