package paulojjj.solarenergy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.tiles.BatteryTileEntity;


public class Battery extends BlockDirectional {

	private Tier tier;

	public Battery(Tier tier) {
		super(Material.ROCK);
		setResistance(50.0f);
		setHardness(3.5f);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
		return new BatteryTileEntity(tier);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer( this, new IProperty[] {FACING});

	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		BatteryTileEntity te = (BatteryTileEntity)world.getTileEntity(pos);
		ItemStack stack = drops.iterator().next();
		NBTTagCompound nbt = new NBTTagCompound();
		stack.setTagCompound(nbt);
		nbt.setDouble(NBT.ENERGY, te.getUltraEnergyStored());
		nbt.setDouble(NBT.MAX_ENERGY, te.getMaxUltraEnergyStored());
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			double energy = stack.getTagCompound().getDouble(NBT.ENERGY);
			double maxEnergy = stack.getTagCompound().getDouble(NBT.MAX_ENERGY);
			BatteryTileEntity te = (BatteryTileEntity)worldIn.getTileEntity(pos);
			te.setUltraEnergyStored(energy);
			te.setMaxUltraEnergyStored(maxEnergy);
		}
        
        EnumFacing facing = placer.getHorizontalFacing().getOpposite();
        int height = Math.round(placer.rotationPitch);
        if (height >= 65) {
        	facing = EnumFacing.UP;
        } else if (height <= -30) {
        	facing = EnumFacing.DOWN;
        }
		worldIn.setBlockState(pos, state.withProperty(FACING, facing));
	}	

	/*	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.getClosestPlayer(pos.get, posY, posZ, distance, spectator)
		// TODO Auto-generated method stub
		super.onBlockAdded(worldIn, pos, state);
	}*/


	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) {
			return true;
		}
		if(playerIn.isSneaking()) {
			worldIn.setBlockState(pos, state.withProperty(FACING, facing));
			return true;
		}
		return GuiHandler.openGui(playerIn, worldIn, GUI.BATTERY, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote) {
			BatteryTileEntity tileEntity = (BatteryTileEntity)worldIn.getTileEntity(pos);
			tileEntity.onNeighborChanged(fromPos);
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player,
			boolean willHarvest) {
		//Delay deletion of the block until after getDrops
		if(willHarvest) return true;
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		worldIn.setBlockToAir(pos);
	}


}
