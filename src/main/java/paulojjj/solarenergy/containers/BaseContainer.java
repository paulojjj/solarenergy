package paulojjj.solarenergy.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulojjj.solarenergy.tiles.BaseTileEntity;

public abstract class BaseContainer<T extends Container> extends Container {
	
	private PlayerInventory playerInventory;
	private BaseTileEntity tileEntity;
	
	public BaseContainer(ContainerType<?> type, int id, PlayerInventory inventory, PacketBuffer data) {
		super(type, id);
		this.playerInventory = inventory;
		if(data != null) {
			BlockPos pos = data.readBlockPos();
			setPos(pos);
		}
	}
	
	public BaseContainer(ContainerType<?> type, int id, PlayerInventory inventory) {
		this(type, id, inventory, null);
	}
	
	
	public PlayerInventory getPlayerInventory() {
		return playerInventory;
	}
	
	public BaseTileEntity getTileEntity() {
		return tileEntity;
	}

	@Override
	public void removed(PlayerEntity playerIn) {
		super.removed(playerIn);
		if(tileEntity != null) {
			tileEntity.onContainerClosed(playerIn);
		}
	}
	
	protected void setPos(BlockPos pos) {
		World world = playerInventory.player.level;
		tileEntity = (BaseTileEntity)world.getBlockEntity(pos);
		tileEntity.onContainerOpened(playerInventory.player);
	}

}
