package paulojjj.solarenergy.containers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity.SolarGeneratorContainerUpdateMessage;

public class SolarGeneratorContainer extends BaseContainer<SolarGeneratorContainer> implements IMessageListener<SolarGeneratorContainerUpdateMessage> {
	
	private double maxProduction = 0;
	private double activeProduction = 0;
	private double output = 0;
	
	public SolarGeneratorContainer(int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
		super(Containers.SOLAR_GENERATOR.getType(), windowId, playerInventory, additionalData);
	}
	
	public SolarGeneratorContainer(int windowId, Inventory playerInventory) {
		this(windowId, playerInventory, null);
	}
	
	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public void onMessage(SolarGeneratorContainerUpdateMessage message) {
		activeProduction = message.activeProduction;
		maxProduction = message.maxProduction;
		output = message.output;
	}
	
	public double getActiveProduction() {
		return activeProduction;
	}

	public double getMaxProduction() {
		return maxProduction;
	}

	public double getOutput() {
		return output;
	}

}
