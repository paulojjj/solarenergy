package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

public class EnergyAssemblerContainer extends BaseContainer<EnergyAssemblerContainer> implements IMessageListener<EnergyStorageContainerUpdateMessage> {

	private PlayerInventory playerInventory;
	private int firstPlayerIndex;
	private int lastPlayerIndex;
	
	private EnergyStorageContainerUpdateMessage statusMessage;

	public EnergyAssemblerContainer(int windowId, PlayerInventory playerInventory, PacketBuffer additionalData) {
		super(Containers.ENERGY_ASSEMBLER.getType(), windowId, playerInventory, additionalData);
		this.playerInventory = playerInventory;

		EnergyAssemblerTileEntity tileEntity = (EnergyAssemblerTileEntity)super.getTileEntity();
		
		this.addSlot(new SlotItemHandler(tileEntity.getPlayerHandler(), 0, 26, 10));
		this.addSlot(new SlotItemHandler(tileEntity.getPlayerHandler(), 1, 26, 59));
		
		firstPlayerIndex = inventorySlots.size();
		addPlayerSlots(playerInventory);
		lastPlayerIndex = inventorySlots.size() - 1;
	}
	
	public EnergyAssemblerContainer(int windowId, PlayerInventory playerInventory) {
		this(windowId, playerInventory, null);
	}
	
	private void addPlayerSlots(IInventory playerInventory) {
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k)
		{
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}

	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	//Shift + Right Click
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
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
				if (!this.mergeItemStack(current, 0, firstPlayerIndex, false))
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
		detectAndSendChanges();
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
