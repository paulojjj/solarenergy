package paulojjj.solarenergy.tiles;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.IEnergyContainer;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.net.BatteryMessage;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.net.PacketManager;

public class BatteryTileEntity extends TileEntity implements IUltraEnergyStorage, ITickable, IEnergyContainer, IMessageListener<BatteryMessage> {

	private Tier tier;
	private double capacity = 0;
	private double energy = 0;
	private double in = 0;
	private double out = 0;
	
	private Set<EntityPlayer> playersUsing = new HashSet<>();

	public BatteryTileEntity() {
		this(Tier.BASIC);
	}

	public BatteryTileEntity(Tier tier) {
		super();
		setTier(tier);
	}
	
	protected void setTier(Tier tier) {
		this.tier = tier;
		capacity = Math.pow(10, tier.ordinal()) * 10000;
		markDirty();
	}
	
	@Override
	public double getEnergy() {
		return energy;
	}	
	
	@Override
	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	@Override
	public double getInputRate() {
		return in;
	}

	@Override
	public double getOutputRate() {
		return out;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	public class OutputEnergyStorage implements IEnergyStorage {

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return BatteryTileEntity.this.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			return BatteryTileEntity.this.getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return false;
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			if(facing == getOuputFacing()) {
				return (T) new OutputEnergyStorage();
			}
			return (T) this;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return (int)receiveUltraEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public double receiveUltraEnergy(double maxReceive, boolean simulate) {
		if(world.isRemote) {
			return 0;
		}
		if(energy >= capacity) {
			return 0;
		}
		double received = Math.min(maxReceive, capacity - energy);
		if(!simulate) {
			energy += received;
			in += received;
			markDirty();
		}
		return received;
	}

	@Override
	public double extractUltraEnergy(double maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public double getUltraEnergyStored() {
		return energy;
	}

	@Override
	public double getMaxUltraEnergyStored() {
		return capacity;
	}
	
	@Override
	public int getEnergyStored() {
		if(energy > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int)energy;
	}

	@Override
	public int getMaxEnergyStored() {
		if(capacity > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int)capacity;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return !world.isRemote;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}
	
	public EnumFacing getOuputFacing() {
		return world.getBlockState(pos).getValue(BlockDirectional.FACING);
	}

	@Override
	public void update() {
		if(world.isRemote) {
			return;
		}

		EnumFacing output = getOuputFacing();
		TileEntity tile = world.getTileEntity(getPos().offset(output));
		if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, output.getOpposite())) {
			IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, output.getOpposite());
			if(energyStorage.canReceive()) {
				double sent = 0;
				if(energyStorage instanceof IUltraEnergyStorage) {
					sent = ((IUltraEnergyStorage)energyStorage).receiveUltraEnergy(energy, false);
				}
				else {
					sent = energyStorage.receiveEnergy((int)Math.min(energy, Integer.MAX_VALUE), false);
				}
				out += sent;
				energy -= sent;
			}
		}
		markDirty();

		for(EntityPlayer player: playersUsing) {
			PacketManager.sendTileEntityMessage(this, (EntityPlayerMP)player, new BatteryMessage(energy, capacity, in, out));
		}
		out = 0;
		in = 0;
}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int tierValue = compound.getInteger("tier");
		energy = compound.getDouble("energy");
		Tier tier =  Tier.values()[tierValue];
		setTier(tier);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setInteger("tier", tier.ordinal());
		compound.setDouble("energy", energy);
		return compound;
	}

	public void onContainerOpened(EntityPlayer player) {
		playersUsing.add(player);
	}

	public void onContainerClosed(EntityPlayer player) {
		playersUsing.remove(player);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public void onMessage(BatteryMessage message) {
		energy = message.getEnergy();
		capacity = message.getCapacity();
		in = message.getIn();
		out = message.getOut();
	}
}
