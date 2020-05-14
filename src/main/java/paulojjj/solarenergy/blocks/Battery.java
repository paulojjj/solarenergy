package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.gui.GuiHandler.GUI;
import paulojjj.solarenergy.tiles.BatteryTileEntity;


public class Battery extends EnergyNetworkBlock<BatteryTileEntity> {

    public static PropertyDirection FACING = PropertyDirection.create("facing");

	public Battery(Tier tier) {
		super();
		configBuilder()
			.with(FACING, EnumFacing.NORTH)
			.gui(GUI.BATTERY)
			.createTileEntity((x) -> new BatteryTileEntity(tier))
			.getDrops(this::setDropNBT)
			.init();
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
	
	public void setDropNBT(List<ItemStack> drops, TileEntity tileEntity) {
		BatteryTileEntity te = (BatteryTileEntity)tileEntity;
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

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.isSneaking()) {
			worldIn.setBlockState(pos, state.withProperty(FACING, facing));
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
}
