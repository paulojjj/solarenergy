package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity.SolarGeneratorContainerUpdateMessage;

public class SolarGeneratorContainer extends Container implements IMessageListener<SolarGeneratorContainerUpdateMessage> {
	
	private SolarGeneratorTileEntity tileEntity;
	
	private double maxProduction = 0;
	private double activeProduction = 0;
	private double output = 0;
	
	public SolarGeneratorContainer(SolarGeneratorTileEntity tileEntity, EntityPlayer player) {
		super();
		this.tileEntity = tileEntity;
		tileEntity.onContainerOpened(player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		tileEntity.onContainerClosed(playerIn);
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
