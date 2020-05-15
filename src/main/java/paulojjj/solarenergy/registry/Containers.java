package paulojjj.solarenergy.registry;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.BatteryContainer;
import paulojjj.solarenergy.containers.EnergyAssemblerContainer;
import paulojjj.solarenergy.containers.EnergyCableContainer;
import paulojjj.solarenergy.containers.SolarGeneratorContainer;

public enum Containers {
	
	SOLAR_GENERATOR("solar_generator_container", SolarGeneratorContainer::new),
	ENERGY_ASSEMBLER("energy_assembler_container", EnergyAssemblerContainer::new),
	BATTERY("battery_container", BatteryContainer::new),
	ENERGY_CABLE("energy_cable_container", EnergyCableContainer::new);
	
	private ContainerType<?> type;

	<T extends Container> Containers(String registryName, IContainerFactory<T> supplier) {
		this.type = IForgeContainerType.<T>create(supplier);
		this.type.setRegistryName(Main.MODID, registryName);
	}

	@SuppressWarnings("unchecked")
	public <T extends Container> ContainerType<T> getType() {
		return (ContainerType<T>)type;
	}

}
