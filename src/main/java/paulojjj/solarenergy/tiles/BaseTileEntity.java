package paulojjj.solarenergy.tiles;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import paulojjj.solarenergy.net.PacketManager;

public abstract class BaseTileEntity extends TileEntity implements ITickableTileEntity {
	
	public BaseTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	private Set<PlayerEntity> playersUsing = new HashSet<>();

	public void onContainerOpened(PlayerEntity player) {
		if(!level.isClientSide) {
			playersUsing.add(player);
		}
	}

	public void onContainerClosed(PlayerEntity player) {
		if(!level.isClientSide) {
			playersUsing.remove(player);
		}
	}
	
	@Override
	public void tick() {
		for(PlayerEntity player : playersUsing) {
			PacketManager.sendContainerUpdateMessage((ServerPlayerEntity)player, getContainerUpdateMessage());
		}
	}
	
	protected abstract Object getContainerUpdateMessage();

}
