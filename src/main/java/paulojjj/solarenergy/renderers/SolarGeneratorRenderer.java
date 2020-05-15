package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

@OnlyIn(Dist.CLIENT)
public class SolarGeneratorRenderer extends TileEntityRenderer<TileEntity> {
	
	public static final Direction[] HORIZONTALS = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};


	public SolarGeneratorRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

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
	public void render(TileEntity tile, float partialTicks, MatrixStack transformation, IRenderTypeBuffer buffer, int combinedLight, int packetLight) {
		SolarGeneratorTileEntity te = (SolarGeneratorTileEntity)tile;
		transformation.push();

		
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableCull();

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		AxisAlignedBB bb = te.getRenderBoundingBox();
		double height = bb.maxY - bb.minY;

		ResourceLocation topResource = TopResources.values()[te.getTier().ordinal()].resourceLocation;
		Render.drawCubeFaces(builder, topResource, 0.0, 0.0, 0.0, 1.0, height, 1.0, Direction.UP);
		Render.drawCubeFaces(builder, BOTTOM_TEXTURE, 0.0, 0.0, 0.0, 1.0, height, 1.0, Direction.DOWN);
		Render.drawCubeFaces(builder, SIDES_TEXTURE, 0.0, 0.0, 0.0, 1.0, height, 1.0, HORIZONTALS);

		BlockPos pos = te.getPos();

		GlStateManager.disableCull();
		for(Direction facing : HORIZONTALS) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			BlockPos neighborPos = pos.offset(facing);
			BlockState bs = te.getWorld().getBlockState(neighborPos);
			VoxelShape shape = bs.getShape(te.getWorld(), neighborPos);
			AxisAlignedBB bbNeighbor = shape.getBoundingBox();
			if(shape.equals(VoxelShapes.fullCube()) || bbNeighbor.maxY <= height) {
				continue;
			}

			double maxY = Math.min(1.0, bbNeighbor.maxY + 0.1);

			Render.drawCubeFaces(builder, SIDES_TEXTURE, 0.0, 0.0, 0.0, 1.0, maxY, 1.0, facing);

		}

		transformation.pop();	
	}

}
