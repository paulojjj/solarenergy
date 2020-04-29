package paulojjj.solarenergy.networks;

import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryNetwork extends BaseNetwork<BatteryTileEntity> {
	
	public BatteryNetwork() {
	}

	@Override
	protected Class<BatteryTileEntity> getTileClass() {
		return BatteryTileEntity.class;
	}
	
	protected Set<IEnergyStorage> getConsumers(TileEntity tileEntity) {
		BatteryTileEntity bte = (BatteryTileEntity)tileEntity;
		return getNeighborStorages(tileEntity, (e, f) -> f == bte.getOuputFacing() && e.canReceive());
	}
	
}
