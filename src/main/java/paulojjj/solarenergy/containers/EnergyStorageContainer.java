package paulojjj.solarenergy.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

public class EnergyStorageContainer<T extends AbstractContainerMenu> extends BaseContainer<T> implements IMessageListener<EnergyStorageContainerUpdateMessage> {
	
	private double energy;
	private double maxEnergy;
	private double input;
	private double output;
	
	public EnergyStorageContainer(MenuType<?> type, int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
		super(type, windowId, playerInventory, additionalData);
	}
	
	public EnergyStorageContainer(MenuType<?> type, int windowId, Inventory playerInventory) {
		this(type, windowId, playerInventory, null);
	}
	
	@Override
	public boolean stillValid(Player playerIn) {
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
