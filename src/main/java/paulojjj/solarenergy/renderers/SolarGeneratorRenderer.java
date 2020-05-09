package paulojjj.solarenergy.renderers;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

@SideOnly(Side.CLIENT)
public class SolarGeneratorRenderer extends TileEntitySpecialRenderer<SolarGeneratorTileEntity> {

	private static final ResourceLocation TOP_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/basic_solar_generator_top.png");
	private static final ResourceLocation BOTTOM_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/solar_generator_bottom.png");
	private static final ResourceLocation SIDES_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/solar_generator_side.png");

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
			builder.pos(x, startY, startX).tex(0.0, height).endVertex(); //Bottom left
			builder.pos(x, startY, endX).tex(width, height).endVertex(); //Bottom right
			builder.pos(x, endY, endX).tex(width, 0.0).endVertex(); //Top right
			builder.pos(x, endY, startX).tex(0.0, 0.0).endVertex(); //Top left
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

	@Override
	public void render(SolarGeneratorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableCull();

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		AxisAlignedBB bb = te.getRenderBoundingBox();
		double height = bb.maxY - bb.minY;

		drawCubeFaces(builder, TOP_TEXTURE, 0.0, 0.0, 0.0, 1.0, height, 1.0, EnumFacing.UP);
		drawCubeFaces(builder, BOTTOM_TEXTURE, 0.0, 0.0, 0.0, 1.0, height, 1.0, EnumFacing.DOWN);
		drawCubeFaces(builder, SIDES_TEXTURE, 0.0, 0.0, 0.0, 1.0, height, 1.0, EnumFacing.HORIZONTALS);

		BlockPos pos = te.getPos();

		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			BlockPos neighborPos = pos.offset(facing);
			IBlockState bs = te.getWorld().getBlockState(neighborPos);
			AxisAlignedBB bbNeighbor = bs.getBoundingBox(te.getWorld(), neighborPos);
			if(bbNeighbor.maxY == 1 || bbNeighbor.maxY <= height) {
				continue;
			}

			double maxY = Math.min(1.0, bbNeighbor.maxY + 0.1);

			drawCubeFaces(builder, SIDES_TEXTURE, 0.0, 0.0, 0.0, 1.0, maxY, 1.0, facing);

		}

		GlStateManager.popMatrix();	
	}

}
