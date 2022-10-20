package paulojjj.solarenergy.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.Config;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.blocks.Battery;
import paulojjj.solarenergy.networks.BatteryNetwork;
import paulojjj.solarenergy.registry.TileEntities;

public class BatteryTileEntity extends EnergyNetworkTileEntity implements IUltraEnergyStorage {

	private Tier tier;

	public BatteryTileEntity(BlockPos pos, BlockState state) {
		this(Tier.BASIC, pos, state);
	}

	public BatteryTileEntity(Tier tier, BlockPos pos, BlockState state) {
		super(TileEntities.BATTERY.getType(), pos, state);
		setTier(tier);
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		int tierInt = tier.ordinal();
		setMaxUltraEnergyStored(Math.pow(10, tierInt + 2) * 10000 * Config.getInstance().getBatteryMultiplier(tier));
		setChanged();
	}

	public class OutputEnergyStorage implements IUltraEnergyStorage {

		@Override
		public double receiveUltraEnergy(double maxReceive, boolean simulate) {
			return 0;
		}

		@Override
		public double extractUltraEnergy(double maxExtract, boolean simulate) {
			return delegate.extractUltraEnergy(maxExtract, simulate);
		}

		@Override
		public double getUltraEnergyStored() {
			return delegate.getUltraEnergyStored();
		}

		@Override
		public double getMaxUltraEnergyStored() {
			return delegate.getMaxUltraEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityEnergy.ENERGY && facing == getOuputFacing()) {
			return LazyOptional.of(() -> (T) new OutputEnergyStorage());
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	public Direction getOuputFacing() {
		return level.getBlockState(worldPosition).getValue(Battery.FACING);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		int tierValue = compound.getInt(NBT.TIER);
		energy = compound.getDouble(NBT.ENERGY);
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		compound = super.save(compound);
		compound.putInt(NBT.TIER, tier.ordinal());
		compound.putDouble(NBT.ENERGY, energy);
		return compound;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getTag());
	}
	
	@Override
	public Class<?> getNetworkClass() {
		return BatteryNetwork.class;
	}

}
