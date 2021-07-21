package paulojjj.solarenergy;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.base.CaseFormat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	
	public static final String DEFAULT_CATEGORY = "general";
	public static final String SOLAR_GENERATOR_CATEGORY = "solarGenerator";
	public static final String BATTERY_CATEGORY = "battery";
	
	private static ForgeConfigSpec configSpec;
	private static Config instance;
	
	private ConfigValue<String> logLevel;
	private ConfigValue<Boolean> produceWhileRaining;
	private ConfigValue<Boolean> realisticGeneration;
	
	private Map<Tier, ConfigValue<Double>> solarGeneratorMultipliers = new LinkedHashMap<>();
	private Map<Tier, ConfigValue<Double>> batteryMultipliers = new LinkedHashMap<>();
	
	private Config() {
	}
	
	private static Config configure(ForgeConfigSpec.Builder builder) {
		Config config = getInstance();
		
		builder.push(DEFAULT_CATEGORY);
		config.logLevel = builder.define("logLevel", "INFO");
		builder.pop();
		
		builder.push(SOLAR_GENERATOR_CATEGORY);
		config.produceWhileRaining = builder.comment("Keeps generating energy when it's raining")
				.define("generatesWhileRaining", true);
		config.realisticGeneration = builder.comment("Enables realistic energy generation (which varies with available sunlight)")
				.define("realisticGeneration", false);
		
		for(Tier tier : Tier.values()) {
			if(!tier.isDense()) {
				config.solarGeneratorMultipliers.put(tier, builder.define(getSolarGeneratorMultiplierPropertyKey(tier), 1.0));
			}
		}
		
		builder.pop();
		
		builder.push(BATTERY_CATEGORY);
		for(Tier tier : Tier.values()) {
			config.batteryMultipliers.put(tier, builder.define(getBatteryPropertyKey(tier), 1.0));
		}
		builder.pop();
		
		return config;
	}
	
	public static void init() {
		//ModLoadingContext.get().registerConfig(Type.COMMON, spec);
		Pair<Config, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Config::configure);
		configSpec = pair.getRight();
		ModLoadingContext.get().registerConfig(Type.COMMON, configSpec);
	}
	
	public static Config getInstance() {
		if(instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	private static String getSolarGeneratorMultiplierPropertyKey(Tier tier) {
		String key = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tier.name());
		key += "SolarGeneratorMultiplier";
		return key;
	}
	
	private static String getBatteryPropertyKey(Tier tier) {
		String key = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tier.name());
		key += "BatteryMultiplier";
		return key;
	}
	
	public Level getLogLevel() {
		return Level.valueOf(logLevel.get());
	}

	public void setLogLevel(Level level) {
		logLevel.set(level.toString());
		configSpec.save();
	}
	
	public boolean getProduceWhileRaining() {
		return produceWhileRaining.get();
	}
	
	public boolean getRealisticGeneration() {
		return realisticGeneration.get();
	}
	
	public double getSolarGeneratorMultiplier(Tier tier) {
		double multiplier = solarGeneratorMultipliers.get(tier).get();
		if(multiplier < 0) {
			multiplier = 0;
		}
		return multiplier;
	}

	public double getBatteryMultiplier(Tier tier) {
		double multiplier = batteryMultipliers.get(tier).get();
		if(multiplier < 0) {
			multiplier = 0;
		}
		return multiplier;
	}
}
