package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import paulojjj.solarenergy.registry.Containers;

public class EnergyCableContainer extends EnergyStorageContainer<EnergyCableContainer> {

	public EnergyCableContainer(int windowId, PlayerInventory playerInventory) {
		super(Containers.ENERGY_CABLE.getType(), windowId, playerInventory);
	}
	
	public EnergyCableContainer(int windowId, PlayerInventory playerInventory, PacketBuffer additionalData) {
		super(Containers.ENERGY_CABLE.getType(), windowId, playerInventory, additionalData);
	}

}
