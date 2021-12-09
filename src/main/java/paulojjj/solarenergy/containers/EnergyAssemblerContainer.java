package paulojjj.solarenergy.containers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.items.SlotItemHandler;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;
import paulojjj.solarenergy.tiles.EnergyStorageTileEntity.EnergyStorageContainerUpdateMessage;

public class EnergyAssemblerContainer extends BaseContainer<EnergyAssemblerContainer> implements IMessageListener<EnergyStorageContainerUpdateMessage> {

	private int firstPlayerIndex;
	private int lastPlayerIndex;
	
	private EnergyStorageContainerUpdateMessage statusMessage;

	public EnergyAssemblerContainer(int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
		super(Containers.ENERGY_ASSEMBLER.getType(), windowId, playerInventory, additionalData);
	}
	
	public EnergyAssemblerContainer(int windowId, Inventory playerInventory) {
		this(windowId, playerInventory, null);
	}
	
	private void addPlayerSlots(Container playerInventory) {
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
	public boolean stillValid(Player playerIn) {
		return true;
	}

	@Override
	//Shift + Right Click
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack current = slot.getItem();
			previous = current.copy();

			if (index < firstPlayerIndex) {
				// From TE Inventory to Player Inventory
				if (!this.moveItemStackTo(current, firstPlayerIndex, lastPlayerIndex + 1, true))
					return ItemStack.EMPTY;
			} else {
				// From Player Inventory to TE Inventory
				if (!this.moveItemStackTo(current, 0, firstPlayerIndex, false))
					return ItemStack.EMPTY;
			}			

			if (current.getCount() == 0)
				slot.set((ItemStack) ItemStack.EMPTY);
			else
				slot.setChanged();

			if (current.getCount() == previous.getCount())
				return ItemStack.EMPTY;
			slot.onTake(getPlayerInventory().player, current);
		}
		broadcastChanges();
		return previous;
	}
	
	public EnergyStorageContainerUpdateMessage getStatusMessage() {
		return statusMessage;
	}

	@Override
	public void onMessage(EnergyStorageContainerUpdateMessage message) {
		statusMessage = message;
	}
	
	@Override
	public void setPos(BlockPos pos) {
		super.setPos(pos);
		
		EnergyAssemblerTileEntity tileEntity = (EnergyAssemblerTileEntity)super.getTileEntity();
		
		this.addSlot(new SlotItemHandler(tileEntity.getPlayerHandler(), 0, 26, 10));
		this.addSlot(new SlotItemHandler(tileEntity.getPlayerHandler(), 1, 26, 59));
		
		firstPlayerIndex = slots.size();
		addPlayerSlots(getPlayerInventory());
		lastPlayerIndex = slots.size() - 1;
	}



}
