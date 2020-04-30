package paulojjj.solarenergy.networks;

import java.util.Map;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class BatteryNetwork extends BaseNetwork<BatteryTileEntity> {
	
	public BatteryNetwork() {
	}

	@Override
	protected Class<BatteryTileEntity> getTileClass() {
		return BatteryTileEntity.class;
	}

	@Override
	protected Map<EnumFacing, IEnergyStorage> getConsumers() {
		return getStorages((t, f, s) -> f == t.getOuputFacing() && s.canReceive());
	}
}
