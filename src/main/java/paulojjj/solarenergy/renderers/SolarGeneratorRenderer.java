package paulojjj.solarenergy.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.registry.Blocks;
import paulojjj.solarenergy.registry.Textures;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

@OnlyIn(Dist.CLIENT)
public class SolarGeneratorRenderer extends TileEntityRenderer<SolarGeneratorTileEntity> {
	
	public static final Direction[] HORIZONTALS = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};


	public SolarGeneratorRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	private static TextureAtlasSprite SIDES_TEXTURE;
	
	private Blocks getBlock(Tier tier) {
		switch(tier) {
		case BASIC:
			return Blocks.BASIC_SOLAR_GENERATOR;
		case REGULAR:
			return Blocks.REGULAR_SOLAR_GENERATOR;
		case INTERMEDIATE:
			return Blocks.INTERMEDIATE_SOLAR_GENERATOR;
		case ADVANCED:
			return Blocks.ADVANCED_SOLAR_GENERATOR;
		case ELITE:
			return Blocks.ELITE_SOLAR_GENERATOR;
		case ULTIMATE:
			return Blocks.ULTIMATE_SOLAR_GENERATOR;
		default:
			throw new RuntimeException("Invalid tier: " + tier);
		}
	}
	
	private ItemStack getItemStack(Tier tier) {
		Item item = getBlock(tier).getItemBlock(); 
		return new ItemStack(item);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(SolarGeneratorTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if(SIDES_TEXTURE == null) {
			SIDES_TEXTURE = Textures.SOLAR_GENERATOR_SIDE.getSprite();
		}
		SolarGeneratorTileEntity te = (SolarGeneratorTileEntity)tile;
		matrixStack.push();
		
		//matrixStack.translate(0.5f, 0.5f, 0.5f);
		
		//Minecraft.getInstance().getItemRenderer().renderItem(getItemStack(te.getTier()), TransformType.NONE, combinedLight, combinedOverlay, matrixStack, buffer);
		
		IVertexBuilder builder = buffer.getBuffer(RenderType.getLeash());
		
		BlockPos pos = tile.getPos();
		AxisAlignedBB bb = te.getRenderBoundingBox();
		float height = (float)(bb.maxY - bb.minY);
		
		//matrixStack.translate(-0.5f, -0.5f, -0.5f);
		
		for(Direction facing : HORIZONTALS) {
			if(!te.hasStorage(facing)) {
				continue;
			}

			BlockPos neighborPos = pos.offset(facing);
			BlockState bs = te.getWorld().getBlockState(neighborPos);
			VoxelShape shape = bs.getShape(te.getWorld(), neighborPos);
			AxisAlignedBB bbNeighbor = shape.getBoundingBox();
			
			if(bs.isSolid() || bbNeighbor.maxY <= height) {
				continue;
			}

			float maxY = (float)Math.min(1.0, bbNeighbor.maxY + 0.1);

			Matrix4f matrix4f = matrixStack.getLast().getMatrix();
			Render.drawCubeFaces(matrix4f, builder, SIDES_TEXTURE, combinedLight, combinedOverlay, 0, height, 0, 1, maxY, 1, facing);
		}

		matrixStack.pop();	
	}

}
