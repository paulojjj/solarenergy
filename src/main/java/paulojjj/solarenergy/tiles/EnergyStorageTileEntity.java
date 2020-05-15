package paulojjj.solarenergy.tiles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.NBT;

public abstract class EnergyStorageTileEntity extends BaseTileEntity implements IUltraEnergyStorage, ITickableTileEntity {

	protected double energy = 0;
	protected double maxEnergy = 0;

	protected double sentSinceLastUpdate;
	protected double receivedSinceLastUpdate;
	protected double input;
	protected double output;

	public EnergyStorageTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public static class EnergyStorageContainerUpdateMessage {
		public double energyStored;
		public double maxEnergyStored;
		public double input;
		public double output;

		public EnergyStorageContainerUpdateMessage() {
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return LazyOptional.of(() -> (T) this);
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
	public void read(CompoundNBT compound) {
		super.read(compound);
		energy = compound.getDouble(NBT.ENERGY);
		maxEnergy = compound.getDouble(NBT.MAX_ENERGY);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound = super.write(compound);
		compound.putDouble(NBT.ENERGY, energy);
		compound.putDouble(NBT.MAX_ENERGY, maxEnergy);
		return compound;
	}

	@Override
	public void tick() {
		input = receivedSinceLastUpdate;
		output = sentSinceLastUpdate;

		receivedSinceLastUpdate = 0;
		sentSinceLastUpdate = 0;
		
		super.tick();
	}

	@Override
	public String toString() {
		return super.toString() + " [position=" + pos + "]";
	}

}
