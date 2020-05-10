package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

public class EnergyStorageContainer extends Container implements IMessageListener<EnergyStorageContainerUpdateMessage> {
	
	private EnergyStorageTileEntity tileEntity;
	
	private double energy;
	private double maxEnergy;
	private double input;
	private double output;
	
	
	public EnergyStorageContainer(EnergyStorageTileEntity tileEntity, EntityPlayer player) {
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
