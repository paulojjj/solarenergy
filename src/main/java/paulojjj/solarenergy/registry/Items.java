package paulojjj.solarenergy.registry;

import net.minecraft.item.Item;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.ModCreativeTab;

public enum Items {
	BASIC_ENERGY_CORE("basic_energy_core", "basic_energy_core"),
	REGULAR_ENERGY_CORE("regular_energy_core", "regular_energy_core"),
	INTERMEDIATE_ENERGY_CORE("intermediate_energy_core", "intermediate_energy_core"),
	ADVANCED_ENERGY_CORE("advanced_energy_core", "advanced_energy_core"),
	ELITE_ENERGY_CORE("elite_energy_core", "elite_energy_core"),
	ULTIMATE_ENERGY_CORE("ultimate_energy_core", "ultimate_energy_core");
	
	private Item item;

	Items(String name, String registryName) {
		Item item = new Item();
		item.setUnlocalizedName(name);
		item.setRegistryName(Main.MODID, name);
		item.setCreativeTab(ModCreativeTab.getInstance());
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

}
