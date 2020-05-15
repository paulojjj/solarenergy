package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.blocks.EnergyCable.Boxes;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;

@OnlyIn(Dist.CLIENT)
public class EnergyCableRenderer extends TileEntityRenderer<TileEntity> {

	public EnergyCableRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	private static final ResourceLocation CENTER_TEXTURE = new ResourceLocation(Main.MODID, "textures/blocks/energy_cable_center.png");
	private static final ResourceLocation SIDES_TEXTURE_HORIZONTAL = new ResourceLocation(Main.MODID, "textures/blocks/energy_cable_horizontal.png");
	private static final ResourceLocation SIDES_TEXTURE_VERTICAL = new ResourceLocation(Main.MODID, "textures/blocks/energy_cable_vertical.png");

	@Override
	public void render(TileEntity tile, float partialTicks, MatrixStack transformation, IRenderTypeBuffer buffer, int combinedLight, int packetLight) {
		EnergyCableTileEntity te = (EnergyCableTileEntity)tile;
		//GlStateManager.pushMatrix();
		transformation.push();
		
		//BlockPo
		//transformation.translate(te.getP, p_227861_3_, p_227861_5_);
		//GlStateManager.translate(x, y, z);
		
		RenderHelper.enableStandardItemLighting();
		//GlStateManager.disableLighting();
		GlStateManager.enableCull();

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		Render.drawCubeFaces(builder, CENTER_TEXTURE, Boxes.CENTER.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN);

		for(Direction facing : Direction.values()) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			if(facing == Direction.NORTH) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.NORTH.getBoundingBox(), Direction.EAST, Direction.WEST);
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.NORTH.getBoundingBox(), Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.SOUTH) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.SOUTH.getBoundingBox(), Direction.EAST, Direction.WEST);
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.SOUTH.getBoundingBox(), Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.EAST) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.EAST.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.WEST) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_HORIZONTAL, Boxes.WEST.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN);
			}
			else if(facing == Direction.UP) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.UP.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
			}
			else if(facing == Direction.DOWN) {
				Render.drawCubeFaces(builder, SIDES_TEXTURE_VERTICAL, Boxes.DOWN.getBoundingBox(), Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
			}
		}
		
		transformation.pop();
		//GlStateManager.popMatrix();	
	}
	
	

}
