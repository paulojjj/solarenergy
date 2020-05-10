package paulojjj.solarenergy.renderers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class Render {
	
	public static void drawCubeFaces(BufferBuilder builder, ResourceLocation texture, double startX, double startY, double startZ, double endX, double endY, double endZ, EnumFacing... facings) {
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		for(EnumFacing facing : facings) {
			buildSquare(builder, startX, startY, startZ, endX, endY, endZ, facing);
		}
		Tessellator.getInstance().draw();
	}

	public static void buildSquare(BufferBuilder builder, double startX, double startY, double startZ, double endX, double endY, double endZ, EnumFacing facing) {
		double width = endX - startX;
		double height = endY - startY;

		switch(facing) {
		case NORTH:
		case SOUTH:
			width = endX - startX;
			height = endY - startY;
			double z = facing == EnumFacing.SOUTH ? endZ : startZ;
			builder.pos(startX, startY, z).tex(0.0, height).endVertex(); //Bottom left
			builder.pos(endX, startY, z).tex(width, height).endVertex(); //Bottom right
			builder.pos(endX, endY, z).tex(width, 0.0).endVertex(); //Top right
			builder.pos(startX, endY, z).tex(0.0, 0.0).endVertex(); //Top left
			break;
		case EAST:
		case WEST:
			width = endZ - startZ;
			height = endY - startY;
			double x = (facing == EnumFacing.EAST ? endX : startX);
			builder.pos(x, startY, startZ).tex(0.0, height).endVertex(); //Bottom left
			builder.pos(x, startY, endZ).tex(width, height).endVertex(); //Bottom right
			builder.pos(x, endY, endZ).tex(width, 0.0).endVertex(); //Top right
			builder.pos(x, endY, startZ).tex(0.0, 0.0).endVertex(); //Top left
			break;
		case UP:
		case DOWN:
			width = endX - startX;
			height = endZ - startZ;
			double y = (facing == EnumFacing.UP ? endY : startY);
			builder.pos(startX, y, endZ).tex(0.0, height).endVertex(); //Bottom left
			builder.pos(endX, y, endZ).tex(width, height).endVertex(); //Bottom right
			builder.pos(endX , y, startZ).tex(width, 0.0).endVertex(); //Top right
			builder.pos(startX, y, startZ).tex(0.0, 0.0).endVertex(); //Top left
			break;
		}
	}
	

}
