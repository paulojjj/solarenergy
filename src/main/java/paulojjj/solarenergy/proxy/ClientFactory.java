package paulojjj.solarenergy.proxy;

import paulojjj.solarenergy.net.ClientPlayerProvider;
import paulojjj.solarenergy.net.IPlayerProvider;

public class ClientFactory implements ISidedFactory {
	
	private static ClientFactory instance;
	
	private IPlayerProvider playerProvider = new ClientPlayerProvider();
	
	public static ClientFactory getInstance() {
		if(instance == null) {
			instance = new ClientFactory();
		}
		return instance;
	}

	@Override
	public IPlayerProvider getPlayerProvider() {
		return playerProvider;
	}
	

}
