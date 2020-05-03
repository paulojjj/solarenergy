package paulojjj.solarenergy.proxy;

import paulojjj.solarenergy.net.IPlayerProvider;
import paulojjj.solarenergy.net.ServerPlayerProvider;

public class ServerFactory implements ISidedFactory {
	
	private static ClientFactory instance;
	
	private IPlayerProvider playerProvider = new ServerPlayerProvider();
	
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
