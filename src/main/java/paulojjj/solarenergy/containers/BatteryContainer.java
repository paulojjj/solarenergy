package paulojjj.solarenergy.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import paulojjj.solarenergy.registry.Containers;

public class BatteryContainer extends EnergyStorageContainer<BatteryContainer> {

	public BatteryContainer(int windowId, Inventory playerInventory) {
		super(Containers.BATTERY.getType(), windowId, playerInventory);
	}
	
	public BatteryContainer(int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
		super(Containers.BATTERY.getType(), windowId, playerInventory, additionalData);
	}	

}
