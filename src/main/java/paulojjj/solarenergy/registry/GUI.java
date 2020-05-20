package paulojjj.solarenergy.registry;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import paulojjj.solarenergy.gui.BatteryGui;
import paulojjj.solarenergy.gui.EnergyAssemblerGui;
import paulojjj.solarenergy.gui.EnergyCableGui;
import paulojjj.solarenergy.gui.SolarGeneratorGui;

public enum GUI {
	BATTERY(BatteryGui::new, Containers.BATTERY),
	SOLAR_GENERATOR(SolarGeneratorGui::new, Containers.SOLAR_GENERATOR),
	ENERGY_ASSEMBLER(EnergyAssemblerGui::new, Containers.ENERGY_ASSEMBLER),
	ENERGY_CABLE(EnergyCableGui::new, Containers.ENERGY_CABLE);

	private IScreenFactory<?, ?> factory;
	private Containers container;
	private ContainerType<?> containerType;
	
	<T extends Container, U extends Screen & IHasContainer<T>> GUI(IScreenFactory<T, U> screenFactory, Containers containers) {
		this.factory = screenFactory;
		this.container = containers;
		this.containerType = containers.getType();
	}

	public ContainerType<?> getContainerType() {
		return containerType;
	}

	public IScreenFactory<?, ?> getFactory() {
		return factory;
	}
	
	public Containers getContainer() {
		return container;
	}

}
