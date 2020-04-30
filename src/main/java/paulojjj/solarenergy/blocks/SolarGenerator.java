package paulojjj.solarenergy.blocks;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.networks.INetwork;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGenerator extends Block {

	private Tier tier;

	public SolarGenerator(Tier tier) {
		super(Material.ROCK);
		setResistance(1.0f);
		setHardness(3.5f);
		this.tier = tier;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new SolarGeneratorTileEntity(tier);
	}

	protected Optional<INetwork<?>> getNetwork(IBlockAccess world, BlockPos pos) {
		SolarGeneratorTileEntity tileEntity = (SolarGeneratorTileEntity)world.getTileEntity(pos);
		if(tileEntity == null || tileEntity.getNetwork() == null) {
			return  Optional.empty();
		}
		return Optional.of(tileEntity.getNetwork());
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
	public boolean isOpaqueCube(IBlockState bs) {
		return false;
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		super.onBlockExploded(world, pos, explosion);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return GuiHandler.openGui(playerIn, worldIn, GUI.SOLAR_GENERATOR, pos);
		//return false;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote) {
			SolarGeneratorTileEntity tileEntity = (SolarGeneratorTileEntity)worldIn.getTileEntity(pos);
			tileEntity.onNeighborChanged(fromPos);
		}
	}
	
}
