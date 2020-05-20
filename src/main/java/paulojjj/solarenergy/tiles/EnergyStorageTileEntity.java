package paulojjj.solarenergy.tiles;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.net.PacketManager;

public abstract class EnergyStorageTileEntity extends TileEntity implements IUltraEnergyStorage, ITickable {

	protected double energy = 0;
	protected double maxEnergy = 0;
	
	protected double sentSinceLastUpdate;
	protected double receivedSinceLastUpdate;
	protected double input;
	protected double output;
	
	private Set<EntityPlayer> playersUsing = new HashSet<>();

	public static class EnergyStorageContainerUpdateMessage {
		public double energyStored;
		public double maxEnergyStored;
		public double input;
		public double output;

		public EnergyStorageContainerUpdateMessage() {
		}

		public EnergyStorageContainerUpdateMessage(EnergyStorageTileEntity te) {
			this(te.getUltraEnergyStored(), te.getMaxUltraEnergyStored(), te.getInput(), te.getOutput());
		}
		
		public EnergyStorageContainerUpdateMessage(double energyStored, double maxEnergyStored, double input,
				double output) {
			super();
			this.energyStored = energyStored;
			this.maxEnergyStored = maxEnergyStored;
			this.input = input;
			this.output = output;
		}
	}
	
	protected Object getContainerUpdateMessage() {
		double energy = getUltraEnergyStored();
		double maxEnergy = getMaxUltraEnergyStored();
		double input = getInput();
		double output = getOutput();
		return new EnergyStorageContainerUpdateMessage(energy, maxEnergy, input, output);		
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return (T) this;
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public double extractUltraEnergy(double maxExtract, boolean simulate) {
		if(energy == 0 || !canExtract()) {
			return 0;
		}
		double sent = Math.min(maxExtract, energy);
		if(!simulate && sent > 0) {
			energy -= sent;
			sentSinceLastUpdate += sent;
			markDirty();			
		}
		return sent;
	}

	@Override
	public double receiveUltraEnergy(double maxReceive, boolean simulate) {
		if(maxEnergy == 0 || energy == maxEnergy || !canReceive()) {
			return 0;
		}
		double received = Math.min(maxReceive, maxEnergy - energy);
		if(!simulate && received > 0) {
			energy += received;
			receivedSinceLastUpdate += received;
			markDirty();
		}
		return received;
	}

	@Override
	public double getUltraEnergyStored() {
		return energy;
	}

	public void setUltraEnergyStored(double value) {
		energy = value;
	}

	@Override
	public double getMaxUltraEnergyStored() {
		return maxEnergy;
	}

	public void setMaxUltraEnergyStored(double value) {
		maxEnergy = value;
	}

	public double getInput() {
		return input;
	}

	public double getOutput() {
		return output;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy = compound.getDouble(NBT.ENERGY);
		maxEnergy = compound.getDouble(NBT.MAX_ENERGY);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setDouble(NBT.ENERGY, energy);
		compound.setDouble(NBT.MAX_ENERGY, maxEnergy);
		return compound;
	}
	
	@Override
	public void update() {
		if(world.isRemote) {
			return;
		}
		input = receivedSinceLastUpdate;
		output = sentSinceLastUpdate;
		
		receivedSinceLastUpdate = 0;
		sentSinceLastUpdate = 0;
		
		for(EntityPlayer player : playersUsing) {
			PacketManager.sendContainerUpdateMessage((EntityPlayerMP)player, getContainerUpdateMessage());
		}
	}
	
	public void onContainerOpened(EntityPlayer player) {
		if(!world.isRemote) {
			playersUsing.add(player);
		}
	}

	public void onContainerClosed(EntityPlayer player) {
		if(!world.isRemote) {
			playersUsing.remove(player);
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " [position=" + pos + "]";
	}

}
