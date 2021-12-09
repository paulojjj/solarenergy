package paulojjj.solarenergy.registry;

import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import paulojjj.solarenergy.gui.BatteryGui;
import paulojjj.solarenergy.gui.EnergyAssemblerGui;
import paulojjj.solarenergy.gui.EnergyCableGui;
import paulojjj.solarenergy.gui.SolarGeneratorGui;

public enum GUI {
	BATTERY(BatteryGui::new, Containers.BATTERY),
	SOLAR_GENERATOR(SolarGeneratorGui::new, Containers.SOLAR_GENERATOR),
	ENERGY_ASSEMBLER(EnergyAssemblerGui::new, Containers.ENERGY_ASSEMBLER),
	ENERGY_CABLE(EnergyCableGui::new, Containers.ENERGY_CABLE);

	private ScreenConstructor<?, ?> factory;
	private Containers container;
	private MenuType<?> containerType;
	
	<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> GUI(ScreenConstructor<T, U> screenFactory, Containers containers) {
		this.factory = screenFactory;
		this.container = containers;
		this.containerType = containers.getType();
	}

	public MenuType<?> getContainerType() {
		return containerType;
	}

	public ScreenConstructor<?, ?> getFactory() {
		return factory;
	}
	
	public Containers getContainer() {
		return container;
	}

}
