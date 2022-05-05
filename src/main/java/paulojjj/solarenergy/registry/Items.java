package paulojjj.solarenergy.registry;

import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import paulojjj.solarenergy.ModCreativeTab;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.blocks.BatteryItemBlock;
import paulojjj.solarenergy.blocks.SolarGeneratorItemBlock;
import paulojjj.solarenergy.proxy.CommonProxy;

public enum Items {
	BASIC_ENERGY_CORE("basic_energy_core"),
	REGULAR_ENERGY_CORE("regular_energy_core"),
	INTERMEDIATE_ENERGY_CORE("intermediate_energy_core"),
	ADVANCED_ENERGY_CORE("advanced_energy_core"),
	ELITE_ENERGY_CORE("elite_energy_core"),
	ULTIMATE_ENERGY_CORE("ultimate_energy_core"),
	BASIC_DENSE_ENERGY_CORE("basic_dense_energy_core"),
	REGULAR_DENSE_ENERGY_CORE("regular_dense_energy_core"),
	INTERMEDIATE_DENSE_ENERGY_CORE("intermediate_dense_energy_core"),
	ADVANCED_DENSE_ENERGY_CORE("advanced_dense_energy_core"),
	ELITE_DENSE_ENERGY_CORE("elite_dense_energy_core"),
	ULTIMATE_DENSE_ENERGY_CORE("ultimate_dense_energy_core"),
	BASIC_SOLAR_CELL("basic_solar_cell"),
	REGULAR_SOLAR_CELL("regular_solar_cell"),
	INTERMEDIATE_SOLAR_CELL("intermediate_solar_cell"),
	ADVANCED_SOLAR_CELL("advanced_solar_cell"),
	ELITE_SOLAR_CELL("elite_solar_cell"),
	ULTIMATE_SOLAR_CELL("ultimate_solar_cell"),
	BASIC_BATTERY_CELL("basic_battery_cell"),
	REGULAR_BATTERY_CELL("regular_battery_cell"),
	INTERMEDIATE_BATTERY_CELL("intermediate_battery_cell"),
	ADVANCED_BATTERY_CELL("advanced_battery_cell"),
	ELITE_BATTERY_CELL("elite_battery_cell"),
	ULTIMATE_BATTERY_CELL("ultimate_battery_cell"),
	BASIC_DENSE_BATTERY_CELL("basic_dense_battery_cell"),
	REGULAR_DENSE_BATTERY_CELL("regular_dense_battery_cell"),
	INTERMEDIATE_DENSE_BATTERY_CELL("intermediate_dense_battery_cell"),
	ADVANCED_DENSE_BATTERY_CELL("advanced_dense_battery_cell"),
	ELITE_DENSE_BATTERY_CELL("elite_dense_battery_cell"),
	ULTIMATE_DENSE_BATTERY_CELL("ultimate_dense_battery_cell"),	
	LEAD_INGOT("ingots/lead", ItemType.FORGE_ORE_DICT),
	
	BASIC_SOLAR_GENERATOR("basic_solar_generator", () -> new SolarGeneratorItemBlock(Tier.BASIC)),
	REGULAR_SOLAR_GENERATOR("regular_solar_generator", () -> new SolarGeneratorItemBlock(Tier.REGULAR)),
	INTERMEDIATE_SOLAR_GENERATOR("intermediate_solar_generator", () -> new SolarGeneratorItemBlock(Tier.INTERMEDIATE)),
	ADVANCED_SOLAR_GENERATOR("advanced_solar_generator", () -> new SolarGeneratorItemBlock(Tier.ADVANCED)),
	ELITE_SOLAR_GENERATOR("elite_solar_generator", () -> new SolarGeneratorItemBlock(Tier.ELITE)),
	ULTIMATE_SOLAR_GENERATOR("ultimate_solar_generator", () -> new SolarGeneratorItemBlock(Tier.ULTIMATE)),
	BASIC_BATTERY("basic_battery", () -> new BatteryItemBlock(Tier.BASIC)),
	REGULAR_BATTERY("regular_battery", () -> new BatteryItemBlock(Tier.REGULAR)),
	INTERMEDIATE_BATTERY("intermediate_battery", () -> new BatteryItemBlock(Tier.INTERMEDIATE)),
	ADVANCED_BATTERY("advanced_battery", () -> new BatteryItemBlock(Tier.ADVANCED)),
	ELITE_BATTERY("elite_battery", () -> new BatteryItemBlock(Tier.ELITE)),
	ULTIMATE_BATTERY("ultimate_battery", () -> new BatteryItemBlock(Tier.ULTIMATE)),
	BASIC_DENSE_BATTERY("basic_dense_battery", () -> new BatteryItemBlock(Tier.BASIC_DENSE)),
	REGULAR_DENSE_BATTERY("regular_dense_battery", () -> new BatteryItemBlock(Tier.REGULAR_DENSE)),
	INTERMEDIATE_DENSE_BATTERY("intermediate_dense_battery", () -> new BatteryItemBlock(Tier.INTERMEDIATE_DENSE)),
	ADVANCED_DENSE_BATTERY("advanced_dense_battery", () -> new BatteryItemBlock(Tier.ADVANCED_DENSE)),
	ELITE_DENSE_BATTERY("elite_dense_battery", () -> new BatteryItemBlock(Tier.ELITE_DENSE)),
	ULTIMATE_DENSE_BATTERY("ultimate_dense_battery", () -> new BatteryItemBlock(Tier.ULTIMATE_DENSE)),
	ENERGY_ASSEMBLER("energy_assembler", Blocks.ENERGY_ASSEMBLER),
	ENERGY_CABLE("energy_cable", Blocks.ENERGY_CABLE);
	

	public enum ItemType {
		NORMAL, FORGE_ORE_DICT;
	}

	private RegistryObject<Item> item;
	private ItemType type;
	private String registryName;

	Items(String name, ItemType type) {
		this(name, name, type, null);
	}
	
	Items(String name) {
		this(name, name, null);
	}
	
	Items(String name, Supplier<? extends Item> item) {
		this(name, name, item);
	}
	
	Items(String name, Blocks block) {
		this(name, name, () -> new BlockItem(block.getBlock(), new Item.Properties().tab(ModCreativeTab.getInstance())));
	}
	
	Items(String name, String registryName, Supplier<? extends Item> item) {
		this(name, registryName, ItemType.NORMAL, item);
	}

	Items(String name, String registryName, ItemType type, Supplier<? extends Item> item) {
		this.item = CommonProxy.ITEMS.register(registryName, () -> {
			Item newItem = item == null ? new Item(new Item.Properties().tab(ModCreativeTab.getInstance())) : item.get();
			this.type = type;
			this.registryName = registryName;
			
			return newItem;
		});
	}

	public Item getItem() {
		return item.get();
	}

	public ItemType getType() {
		return type;
	}

	public String getRegistryName() {
		return registryName;
	}

}
