package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryContainer extends Container {
	
	private BatteryTileEntity tileEntity;
	
	
	public BatteryContainer(BatteryTileEntity tileEntity, InventoryPlayer playerInventory) {
		super();
		this.tileEntity = tileEntity;
		tileEntity.onContainerOpened(playerInventory.player);
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

}
