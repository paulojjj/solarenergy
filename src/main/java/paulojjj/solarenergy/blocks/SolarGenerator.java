package paulojjj.solarenergy.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGenerator extends EnergyNetworkBlock<SolarGeneratorTileEntity> {

	public SolarGenerator(Tier tier) {
		configBuilder()
		.resistance(1.0f)
		.gui(GUI.SOLAR_GENERATOR)
		.createTileEntity((x) -> new SolarGeneratorTileEntity(tier))
		.renderType(EnumBlockRenderType.INVISIBLE)
		.init();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public int getLightOpacity(IBlockState state) {
		return 1;
	}
	
}
