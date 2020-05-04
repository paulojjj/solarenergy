package paulojjj.solarenergy.tiles;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.networks.BatteryNetwork;

public class BatteryTileEntity extends EnergyNetworkTileEntity implements IUltraEnergyStorage, ITickable {

	private Tier tier;

	public BatteryTileEntity() {
		this(Tier.BASIC);
	}

	public BatteryTileEntity(Tier tier) {
		super();
		setTier(tier);
	}

	protected void setTier(Tier tier) {
		this.tier = tier;
		int tierInt = tier.ordinal();
		setMaxUltraEnergyStored(Math.pow(10, tierInt < Tier.BASIC_DENSE.ordinal() ? tierInt : tierInt + 1) * 10000);
		markDirty();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return super.hasCapability(capability, facing);
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
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY && facing == getOuputFacing()) {
			return (T) new OutputEnergyStorage();
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

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	public EnumFacing getOuputFacing() {
		return world.getBlockState(pos).getValue(BlockDirectional.FACING);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int tierValue = compound.getInteger(NBT.TIER);
		energy = compound.getDouble(NBT.ENERGY);
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setInteger(NBT.TIER, tier.ordinal());
		compound.setDouble(NBT.ENERGY, energy);
		return compound;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public Class<?> getNetworkClass() {
		return BatteryNetwork.class;
	}

}
