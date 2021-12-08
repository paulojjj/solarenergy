package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.tiles.BatteryTileEntity;


public class Battery extends EnergyNetworkBlock<BatteryTileEntity> {

	public static EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);

	public Battery(Tier tier) {
		super(propertiesBuilder());
		configBuilder()
		.property(FACING)
		.guiContainer(Containers.BATTERY)
		.createTileEntity((x) -> new BatteryTileEntity(tier))
		.getDrops(this::setDropNBT)
		.init();
	}

	@Override
	protected void createBlockStateDefinition(net.minecraft.state.StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	public void setDropNBT(List<ItemStack> drops, TileEntity tileEntity) {
		BatteryTileEntity te = (BatteryTileEntity)tileEntity;
		ItemStack stack = drops.iterator().next();
		CompoundNBT nbt = new CompoundNBT();
		stack.setTag(nbt);
		nbt.putDouble(NBT.ENERGY, te.getUltraEnergyStored());
		nbt.putDouble(NBT.MAX_ENERGY, te.getMaxUltraEnergyStored());
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		CompoundNBT nbt = stack.getTag();
		if(nbt != null) {
			double energy = nbt.getDouble(NBT.ENERGY);
			double maxEnergy = nbt.getDouble(NBT.MAX_ENERGY);
			BatteryTileEntity te = (BatteryTileEntity)worldIn.getBlockEntity(pos);
			te.setUltraEnergyStored(energy);
			te.setMaxUltraEnergyStored(maxEnergy);
		}

		Direction facing = placer.getDirection().getOpposite();
		int height = Math.round(placer.xRot);
		if (height >= 65) {
			facing = Direction.UP;
		} else if (height <= -30) {
			facing = Direction.DOWN;
		}
		worldIn.setBlockAndUpdate(pos, state.setValue(FACING, facing));
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(player.isCrouching()) {
			worldIn.setBlockAndUpdate(pos, state.setValue(FACING, hit.getDirection()));
			return ActionResultType.SUCCESS;
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
}
