package paulojjj.solarenergy.networks;

import java.util.Set;

import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryNetwork extends BaseNetwork<BatteryTileEntity> {
	
	public BatteryNetwork() {
	}

	@Override
	public Class<BatteryTileEntity> getTileClass() {
		return BatteryTileEntity.class;
	}

	@Override
	protected Set<IEnergyStorage> getConsumers() {
		return getStorages((t, f, s) -> f == t.getOuputFacing() && s.canReceive());
	}
}
