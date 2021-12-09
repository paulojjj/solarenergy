package paulojjj.solarenergy.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import paulojjj.solarenergy.registry.Containers;

public class EnergyCableContainer extends EnergyStorageContainer<EnergyCableContainer> {

	public EnergyCableContainer(int windowId, Inventory playerInventory) {
		super(Containers.ENERGY_CABLE.getType(), windowId, playerInventory);
	}
	
	public EnergyCableContainer(int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
		super(Containers.ENERGY_CABLE.getType(), windowId, playerInventory, additionalData);
	}

}
