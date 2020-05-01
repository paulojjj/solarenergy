package paulojjj.solarenergy;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

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
	
	private Property getLogLevelProperty() {
		return configuration.get(DEFAULT_CATEGORY, "log_level", "INFO");
	}

	public Level getLogLevel() {
		return Level.valueOf(getLogLevelProperty().getString());
	}

	public void setLogLevel(Level level) {
		getLogLevelProperty().set(level.toString());
		configuration.save();
	}
	
}
