package paulojjj.solarenergy.renderers;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.blocks.EnergyCable.Boxes;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

@SideOnly(Side.CLIENT)
public class EnergyCableRenderer extends TileEntitySpecialRenderer<EnergyCableTileEntity> {

	private static final ResourceLocation CENTER_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/energy_cable_center.png");
	private static final ResourceLocation SIDES_TEXTURE_HORIZONTAL = new ResourceLocation(Main.MODID, "textures/blocks/energy_cable_horizontal.png");
	private static final ResourceLocation SIDES_TEXTURE_VERTICAL = new ResourceLocation(Main.MODID, "textures/blocks/energy_cable_vertical.png");

	@Override
	public void render(EnergyCableTileEntity te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableCull();

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		Render.drawCubeFaces(builder, CENTER_TEXTURE, Boxes.CENTER.getBoundingBox(), EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN);

		for(EnumFacing facing : EnumFacing.values()) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			if(facing == EnumFacing.NORTH) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.NORTH.getBoundingBox(), EnumFacing.EAST, EnumFacing.WEST);
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.NORTH.getBoundingBox(), EnumFacing.UP, EnumFacing.DOWN);
			}
			else if(facing == EnumFacing.SOUTH) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.SOUTH.getBoundingBox(), EnumFacing.EAST, EnumFacing.WEST);
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.SOUTH.getBoundingBox(), EnumFacing.UP, EnumFacing.DOWN);
			}
			else if(facing == EnumFacing.EAST) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.EAST.getBoundingBox(), EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.DOWN);
			}
			else if(facing == EnumFacing.WEST) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.WEST.getBoundingBox(), EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.DOWN);
			}
			else if(facing == EnumFacing.UP) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.UP.getBoundingBox(), EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST);
			}
			else if(facing == EnumFacing.DOWN) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.DOWN.getBoundingBox(), EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST);
			}
		}

		GlStateManager.popMatrix();	
	}

}
