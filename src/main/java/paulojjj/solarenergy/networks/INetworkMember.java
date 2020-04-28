package paulojjj.solarenergy.networks;

import paulojjj.solarenergy.IUltraEnergyStorage;

public interface INetworkMember extends IUltraEnergyStorage {
	
	INetwork<?> getNetwork();
	void setNetwork(INetwork<?> network);
	
}
