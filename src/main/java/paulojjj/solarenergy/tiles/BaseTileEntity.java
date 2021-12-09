package paulojjj.solarenergy.tiles;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import paulojjj.solarenergy.net.PacketManager;

public abstract class BaseTileEntity extends BlockEntity {
	
	public BaseTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
	}

	private Set<Player> playersUsing = new HashSet<>();

	public void onContainerOpened(Player player) {
		if(!level.isClientSide) {
			playersUsing.add(player);
		}
	}

	public void onContainerClosed(Player player) {
		if(!level.isClientSide) {
			playersUsing.remove(player);
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity tileEntity) {
		BaseTileEntity te = (BaseTileEntity)tileEntity;
		te.tick();
	}
	
	public void tick() {
		for(Player player : playersUsing) {
			PacketManager.sendContainerUpdateMessage((ServerPlayer)player, getContainerUpdateMessage());
		}
	}
	
	protected abstract Object getContainerUpdateMessage();

}
