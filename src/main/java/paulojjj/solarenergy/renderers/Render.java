package paulojjj.solarenergy.renderers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class Render {
	
	public static void drawCubeFaces(BufferBuilder builder, ResourceLocation texture, AxisAlignedBB bb, EnumFacing... facings) {
		drawCubeFaces(builder, texture, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, facings);		
	}
	
	public static void drawCubeFaces(BufferBuilder builder, ResourceLocation texture, double startX, double startY, double startZ, double endX, double endY, double endZ, EnumFacing... facings) {
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		for(EnumFacing facing : facings) {
			buildSquare(builder, startX, startY, startZ, endX, endY, endZ, facing);
		}
		Tessellator.getInstance().draw();
	}

	public static void buildSquare(BufferBuilder builder, double startX, double startY, double startZ, double endX, double endY, double endZ, EnumFacing facing) {
		switch(facing) {
		case NORTH:
			builder.pos(startX, startY, startZ).tex(0.0, 1.0).endVertex(); //Bottom left
			builder.pos(startX, endY, startZ).tex(0.0, 0.0).endVertex(); //Top left
			builder.pos(endX, endY, startZ).tex(1.0, 0.0).endVertex(); //Top right
			builder.pos(endX, startY, startZ).tex(1.0, 1.0).endVertex(); //Bottom right
			break;
		case SOUTH:
			builder.pos(startX, startY, endZ).tex(0.0, 1.0).endVertex(); //Bottom left
			builder.pos(endX, startY, endZ).tex(1.0, 1.0).endVertex(); //Bottom right
			builder.pos(endX, endY, endZ).tex(1.0, 0.0).endVertex(); //Top right
			builder.pos(startX, endY, endZ).tex(0.0, 0.0).endVertex(); //Top left
			break;
		case EAST:
			builder.pos(endX, startY, startZ).tex(0.0, 1.0).endVertex(); //Bottom left
			builder.pos(endX, endY, startZ).tex(0.0, 0.0).endVertex(); //Top left
			builder.pos(endX, endY, endZ).tex(1.0, 0.0).endVertex(); //Top right
			builder.pos(endX, startY, endZ).tex(1.0, 1.0).endVertex(); //Bottom right
			break;
		case WEST:
			builder.pos(startX, startY, startZ).tex(0.0, 1.0).endVertex(); //Bottom left
			builder.pos(startX, startY, endZ).tex(1.0, 1.0).endVertex(); //Bottom right
			builder.pos(startX, endY, endZ).tex(1.0, 0.0).endVertex(); //Top right
			builder.pos(startX, endY, startZ).tex(0.0, 0.0).endVertex(); //Top left
			break;
		case UP:
			builder.pos(startX, endY, endZ).tex(0.0, 1.0).endVertex(); //Bottom left
			builder.pos(endX, endY, endZ).tex(1.0, 1.0).endVertex(); //Bottom right
			builder.pos(endX , endY, startZ).tex(1.0, 0.0).endVertex(); //Top right
			builder.pos(startX, endY, startZ).tex(0.0, 0.0).endVertex(); //Top left
			break;
		case DOWN:
			builder.pos(startX, startY, endZ).tex(0.0, 1.0).endVertex(); //Bottom left
			builder.pos(startX, startY, startZ).tex(0.0, 0.0).endVertex(); //Top left
			builder.pos(endX , startY, startZ).tex(1.0, 0.0).endVertex(); //Top right
			builder.pos(endX, startY, endZ).tex(1.0, 1.0).endVertex(); //Bottom right
			break;
		}
	}
	

}
