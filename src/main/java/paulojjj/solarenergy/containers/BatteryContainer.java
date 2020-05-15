package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import paulojjj.solarenergy.registry.Containers;

public class BatteryContainer extends EnergyStorageContainer<BatteryContainer> {

	public BatteryContainer(int windowId, PlayerInventory playerInventory) {
		super(Containers.BATTERY.getType(), windowId, playerInventory);
	}
	
	public BatteryContainer(int windowId, PlayerInventory playerInventory, PacketBuffer additionalData) {
		super(Containers.BATTERY.getType(), windowId, playerInventory, additionalData);
	}	

}
