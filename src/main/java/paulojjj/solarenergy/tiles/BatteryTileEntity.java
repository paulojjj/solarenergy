package paulojjj.solarenergy.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.blocks.Battery;
import paulojjj.solarenergy.networks.BatteryNetwork;
import paulojjj.solarenergy.registry.TileEntities;

public class BatteryTileEntity extends EnergyNetworkTileEntity implements IUltraEnergyStorage {

	private Tier tier;

	public BatteryTileEntity() {
		this(Tier.BASIC);
	}

	public BatteryTileEntity(Tier tier) {
		super(TileEntities.BATTERY.getType());
		setTier(tier);
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		int tierInt = tier.ordinal();
		setMaxUltraEnergyStored(Math.pow(10, tierInt < Tier.BASIC_DENSE.ordinal() ? tierInt : tierInt + 2) * 10000);
		markDirty();
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
		return world.getBlockState(pos).get(Battery.FACING);
	}

	@Override
	public void read(BlockState blockState, CompoundNBT compound) {
		super.read(blockState, compound);
		int tierValue = compound.getInt(NBT.TIER);
		energy = compound.getDouble(NBT.ENERGY);
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound = super.write(compound);
		compound.putInt(NBT.TIER, tier.ordinal());
		compound.putDouble(NBT.ENERGY, energy);
		return compound;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public Class<?> getNetworkClass() {
		return BatteryNetwork.class;
	}

}
