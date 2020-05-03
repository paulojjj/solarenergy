package paulojjj.solarenergy.proxy;

public interface Proxy {
	
	public void registerGuiHandler();
	
	public void registerHandlers();
	
	public void registerAssets();
	
	public void registerCommands();
	
	ISidedFactory getFactory();
	
}
