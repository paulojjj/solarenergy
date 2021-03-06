package paulojjj.solarenergy.networks;

import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.IUltraEnergyStorage;

public interface INetwork<T extends TileEntity & INetworkMember> extends IUltraEnergyStorage {
	
	static <N extends INetwork<TE>, TE extends TileEntity & INetworkMember> N newInstance(Class<N> networkClass, TE initialTile) {
		try {
			N network = networkClass.newInstance();
			network.init(initialTile);
			return network;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Error creating network", e);
		}
	}
	
	INetwork<T> init(T initialTile);
	
	Set<T> getTiles();
	Map<T, Map<EnumFacing, IEnergyStorage>> getStorages();
	Class<T> getTileClass();
	
	boolean isValid();
	
	void destroy();
	
	void onBlockRemoved(T tile);
	
	void onNeighborChanged(T source, BlockPos neighborPos);
	
	void onChunkUnload(BlockPos pos);
	
	void update();
	
	double getEnergyInput();
	double getEnergyOutput();

}
