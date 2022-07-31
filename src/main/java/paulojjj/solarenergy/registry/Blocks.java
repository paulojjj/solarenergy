package paulojjj.solarenergy.registry;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.blocks.Battery;
import paulojjj.solarenergy.blocks.EnergyAssembler;
import paulojjj.solarenergy.blocks.EnergyCable;
import paulojjj.solarenergy.blocks.SolarGenerator;
import paulojjj.solarenergy.proxy.CommonProxy;

public enum Blocks {

	BASIC_SOLAR_GENERATOR("basic_solar_generator", () -> new SolarGenerator(Tier.BASIC)),
	REGULAR_SOLAR_GENERATOR("regular_solar_generator", () -> new SolarGenerator(Tier.REGULAR)),
	INTERMEDIATE_SOLAR_GENERATOR("intermediate_solar_generator", () -> new SolarGenerator(Tier.INTERMEDIATE)),
	ADVANCED_SOLAR_GENERATOR("advanced_solar_generator", () -> new SolarGenerator(Tier.ADVANCED)),
	ELITE_SOLAR_GENERATOR("elite_solar_generator", () -> new SolarGenerator(Tier.ELITE)),
	ULTIMATE_SOLAR_GENERATOR("ultimate_solar_generator", () -> new SolarGenerator(Tier.ULTIMATE)),
	BASIC_BATTERY("basic_battery", () -> new Battery(Tier.BASIC)),
	REGULAR_BATTERY("regular_battery", () -> new Battery(Tier.REGULAR)),
	INTERMEDIATE_BATTERY("intermediate_battery", () -> new Battery(Tier.INTERMEDIATE)),
	ADVANCED_BATTERY("advanced_battery", () -> new Battery(Tier.ADVANCED)),
	ELITE_BATTERY("elite_battery", () -> new Battery(Tier.ELITE)),
	ULTIMATE_BATTERY("ultimate_battery", () -> new Battery(Tier.ULTIMATE)),
	BASIC_DENSE_BATTERY("basic_dense_battery", () -> new Battery(Tier.BASIC_DENSE)),
	REGULAR_DENSE_BATTERY("regular_dense_battery", () -> new Battery(Tier.REGULAR_DENSE)),
	INTERMEDIATE_DENSE_BATTERY("intermediate_dense_battery", () -> new Battery(Tier.INTERMEDIATE_DENSE)),
	ADVANCED_DENSE_BATTERY("advanced_dense_battery", () -> new Battery(Tier.ADVANCED_DENSE)),
	ELITE_DENSE_BATTERY("elite_dense_battery", () -> new Battery(Tier.ELITE_DENSE)),
	ULTIMATE_DENSE_BATTERY("ultimate_dense_battery", () -> new Battery(Tier.ULTIMATE_DENSE)),
	ENERGY_ASSEMBLER("energy_assembler", () -> new EnergyAssembler()),
	ENERGY_CABLE("energy_cable", () -> new EnergyCable());
	
	private RegistryObject<Block> block;
	//private RegistryObject<BlockItem> itemBlock;
	
	Blocks(String registryName, Supplier<? extends Block> block) {
		this.block = CommonProxy.BLOCKS.register(registryName, () -> block.get());

		//this.block = CommonProxy.BLOCKS.register(registryName, () -> {
		//	System.out.println("Registrando bloco " + registryName);
		//	return this.itemBlock.get().getBlock();
		//});
	}
	
	//public BlockItem getItemBlock() {
	//	return itemBlock.get();
	//}
	
	public Block getBlock() {
		return block.get();
	}
	
	public static Blocks getSolarGenerator(Tier tier) {
		switch(tier) {
			case BASIC:
				return Blocks.BASIC_SOLAR_GENERATOR;
			case REGULAR:
				return Blocks.REGULAR_SOLAR_GENERATOR;
			case INTERMEDIATE:
				return Blocks.INTERMEDIATE_SOLAR_GENERATOR;
			case ADVANCED:
				return Blocks.ADVANCED_SOLAR_GENERATOR;
			case ELITE:
				return Blocks.ELITE_SOLAR_GENERATOR;
			case ULTIMATE:
				return Blocks.ULTIMATE_SOLAR_GENERATOR;
			default:
				throw new RuntimeException("Invalid solar generator tier");
		}
	}
	
	public static Blocks getBattery(Tier tier) {
		switch(tier) {
			case BASIC:
				return Blocks.BASIC_BATTERY;
			case REGULAR:
				return Blocks.REGULAR_BATTERY;
			case INTERMEDIATE:
				return Blocks.INTERMEDIATE_BATTERY;
			case ADVANCED:
				return Blocks.ADVANCED_BATTERY;
			case ELITE:
				return Blocks.ELITE_BATTERY;
			case ULTIMATE:
				return Blocks.ULTIMATE_BATTERY;
			case BASIC_DENSE:
				return Blocks.BASIC_DENSE_BATTERY;
			case REGULAR_DENSE:
				return Blocks.REGULAR_DENSE_BATTERY;
			case INTERMEDIATE_DENSE:
				return Blocks.INTERMEDIATE_DENSE_BATTERY;
			case ADVANCED_DENSE:
				return Blocks.ADVANCED_DENSE_BATTERY;
			case ELITE_DENSE:
				return Blocks.ELITE_DENSE_BATTERY;
			case ULTIMATE_DENSE:
				return Blocks.ULTIMATE_DENSE_BATTERY;
			default:
				throw new RuntimeException("Invalid battery tier");				
		}
	}
	
	
}
