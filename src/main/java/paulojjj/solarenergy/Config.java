package paulojjj.solarenergy;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	
	public static final String DEFAULT_CATEGORY = "general";
	
	private static ForgeConfigSpec configSpec;
	private static Config instance;
	
	private ConfigValue<String> logLevel;
	
	private Config() {
	}
	
	public static void init() {
		//ModLoadingContext.get().registerConfig(Type.COMMON, spec);
		Pair<Config, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Config::configure);
		configSpec = pair.getRight();
		ModLoadingContext.get().registerConfig(Type.COMMON, configSpec);
	}
	
	private static Config configure(ForgeConfigSpec.Builder builder) {
		Config config = getInstance();
		
		builder.push(DEFAULT_CATEGORY);
		config.logLevel = builder.define("log_level", "INFO");
		builder.pop();
		
		return config;
	}
	
	public static Config getInstance() {
		if(instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	public Level getLogLevel() {
		return Level.valueOf(logLevel.get());
	}

	public void setLogLevel(Level level) {
		logLevel.set(level.toString());
		configSpec.save();
	}
	
}
