package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;

public class Render {
	
	public static void drawCubeFaces(Matrix4f matrix4f, IVertexBuilder builder, TextureAtlasSprite texture, int light, int overlay, AxisAlignedBB bb, Direction... facings) {
		drawCubeFaces(matrix4f, builder, texture, light, overlay, (float)bb.minX, (float)bb.minY, (float)bb.minZ, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ, facings);		
	}
	
	public static void drawCubeFaces(Matrix4f matrix4f, IVertexBuilder builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, Direction... facings) {
		drawCubeFaces(matrix4f, builder, texture, light, overlay, startX, startY, startZ, endX, endY, endZ, 0.75f, facings);
	}
	
	public static void drawCubeFaces(Matrix4f matrix4f, IVertexBuilder builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, float color, Direction... facings) {
		for(Direction facing : facings) {
			buildSquare(matrix4f, builder, texture, light, overlay, startX, startY, startZ, endX, endY, endZ, color, facing);
		}
	}
	public static void buildSquare(Matrix4f matrix4f, IVertexBuilder builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, Direction facing) {
		buildSquare(matrix4f, builder, texture, light, overlay, startX, startY, startZ, endX, endY, endZ, 0.75f, facing);
	}

	public static void buildSquare(Matrix4f matrix4f, IVertexBuilder builder, TextureAtlasSprite texture, int light, int overlay, float startX, float startY, float startZ, float endX, float endY, float endZ, float color, Direction facing) {
		float minU = texture.getMinU();
		float minV = texture.getMinV();
		float maxU = texture.getMaxU();
		float maxV = texture.getMaxV();
		
		switch(facing) {
		case NORTH:
			builder.pos(matrix4f, startX, startY, startZ).color(color, color, color, 1f).tex(minU, maxV).overlay(overlay).lightmap(light).normal(0, 0, -1).endVertex(); //Bottom left
			builder.pos(matrix4f, startX, endY, startZ).color(color, color, color, 1f).tex(minU, minV).overlay(overlay).lightmap(light).normal(0, 0, -1).endVertex(); //Top left
			builder.pos(matrix4f, endX, endY, startZ).color(color, color, color, 1f).tex(maxU, minV).overlay(overlay).lightmap(light).normal(0, 0, -1).endVertex(); //Top right
			builder.pos(matrix4f, endX, startY, startZ).color(color, color, color, 1f).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(0, 0, -1).endVertex(); //Bottom right
			break;
		case SOUTH:
			builder.pos(matrix4f, startX, startY, endZ).color(color, color, color, 1f).tex(minU, maxV).overlay(overlay).lightmap(light).normal(0, 0, 1).endVertex(); //Bottom left
			builder.pos(matrix4f, endX, startY, endZ).color(color, color, color, 1f).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(0, 0, 1).endVertex(); //Bottom right
			builder.pos(matrix4f, endX, endY, endZ).color(color, color, color, 1f).tex(maxU, minV).overlay(overlay).lightmap(light).normal(0, 0, 1).endVertex(); //Top right
			builder.pos(matrix4f, startX, endY, endZ).color(color, color, color, 1f).tex(minU, minV).overlay(overlay).lightmap(light).normal(0, 0, 1).endVertex(); //Top left
			break;
		case EAST:
			builder.pos(matrix4f, endX, startY, startZ).color(color, color, color, 1f).tex(minU, maxV).overlay(overlay).lightmap(light).normal(1, 0, 0).endVertex(); //Bottom left
			builder.pos(matrix4f, endX, endY, startZ).color(color, color, color, 1f).tex(minU, minV).overlay(overlay).lightmap(light).normal(1, 0, 0).endVertex(); //Top left
			builder.pos(matrix4f, endX, endY, endZ).color(color, color, color, 1f).tex(maxU, minV).overlay(overlay).lightmap(light).normal(1, 0, 0).endVertex(); //Top right
			builder.pos(matrix4f, endX, startY, endZ).color(color, color, color, 1f).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(1, 0, 0).endVertex(); //Bottom right
			break;
		case WEST:
			builder.pos(matrix4f, startX, startY, startZ).color(color, color, color, 1f).tex(minU, maxV).overlay(overlay).lightmap(light).normal(-1, 0, 0).endVertex(); //Bottom left
			builder.pos(matrix4f, startX, startY, endZ).color(color, color, color, 1f).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(-1, 0, 0).endVertex(); //Bottom right
			builder.pos(matrix4f, startX, endY, endZ).color(color, color, color, 1f).tex(maxU, minV).overlay(overlay).lightmap(light).normal(-1, 0, 0).endVertex(); //Top right
			builder.pos(matrix4f, startX, endY, startZ).color(color, color, color, 1f).tex(minU, minV).overlay(overlay).lightmap(light).normal(-1, 0, 0).endVertex(); //Top left
			break;
		case UP:
			builder.pos(matrix4f, startX, endY, endZ).color(color, color, color, 1f).tex(minU, maxV).overlay(overlay).lightmap(light).normal(0, 1, 0).endVertex(); //Bottom left
			builder.pos(matrix4f, endX, endY, endZ).color(color, color, color, 1f).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(0, 1, 0).endVertex(); //Bottom right
			builder.pos(matrix4f, endX , endY, startZ).color(color, color, color, 1f).tex(maxU, minV).overlay(overlay).lightmap(light).normal(0, 1, 0).endVertex(); //Top right
			builder.pos(matrix4f, startX, endY, startZ).color(color, color, color, 1f).tex(minU, minV).overlay(overlay).lightmap(light).normal(0, 1, 0).endVertex(); //Top left
			break;
		case DOWN:
			builder.pos(matrix4f, startX, startY, endZ).color(color, color, color, 1f).tex(minU, maxV).overlay(overlay).lightmap(light).normal(0, -1, 0).endVertex(); //Bottom left
			builder.pos(matrix4f, startX, startY, startZ).color(color, color, color, 1f).tex(minU, minV).overlay(overlay).lightmap(light).normal(0, -1, 0).endVertex(); //Top left
			builder.pos(matrix4f, endX , startY, startZ).color(color, color, color, 1f).tex(maxU, minV).overlay(overlay).lightmap(light).normal(0, -1, 0).endVertex(); //Top right
			builder.pos(matrix4f, endX, startY, endZ).color(color, color, color, 1f).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(0, -1, 0).endVertex(); //Bottom right
			break;
		}
	}
	

}
