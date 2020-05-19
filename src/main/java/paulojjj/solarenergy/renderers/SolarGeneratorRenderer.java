package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
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
		matrixStack.push();
		
		IVertexBuilder builder = buffer.getBuffer(RenderType.getLeash());
		
		BlockPos pos = tile.getPos();
		AxisAlignedBB bb = te.getRenderBoundingBox();
		float height = (float)(bb.maxY - bb.minY);
		
		for(Direction facing : HORIZONTALS) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			BlockPos neighborPos = pos.offset(facing);
			BlockState bs = te.getWorld().getBlockState(neighborPos);
			VoxelShape shape = bs.getShape(te.getWorld(), neighborPos);
			if(shape.isEmpty()) {
				continue;
			}
			AxisAlignedBB bbNeighbor = shape.getBoundingBox();
			
			if(bs.isSolid() || bbNeighbor.maxY <= height) {
				continue;
			}

			float maxY = (float)Math.min(1.0, bbNeighbor.maxY + 0.1);

			Matrix4f matrix4f = matrixStack.getLast().getMatrix();
			Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE, combinedLight, combinedOverlay, 0, height, 0, 1, maxY, 1, 0.3f, facing);
		}

		matrixStack.pop();	
	}

}
