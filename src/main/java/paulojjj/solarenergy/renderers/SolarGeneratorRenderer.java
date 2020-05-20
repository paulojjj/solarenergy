package paulojjj.solarenergy.renderers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
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

	public enum TopResources {
		BASIC, REGULAR, INTERMEDIATE, ADVANCED, ELITE, ULTIMATE;
		
		private ResourceLocation resourceLocation;
		
		private TopResources() {
			String tierName = this.name().toLowerCase();
			this.resourceLocation = new ResourceLocation(Main.MODID, "textures/blocks/" + tierName + "_solar_generator_top.png");
		}
	}
	
	private static final ResourceLocation BOTTOM_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/solar_generator_bottom.png");
	private static final ResourceLocation SIDES_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/solar_generator_side.png");

	@Override
	public void render(SolarGeneratorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableCull();

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		AxisAlignedBB bb = te.getRenderBoundingBox();
		double height = bb.maxY - bb.minY;

		ResourceLocation topResource = TopResources.values()[te.getTier().ordinal()].resourceLocation;
		Render.drawCubeFaces(builder, topResource, 0.0, 0.0, 0.0, 1.0, height, 1.0, EnumFacing.UP);
		Render.drawCubeFaces(builder, BOTTOM_TEXTURE, 0.0, 0.0, 0.0, 1.0, height, 1.0, EnumFacing.DOWN);
		Render.drawCubeFaces(builder, SIDES_TEXTURE, 0.0, 0.0, 0.0, 1.0, height, 1.0, EnumFacing.HORIZONTALS);

		BlockPos pos = te.getPos();

		GlStateManager.disableCull();
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			BlockPos neighborPos = pos.offset(facing);
			IBlockState bs = te.getWorld().getBlockState(neighborPos);
			AxisAlignedBB bbNeighbor = bs.getBoundingBox(te.getWorld(), neighborPos);
			
			if(bs.isFullBlock() || bbNeighbor.maxY <= height) {
				continue;
			}

			double maxY = Math.min(1.0, bbNeighbor.maxY + 0.1);

			Render.drawCubeFaces(builder, SIDES_TEXTURE, 0.001, 0.0, 0.001, 0.999, maxY, 0.999, facing);

		}

		GlStateManager.popMatrix();	
	}

}
