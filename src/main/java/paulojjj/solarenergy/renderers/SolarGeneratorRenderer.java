package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.registry.Textures;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

@OnlyIn(Dist.CLIENT)
public class SolarGeneratorRenderer implements BlockEntityRenderer<SolarGeneratorTileEntity> {
	
	public static final Direction[] HORIZONTALS = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
	public static final Direction[] ALL = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};


	public SolarGeneratorRenderer(BlockEntityRendererProvider.Context context) {
	}

	private static TextureAtlasSprite SIDES_TEXTURE;
	
	//Cube depth
	private static final float DEPTH = 0.05f;
	
	public enum ConnectorBoxes {
		
		NORTH(new AABB(0, 0, 0, 1, 1, DEPTH)),
		SOUTH(new AABB(0, 0, 1-DEPTH, 1, 1, 1)),
		WEST(new AABB(0, 0, 0, DEPTH, 1, 1)),
		EAST(new AABB(1-DEPTH, 0, 0, 1, 1, 1));

		private AABB bb;

		private ConnectorBoxes(AABB bb) {
			this.bb = bb;
		}

		public AABB getBoundingBox() {
			return bb;
		}

		public static ConnectorBoxes getBox(Direction facing) {
			return ConnectorBoxes.values()[facing.ordinal()];
		}
	}
	
	@Override
	public void render(SolarGeneratorTileEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if(SIDES_TEXTURE == null) {
			SIDES_TEXTURE = Textures.SOLAR_GENERATOR_SIDE.getSprite();
		}
		SolarGeneratorTileEntity te = (SolarGeneratorTileEntity)tile;
		matrixStack.pushPose();
		
		VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
		
		BlockPos pos = tile.getBlockPos();
		AABB bb = te.getRenderBoundingBox();
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
			AABB bbNeighbor = shape.bounds();
			if(bs.canOcclude() ||  bs.isRedstoneConductor(te.getLevel(), neighborPos) || bbNeighbor.maxY <= height) {
				continue;
			}

			AABB connectorBox = null;
			
			if(facing == Direction.NORTH) {
				connectorBox = ConnectorBoxes.NORTH.bb;
			}
			else if(facing == Direction.SOUTH) {
				connectorBox = ConnectorBoxes.SOUTH.bb;
			}
			else if(facing == Direction.EAST) {
				connectorBox = ConnectorBoxes.EAST.bb;
			}
			else if(facing == Direction.WEST) {
				connectorBox = ConnectorBoxes.WEST.bb;
			}

			if(connectorBox != null) {
				Matrix4f matrix4f = matrixStack.last().pose();
				float maxY = (float)Math.min(1.0, bbNeighbor.maxY + 0.1);
				Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE, combinedLight, combinedOverlay, (float)connectorBox.minX, height, (float)connectorBox.minZ, (float)connectorBox.maxX, maxY, (float)connectorBox.maxZ, 0.5f, ALL);
			}
		}

		matrixStack.popPose();	
	}

}
