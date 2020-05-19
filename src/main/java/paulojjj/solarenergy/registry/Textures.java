package paulojjj.solarenergy.registry;

import net.minecraft.util.ResourceLocation;
import paulojjj.solarenergy.Main;

public enum Textures {
	
	SOLAR_GENERATOR_SIDE("block/solar_generator_side"),
	ENERGY_CABLE_CENTER("block/energy_cable_center"),
	ENERGY_CABLE_HORIZONTAL("block/energy_cable_horizontal"),
	ENERGY_CABLE_VERTICAL("block/energy_cable_vertical");
	
	private ResourceLocation resourceLocation;
	
	private Textures(String location) {
		resourceLocation = new ResourceLocation(Main.MODID, location);
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}
}
