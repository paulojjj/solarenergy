package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
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
		.createTileEntity((x, y) -> new BatteryTileEntity(tier, x, y))
		.getDrops(this::setDropNBT)
		.init();
	}

	@Override
	protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	public void setDropNBT(List<ItemStack> drops, BlockEntity tileEntity) {
		BatteryTileEntity te = (BatteryTileEntity)tileEntity;
		ItemStack stack = drops.iterator().next();
		CompoundTag nbt = new CompoundTag();
		stack.setTag(nbt);
		nbt.putDouble(NBT.ENERGY, te.getUltraEnergyStored());
		nbt.putDouble(NBT.MAX_ENERGY, te.getMaxUltraEnergyStored());
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		CompoundTag nbt = stack.getTag();
		if(nbt != null) {
			double energy = nbt.getDouble(NBT.ENERGY);
			double maxEnergy = nbt.getDouble(NBT.MAX_ENERGY);
			BatteryTileEntity te = (BatteryTileEntity)worldIn.getBlockEntity(pos);
			te.setUltraEnergyStored(energy);
			te.setMaxUltraEnergyStored(maxEnergy);
		}

		Direction facing = placer.getDirection().getOpposite();
		int height = Math.round(placer.getXRot());
		if (height >= 65) {
			facing = Direction.UP;
		} else if (height <= -30) {
			facing = Direction.DOWN;
		}
		worldIn.setBlockAndUpdate(pos, state.setValue(FACING, facing));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
			InteractionHand handIn, BlockHitResult hit) {
		if(player.isCrouching()) {
			worldIn.setBlockAndUpdate(pos, state.setValue(FACING, hit.getDirection()));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
}
