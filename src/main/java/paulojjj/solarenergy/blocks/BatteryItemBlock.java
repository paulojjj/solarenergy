package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.NBT;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryItemBlock extends ItemBlock implements ItemCraftedListener {

	public BatteryItemBlock(Tier tier) {
		super(new Battery(tier));
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		double energy = 0;
		double capacity = 0;
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			energy = nbt.getDouble(NBT.ENERGY);
			capacity = nbt.getDouble(NBT.MAX_ENERGY);
		}
		else {
			BatteryTileEntity te = (BatteryTileEntity)this.getBlock().createTileEntity(worldIn, this.getBlock().getDefaultState());
			energy = te.getUltraEnergyStored();
			capacity = te.getMaxUltraEnergyStored();
		}
		tooltip.add(String.format("%s: %s", I18n.format(Main.MODID + ".stored_energy"), EnergyFormatter.format(energy)));
		tooltip.add(String.format("%s: %s", I18n.format(Main.MODID + ".capacity"), EnergyFormatter.format(capacity)));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTagCompound() != null;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			double energy = nbt.getDouble(NBT.ENERGY);
			double capacity = nbt.getDouble(NBT.MAX_ENERGY);
			return 1 - (energy/capacity);
		}
		return super.getDurabilityForDisplay(stack);
	}

	@Override
	public void onItemCrafted(ItemCraftedEvent event) {
		//TODO: figure out a way to send network update messages to clients (couldn't find a TileEntity associated with source Items)
/*		if(event.player.world.isRemote) {
			return;
		}
/*		//Update client
		SPacketUpdateTileEntity pkt = new SPacketUpdateTileEntity(te.getPos(), 0, nbt);
		//pkt.
		if(event.player.world.isRemote) {
			PacketManager.sendToAllTracking(te, pkt);
		}*/
		
		IInventory craftMatrix = event.craftMatrix;
		Double energy = 0.0;
		for(int i=0; i<craftMatrix.getSizeInventory();i++) {
			ItemStack stack = craftMatrix.getStackInSlot(i);
			if(stack != null) {
				NBTTagCompound nbt = stack.getTagCompound();
				if(nbt != null) {
					if(nbt.hasKey(NBT.ENERGY)) {
						energy += nbt.getDouble(NBT.ENERGY);
					}
				}
			}
		}
		if(energy > 0.0) {
			NBTTagCompound nbt = event.crafting.getTagCompound();
			if(nbt == null) {
				nbt = new NBTTagCompound();					
			}

			Block block = ((BatteryItemBlock)event.crafting.getItem()).getBlock();
			BatteryTileEntity te = (BatteryTileEntity)block.createTileEntity(event.player.world, block.getDefaultState());
			Double maxEnergy = te.getMaxUltraEnergyStored();

			nbt.setDouble(NBT.ENERGY, energy);
			nbt.setDouble(NBT.MAX_ENERGY, maxEnergy);
			event.crafting.setTagCompound(nbt);
		}
	}	

}
