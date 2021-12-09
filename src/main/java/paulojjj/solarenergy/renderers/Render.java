package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import com.mojang.math.Matrix4f;

public class Render {
	
	public static void drawCubeFaces(Matrix4f matrix4f, VertexConsumer builder, TextureAtlasSprite texture, int light, int overlay, AABB bb, Direction... facings) {
		drawCubeFaces(matrix4f, builder, texture, light, overlay, (float)bb.minX, (float)bb.minY, (float)bb.minZ, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ, facings);		
	}
	
	public static void drawCubeFaces(Matrix4f matrix4f, VertexConsumer builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, Direction... facings) {
		drawCubeFaces(matrix4f, builder, texture, light, overlay, startX, startY, startZ, endX, endY, endZ, 0.75f, facings);
	}
	
	public static void drawCubeFaces(Matrix4f matrix4f, VertexConsumer builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, float color, Direction... facings) {
		for(Direction facing : facings) {
			buildSquare(matrix4f, builder, texture, light, overlay, startX, startY, startZ, endX, endY, endZ, color, facing);
		}
	}
	public static void buildSquare(Matrix4f matrix4f, VertexConsumer builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, Direction facing) {
		buildSquare(matrix4f, builder, texture, light, overlay, startX, startY, startZ, endX, endY, endZ, 0.75f, facing);
	}

	public static void buildSquare(Matrix4f matrix4f, VertexConsumer builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, float color, Direction facing) {
		float minU = texture.getU0();
		float minV = texture.getV0();
		float maxU = texture.getU1();
		float maxV = texture.getV1();
		
		switch(facing) {
		case NORTH:
			builder.vertex(matrix4f, startX, startY, startZ).color(color, color, color, 1f).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex(); //Bottom left
			builder.vertex(matrix4f, startX, endY, startZ).color(color, color, color, 1f).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex(); //Top left
			builder.vertex(matrix4f, endX, endY, startZ).color(color, color, color, 1f).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex(); //Top right
			builder.vertex(matrix4f, endX, startY, startZ).color(color, color, color, 1f).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(0, 0, -1).endVertex(); //Bottom right
			break;
		case SOUTH:
			builder.vertex(matrix4f, startX, startY, endZ).color(color, color, color, 1f).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex(); //Bottom left
			builder.vertex(matrix4f, endX, startY, endZ).color(color, color, color, 1f).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex(); //Bottom right
			builder.vertex(matrix4f, endX, endY, endZ).color(color, color, color, 1f).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex(); //Top right
			builder.vertex(matrix4f, startX, endY, endZ).color(color, color, color, 1f).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex(); //Top left
			break;
		case EAST:
			builder.vertex(matrix4f, endX, startY, startZ).color(color, color, color, 1f).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex(); //Bottom left
			builder.vertex(matrix4f, endX, endY, startZ).color(color, color, color, 1f).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex(); //Top left
			builder.vertex(matrix4f, endX, endY, endZ).color(color, color, color, 1f).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex(); //Top right
			builder.vertex(matrix4f, endX, startY, endZ).color(color, color, color, 1f).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(1, 0, 0).endVertex(); //Bottom right
			break;
		case WEST:
			builder.vertex(matrix4f, startX, startY, startZ).color(color, color, color, 1f).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex(); //Bottom left
			builder.vertex(matrix4f, startX, startY, endZ).color(color, color, color, 1f).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex(); //Bottom right
			builder.vertex(matrix4f, startX, endY, endZ).color(color, color, color, 1f).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex(); //Top right
			builder.vertex(matrix4f, startX, endY, startZ).color(color, color, color, 1f).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(-1, 0, 0).endVertex(); //Top left
			break;
		case UP:
			builder.vertex(matrix4f, startX, endY, endZ).color(color, color, color, 1f).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex(); //Bottom left
			builder.vertex(matrix4f, endX, endY, endZ).color(color, color, color, 1f).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex(); //Bottom right
			builder.vertex(matrix4f, endX , endY, startZ).color(color, color, color, 1f).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex(); //Top right
			builder.vertex(matrix4f, startX, endY, startZ).color(color, color, color, 1f).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(0, 1, 0).endVertex(); //Top left
			break;
		case DOWN:
			builder.vertex(matrix4f, startX, startY, endZ).color(color, color, color, 1f).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(0, -1, 0).endVertex(); //Bottom left
			builder.vertex(matrix4f, startX, startY, startZ).color(color, color, color, 1f).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(0, -1, 0).endVertex(); //Top left
			builder.vertex(matrix4f, endX , startY, startZ).color(color, color, color, 1f).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(0, -1, 0).endVertex(); //Top right
			builder.vertex(matrix4f, endX, startY, endZ).color(color, color, color, 1f).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(0, -1, 0).endVertex(); //Bottom right
			break;
		}
	}
	

}
