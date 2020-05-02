package paulojjj.solarenergy.registry;

import net.minecraft.item.Item;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.ModCreativeTab;

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
	LEAD_INGOT("ingotLead", ItemType.FORGE_ORE_DICT);

	public enum ItemType {
		NORMAL, FORGE_ORE_DICT;
	}

	private Item item;
	private ItemType type;
	private String registryName;

	Items(String name, ItemType type) {
		this(name, name, type);
	}
	
	Items(String name) {
		this(name, name);
	}
	
	Items(String name, String registryName) {
		this(name, registryName, ItemType.NORMAL);
	}

	Items(String name, String registryName, ItemType type) {
		Item item = new Item();
		item.setUnlocalizedName(name);
		item.setRegistryName(Main.MODID, registryName);
		item.setCreativeTab(ModCreativeTab.getInstance());
		this.item = item;
		this.type = type;
		this.registryName = registryName;
	}

	public Item getItem() {
		return item;
	}

	public ItemType getType() {
		return type;
	}

	public String getRegistryName() {
		return registryName;
	}

}
