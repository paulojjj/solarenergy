package paulojjj.solarenergy.containers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import paulojjj.solarenergy.tiles.BaseTileEntity;

public abstract class BaseContainer<T extends AbstractContainerMenu> extends AbstractContainerMenu {
	
	private Inventory playerInventory;
	private BaseTileEntity tileEntity;
	
	public BaseContainer(MenuType<?> type, int id, Inventory inventory, FriendlyByteBuf data) {
		super(type, id);
		this.playerInventory = inventory;
		if(data != null) {
			BlockPos pos = data.readBlockPos();
			setPos(pos);
		}
	}
	
	public BaseContainer(MenuType<?> type, int id, Inventory inventory) {
		this(type, id, inventory, null);
	}
	
	
	public Inventory getPlayerInventory() {
		return playerInventory;
	}
	
	public BaseTileEntity getTileEntity() {
		return tileEntity;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if(tileEntity != null) {
			tileEntity.onContainerClosed(playerIn);
		}
	}
	
	protected void setPos(BlockPos pos) {
		Level world = playerInventory.player.level;
		tileEntity = (BaseTileEntity)world.getBlockEntity(pos);
		tileEntity.onContainerOpened(playerInventory.player);
	}

}
