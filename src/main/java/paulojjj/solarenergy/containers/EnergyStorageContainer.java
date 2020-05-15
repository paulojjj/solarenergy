package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

public class EnergyStorageContainer<T extends Container> extends BaseContainer<T> implements IMessageListener<EnergyStorageContainerUpdateMessage> {
	
	private double energy;
	private double maxEnergy;
	private double input;
	private double output;
	
	public EnergyStorageContainer(ContainerType<?> type, int windowId, PlayerInventory playerInventory, PacketBuffer additionalData) {
		super(type, windowId, playerInventory, additionalData);
	}
	
	public EnergyStorageContainer(ContainerType<?> type, int windowId, PlayerInventory playerInventory) {
		this(type, windowId, playerInventory, null);
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public void onMessage(EnergyStorageContainerUpdateMessage message) {
		energy = message.energyStored;
		maxEnergy = message.maxEnergyStored;
		input = message.input;
		output = message.output;
	}

	public double getEnergy() {
		return energy;
	}

	public double getMaxEnergy() {
		return maxEnergy;
	}

	public double getInput() {
		return input;
	}

	public double getOutput() {
		return output;
	}

}
