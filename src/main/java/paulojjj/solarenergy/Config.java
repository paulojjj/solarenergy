package paulojjj.solarenergy;

import net.minecraftforge.common.config.Configuration;

public class Config {
	
	public static final String DEFAULT_CATEGORY = "general";
	
	private Configuration configuration;
	private static Config instance;
	
	private Config() {
	}
	
	public static void init(Configuration configuration) {
		getInstance().configuration = configuration;
		configuration.load();
	}
	
	public static Config getInstance() {
		if(instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
}
