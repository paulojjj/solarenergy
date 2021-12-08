package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.registry.Textures;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

@OnlyIn(Dist.CLIENT)
public class SolarGeneratorRenderer extends TileEntityRenderer<SolarGeneratorTileEntity> {
	
	public static final Direction[] HORIZONTALS = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};


	public SolarGeneratorRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	private static TextureAtlasSprite SIDES_TEXTURE;
	
	@Override
	public void render(SolarGeneratorTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if(SIDES_TEXTURE == null) {
			SIDES_TEXTURE = Textures.SOLAR_GENERATOR_SIDE.getSprite();
		}
		SolarGeneratorTileEntity te = (SolarGeneratorTileEntity)tile;
		matrixStack.pushPose();
		
		IVertexBuilder builder = buffer.getBuffer(RenderType.leash());
		
		BlockPos pos = tile.getBlockPos();
		AxisAlignedBB bb = te.getRenderBoundingBox();
		float height = (float)(bb.maxY - bb.minY);
		
		for(Direction facing : HORIZONTALS) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			BlockPos neighborPos = pos.relative(facing);
			BlockState bs = te.getLevel().getBlockState(neighborPos);
			VoxelShape shape = bs.getShape(te.getLevel(), neighborPos);
			if(shape.isEmpty()) {
				continue;
			}
			AxisAlignedBB bbNeighbor = shape.bounds();
			if(bs.canOcclude() ||  bs.isRedstoneConductor(te.getLevel(), neighborPos) || bbNeighbor.maxY <= height) {
				continue;
			}

			float maxY = (float)Math.min(1.0, bbNeighbor.maxY + 0.1);

			Matrix4f matrix4f = matrixStack.last().pose();
			Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE, combinedLight, combinedOverlay, 0.001f, height, 0.001f, 0.999f, maxY, 0.999f, 0.3f, facing);
		}

		matrixStack.popPose();	
	}

}
