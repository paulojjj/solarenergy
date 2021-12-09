package paulojjj.solarenergy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerWrapper extends ItemStackHandler {
	
	public enum HandlerType {
		PLAYER, CAPABILITY, INTERNAL
	}
	
	public enum SlotType {
		INPUT, OUTPUT
	}
	
	private ItemStackHandler delegate;
	private HandlerType type;
	private List<SlotType> slotTypes;
	
	public ItemStackHandlerWrapper(ItemStackHandler stackHandler, HandlerType type, SlotType... slotTypes) {
		this.delegate = stackHandler;
		this.type = type;
		this.slotTypes = Arrays.asList(slotTypes);
	}
	
	public static HandlerBuilder builder(ItemStackHandler delegate) {
		return new HandlerBuilderImpl(delegate);
	}
	
	public static interface HandlerBuilder {
		public HandlerInitialSlotBuilder type(HandlerType type);
	}
	
	public static interface HandlerInitialSlotBuilder {
		public FinalBuilder addSlot(SlotType slotType);
	}
	
	
	public static interface FinalBuilder extends HandlerInitialSlotBuilder{
		public ItemStackHandlerWrapper build();
	}
	
	public static class HandlerBuilderImpl implements HandlerBuilder, FinalBuilder {

		private ItemStackHandler delegate;
		private HandlerType type;
		private List<SlotType> slots = new ArrayList<>();
		
		public HandlerBuilderImpl(ItemStackHandler delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public FinalBuilder addSlot(SlotType slotType) {
			slots.add(slotType);
			return this;
		}

		@Override
		public ItemStackHandlerWrapper build() {
			SlotType[] slotsArray = slots.toArray(new SlotType[0]);
			return new ItemStackHandlerWrapper(delegate, type, slotsArray);
		}

		@Override
		public HandlerInitialSlotBuilder type(HandlerType type) {
			this.type = type;
			return this;
		}
	}
	

	public void setSize(int size) {
		delegate.setSize(size);
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		delegate.setStackInSlot(slot, stack);
	}

	public int getSlots() {
		return delegate.getSlots();
	}

	public ItemStack getStackInSlot(int slot) {
		return delegate.getStackInSlot(slot);
	}
	
	public boolean canExtract(int slot) {
		SlotType slotType = slotTypes.get(slot);
		if(slotType == SlotType.INPUT) {
			return type == HandlerType.PLAYER || type == HandlerType.INTERNAL;
		}
		else {
			return type == HandlerType.PLAYER || type == HandlerType.CAPABILITY;
		}
	}
	
	public boolean canInsert(int slot) {
		SlotType slotType = slotTypes.get(slot);
		if(slotType == SlotType.INPUT) {
			return type == HandlerType.PLAYER || type == HandlerType.CAPABILITY;
		}
		else {
			return type == HandlerType.INTERNAL;
		}
	}
	
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(!canInsert(slot)) {
			return stack.copy();
		}
		return delegate.insertItem(slot, stack, simulate);
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(!canExtract(slot)) {
			return ItemStack.EMPTY;
		}
		return delegate.extractItem(slot, amount, simulate);
	}

	public int getSlotLimit(int slot) {
		return delegate.getSlotLimit(slot);
	}

	public boolean isItemValid(int slot, ItemStack stack) {
		return delegate.isItemValid(slot, stack);
	}

	public CompoundTag serializeNBT() {
		return delegate.serializeNBT();
	}

	public void deserializeNBT(CompoundTag nbt) {
		delegate.deserializeNBT(nbt);
	}

}
