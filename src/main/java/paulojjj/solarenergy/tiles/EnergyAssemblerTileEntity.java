package paulojjj.solarenergy.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import paulojjj.solarenergy.blocks.EnergyAssembler;
import paulojjj.solarenergy.recipes.RecipeHandler;
import paulojjj.solarenergy.registry.Blocks;

public class EnergyAssemblerTileEntity extends EnergyStorageTileEntity implements net.minecraft.util.ITickable {
	
	private int energyNeeded = 10000;
	
	private Item assemblingItem;

	public enum Slot {
		INPUT, OUTPUT
	}
	
	private EnergyAssemblerItemHandler itemHandler = new EnergyAssemblerItemHandler(this, 2);

	public EnergyAssemblerItemHandler getItemHandler() {
		return itemHandler;
	}

	public EnergyAssemblerTileEntity() {
		super();
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) itemHandler;
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
			tileEntity.markDirty();
		}
	}
	

	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		ItemStack stack = new ItemStack(compound.getCompoundTag("AssemblingItem"));
		assemblingItem = stack == ItemStack.EMPTY ? null : stack.getItem();
		itemHandler.deserializeNBT(compound.getCompoundTag("Inventory"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		NBTTagCompound itemCompound = new NBTTagCompound();
		ItemStack stack = assemblingItem == null ? ItemStack.EMPTY : new ItemStack(assemblingItem, 1);
		stack.writeToNBT(itemCompound);
		compound.setTag("AssemblingItem", itemCompound);
		compound.setTag("Inventory", itemHandler.serializeNBT());
		return compound;
	}
	
	protected boolean canAssemble(Item item) {
		return RecipeHandler.getEnergyAssemblerRecipe(item).isPresent();
	}
	
	protected double getEnergyToAssemble(Item item) {
		return RecipeHandler.getEnergyAssemblerRecipe(item).get().getEnergyNeeded();
	}
	
	protected ItemStack getOutput(Item input) {
		return RecipeHandler.getEnergyAssemblerRecipe(input).get().getOutput().copy();	
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return false;
	}
	
	public void setBlockActive(boolean active) {
		world.setBlockState(pos, Blocks.ENERGY_ASSEMBLER.getItemBlock().getBlock().getDefaultState().withProperty(EnergyAssembler.ACTIVE, active));
	}
	
	public void beginAssemble(Item item) {
		assemblingItem = item;
		energy = 0;
		maxEnergy = getEnergyToAssemble(item);
		setBlockActive(true);
	}

	public void endAssemble() {
		assemblingItem = null;
		energy = 0;
		maxEnergy = 0;
		setBlockActive(false);
	}
	
	@Override
	public void update() {
		super.update();
		if(world.isRemote) {
			return;
		}
		if(assemblingItem != null && assemblingItem != ItemStack.EMPTY.getItem()) {
			//Item ready
			if(energy >= maxEnergy) {
				ItemStack stack = getOutput(assemblingItem);
				if(itemHandler.insertItem(Slot.OUTPUT.ordinal(), stack, false) == ItemStack.EMPTY) {
					endAssemble();
				}
			}
		}
		if(assemblingItem != null && assemblingItem != ItemStack.EMPTY.getItem()) {
			return;
		}
		
		ItemStack input = itemHandler.getStackInSlot(Slot.INPUT.ordinal());
		if(!input.isEmpty()) {
			Item item = input.getItem();
			if(canAssemble(item)) {
				ItemStack extracted = new ItemStack(item,1);
				extracted = itemHandler.extractItem(Slot.INPUT.ordinal(), 1, false);
				if(extracted != ItemStack.EMPTY) {
					beginAssemble(extracted.getItem());
				}
			}
		}
	}


}
