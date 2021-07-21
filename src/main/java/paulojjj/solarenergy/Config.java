package paulojjj.solarenergy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.base.CaseFormat;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	
	public static final String DEFAULT_CATEGORY = "general";
	public static final String SOLAR_GENERATOR_CATEGORY = "solarGenerator";
	public static final String BATTERY_CATEGORY = "battery";
	
	private Configuration configuration;
	private static Config instance;
	
	private Config() {
	}
	
	public static void init(Configuration configuration) {
		getInstance().configuration = configuration;
		configuration.load();
		
		//Call properties getters to initialize default values
		Class<Config> clazz = Config.class;
		for(Method method : clazz.getDeclaredMethods()) {
			String name = method.getName();
			if(method.getParameterCount() == 0 && name.startsWith("get") && name.endsWith("Property")) {
				if(!method.isAccessible()) {
					method.setAccessible(true);
				}
				try {
					method.invoke(getInstance());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		List<String> solarGeneratorTierOder = new ArrayList<>();
		List<String> batteryTierOrder = new ArrayList<>();
		
		for(Tier tier : Tier.values()) {
			if(!tier.isDense()) {
				solarGeneratorTierOder.add(getInstance().getSolarGeneratorMultiplierPropertyKey(tier));
				getInstance().getSolarGeneratorMultiplierProperty(tier);
			}
			getInstance().getBatteryMultiplierProperty(tier);
			batteryTierOrder.add(getInstance().getBatteryPropertyKey(tier));
		}
		
		configuration.setCategoryPropertyOrder(SOLAR_GENERATOR_CATEGORY, solarGeneratorTierOder);
		configuration.setCategoryPropertyOrder(BATTERY_CATEGORY, batteryTierOrder);
		
		if(configuration.hasChanged()) {
			configuration.save();
		}
		
	}
	
	public static Config getInstance() {
		if(instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	private Property getLogLevelProperty() {
		return configuration.get(DEFAULT_CATEGORY, "logLevel", "INFO");
	}

	private Property getProduceWhileRainingProperty() {
		return configuration.get(SOLAR_GENERATOR_CATEGORY, "generatesWhileRaining", true, I18n.format("config.comment.generatesWhileRaining"));
	}
	
	private Property getRealisticGenerationProperty() {
		return configuration.get(SOLAR_GENERATOR_CATEGORY, "realisticGeneration", false, I18n.format("config.comment.realisticGeneration"));
	}
	
	private String getSolarGeneratorMultiplierPropertyKey(Tier tier) {
		String key = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tier.name());
		key += "SolarGeneratorMultiplier";
		return key;
	}
	
	private Property getSolarGeneratorMultiplierProperty(Tier tier) {
		String key = getSolarGeneratorMultiplierPropertyKey(tier);
		return configuration.get(SOLAR_GENERATOR_CATEGORY, key, 1.0);		
	}
	
	private String getBatteryPropertyKey(Tier tier) {
		String key = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tier.name());
		key += "BatteryMultiplier";
		return key;
	}
	
	private Property getBatteryMultiplierProperty(Tier tier) {
		String key = getBatteryPropertyKey(tier);
		return configuration.get(BATTERY_CATEGORY, key, 1.0);		
	}
	
	public Level getLogLevel() {
		return Level.valueOf(getLogLevelProperty().getString());
	}

	public void setLogLevel(Level level) {
		getLogLevelProperty().set(level.toString());
		configuration.save();
	}
	
	public boolean getProduceWhileRaining() {
		return getProduceWhileRainingProperty().getBoolean();
	}
	
	public boolean getRealisticGeneration() {
		return getRealisticGenerationProperty().getBoolean();
	}
	
	public double getSolarGeneratorMultiplier(Tier tier) {
		double multiplier = getSolarGeneratorMultiplierProperty(tier).getDouble();
		if(multiplier < 0) {
			multiplier = 0;
		}
		return multiplier;
	}

	public double getBatteryMultiplier(Tier tier) {
		double multiplier = getBatteryMultiplierProperty(tier).getDouble();
		if(multiplier < 0) {
			multiplier = 0;
		}
		return multiplier;
	}
}
