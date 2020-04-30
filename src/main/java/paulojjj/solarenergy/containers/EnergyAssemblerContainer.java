package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

public class EnergyAssemblerContainer extends Container implements IMessageListener<EnergyStorageContainerUpdateMessage> {

	private InventoryPlayer playerInventory;
	private EnergyAssemblerTileEntity tileEntity;
	private int firstPlayerIndex;
	private int lastPlayerIndex;
	private int firstOutputIndex;
	
	private EnergyStorageContainerUpdateMessage statusMessage;

	public EnergyAssemblerContainer(EnergyAssemblerTileEntity tileEntity, InventoryPlayer playerInventory) {
		this.tileEntity = tileEntity;
		this.playerInventory = playerInventory;

		this.addSlotToContainer(new SlotItemHandler(tileEntity.getItemHandler(), 0, 26, 10));
		firstOutputIndex = inventorySlots.size();;
		this.addSlotToContainer(new SlotItemHandler(tileEntity.getItemHandler(), 1, 26, 59));
		
		firstPlayerIndex = inventorySlots.size();
		addPlayerSlots(playerInventory);
		lastPlayerIndex = inventorySlots.size() - 1;
		
		tileEntity.onContainerOpened(playerInventory.player);		
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		tileEntity.onContainerClosed(playerInventory.player);
	}
	
	private void addPlayerSlots(IInventory playerInventory) {
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k)
		{
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
		}

	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	//Shift + Right Click
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack current = slot.getStack();
			previous = current.copy();

			if (index < firstPlayerIndex) {
				// From TE Inventory to Player Inventory
				if (!this.mergeItemStack(current, firstPlayerIndex, lastPlayerIndex + 1, true))
					return ItemStack.EMPTY;
			} else {
				// From Player Inventory to TE Inventory
				if (!this.mergeItemStack(current, 0, firstOutputIndex, false))
					return ItemStack.EMPTY;
			}			

			if (current.getCount() == 0)
				slot.putStack((ItemStack) ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (current.getCount() == previous.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerInventory.player, current);
		}
		return previous;
	}
	
	public EnergyStorageContainerUpdateMessage getStatusMessage() {
		return statusMessage;
	}

	@Override
	public void onMessage(EnergyStorageContainerUpdateMessage message) {
		statusMessage = message;
	}



}
