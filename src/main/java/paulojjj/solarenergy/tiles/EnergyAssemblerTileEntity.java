package paulojjj.solarenergy.tiles;

import java.util.Optional;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import paulojjj.solarenergy.ItemStackHandlerWrapper;
import paulojjj.solarenergy.ItemStackHandlerWrapper.HandlerType;
import paulojjj.solarenergy.ItemStackHandlerWrapper.SlotType;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.TickHandler;
import paulojjj.solarenergy.net.IMessageListener;
import paulojjj.solarenergy.net.PacketManager;
import paulojjj.solarenergy.recipes.EnergyAssemblerRecipe;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity.EnergyAssemblerTileUpdateMessage;
import paulojjj.solarenergy.registry.TileEntities;

public class EnergyAssemblerTileEntity extends EnergyStorageTileEntity implements ITickableTileEntity, IMessageListener<EnergyAssemblerTileUpdateMessage> {
	
	public static final int UPDATE_TICKS = 10;
	
	private Item assemblingItem;
	private Item resultItem;
	private ItemStackHandler itemHandler;
	
	private IItemHandler playerHandler;
	private IItemHandler internalHandler;
	private IItemHandler capabilityHandler;
	
	private long lastUpdate = 0;

	public enum Slot {
		INPUT, OUTPUT
	}
	
	public Item getAssemblingItem() {
		return assemblingItem;
	}
	
	public Item getResultItem() {
		return resultItem;
	}
	
	public IItemHandler getPlayerHandler() {
		return playerHandler;
	}
	
	public static class EnergyAssemblerTileUpdateMessage extends EnergyStorageContainerUpdateMessage {
		public Item resultItem;
		
		public EnergyAssemblerTileUpdateMessage() {
			super();
		}
		
		public EnergyAssemblerTileUpdateMessage(EnergyAssemblerTileEntity te) {
			super(te);
			resultItem = te.resultItem;
		}
		
	}
	
	protected void initItemHandlers(SlotType... slotTypes) {
		itemHandler = new ItemStackHandler(slotTypes.length);
		playerHandler = new ItemStackHandlerWrapper(itemHandler, HandlerType.PLAYER, slotTypes);
		internalHandler = new ItemStackHandlerWrapper(itemHandler, HandlerType.INTERNAL, slotTypes);
		capabilityHandler = new ItemStackHandlerWrapper(itemHandler, HandlerType.CAPABILITY, slotTypes);
	}

	public EnergyAssemblerTileEntity() {
		this(TileEntities.ENERGY_ASSEMBLER.getType());
	}
	
	public EnergyAssemblerTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		initItemHandlers(SlotType.INPUT, SlotType.OUTPUT);
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> (T) capabilityHandler);
		}
		return super.getCapability(capability, facing);
	}
	
	public static class EnergyAssemblerItemHandler extends ItemStackHandler {
		
		private TileEntity tileEntity;
		
		public EnergyAssemblerItemHandler(TileEntity tileEntity, int slots) {
			super(slots);
			this.tileEntity = tileEntity;
		}
		
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return super.insertItem(slot, stack, simulate);
		}
		
		@Override
		protected void onContentsChanged(int slot) {
			tileEntity.setChanged();
		}
	}
	

	
	@Override
	public void load(CompoundNBT compound) {
		super.load(compound);
		ItemStack stack = ItemStack.of((CompoundNBT)compound.get(NBT.ASSEMBLING_ITEM));
		assemblingItem = stack == ItemStack.EMPTY ? null : stack.getItem();
		itemHandler.deserializeNBT((CompoundNBT)compound.get(NBT.INVENTORY));
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound = super.save(compound);
		CompoundNBT itemCompound = new CompoundNBT();
		ItemStack stack = assemblingItem == null ? ItemStack.EMPTY : new ItemStack(assemblingItem, 1);
		stack.save(itemCompound);
		compound.put(NBT.ASSEMBLING_ITEM, itemCompound);
		compound.put(NBT.INVENTORY, itemHandler.serializeNBT());
		return compound;
	}
	
	protected Optional<EnergyAssemblerRecipe> getRecipe(Item input) {
		return RecipeHandler.getEnergyAssemblerRecipe(input);
	}
	
	protected boolean canAssemble(Item item) {
		return getRecipe(item).isPresent();
	}
	
	protected double getEnergyToAssemble(Item item) {
		return getRecipe(item).get().getEnergyNeeded();
	}
	
	protected ItemStack getOutput(Item input) {
		return getRecipe(input).get().getOutput().copy();	
	}
	
	protected void updateClientEntity() {
		PacketManager.sendToAllTracking(this, new EnergyAssemblerTileUpdateMessage(this));
	}
	
	public void beginAssemble(Item item) {
		assemblingItem = item;
		energy = 0;
		maxEnergy = getEnergyToAssemble(item);
		resultItem = getOutput(item).getItem();
		updateClientEntity();
	}

	public void endAssemble() {
		assemblingItem = null;
		resultItem = null;
		energy = 0;
		maxEnergy = 0;
		updateClientEntity();
	}
	
	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide && maxEnergy > 0 && energy < maxEnergy) {
			//Simulate energy update on client between updates
			energy += Math.min(input, maxEnergy - energy);
		}
		if(level.isClientSide) {
			return;
		}
		if(assemblingItem != null && assemblingItem != ItemStack.EMPTY.getItem()) {
			//Send update packets every UPDATE_TICKS to decrease network usage
			long tick = TickHandler.getTick();
			if(tick > lastUpdate + UPDATE_TICKS) {
				updateClientEntity();
				lastUpdate = TickHandler.getTick();
			}
			
			//Item ready
			if(energy >= maxEnergy) {
				ItemStack stack = getOutput(assemblingItem);
				if(internalHandler.insertItem(Slot.OUTPUT.ordinal(), stack, false) == ItemStack.EMPTY) {
					endAssemble();
				}
			}
		}
		if(assemblingItem != null && assemblingItem != ItemStack.EMPTY.getItem()) {
			return;
		}
		
		ItemStack input = internalHandler.getStackInSlot(Slot.INPUT.ordinal());
		if(!input.isEmpty()) {
			Item item = input.getItem();
			if(canAssemble(item)) {
				ItemStack extracted = new ItemStack(item,1);
				extracted = internalHandler.extractItem(Slot.INPUT.ordinal(), 1, false);
				if(extracted != ItemStack.EMPTY) {
					beginAssemble(extracted.getItem());
				}
			}
		}
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		if(assemblingItem != null && assemblingItem != ItemStack.EMPTY.getItem()) {
			resultItem = getOutput(assemblingItem).getItem();
		}
	}

	@Override
	public void onMessage(EnergyAssemblerTileUpdateMessage message) {
		energy = message.energyStored;
		maxEnergy = message.maxEnergyStored;
		resultItem = message.resultItem;
		input = message.input;
		output = message.output;
	}


}
