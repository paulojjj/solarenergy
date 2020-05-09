package paulojjj.solarenergy.tiles;

import java.util.Collection;

public class EnergyNetworkUpdateMessage {
	private Collection<Byte> neighborStorages;

	public Collection<Byte> getNeighborStorages() {
		return neighborStorages;
	}
	
	public void setNeighborStorages(Collection<Byte> storages) {
		neighborStorages = storages;
	}	
}
