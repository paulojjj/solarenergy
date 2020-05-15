package paulojjj.solarenergy.registry;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.IContainerFactory;
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
	private INamedContainerProvider containerProvider;
	private ContainerType<?> containerType;
	
	public static class ContainerFactory<T extends Container> implements INamedContainerProvider, IContainerFactory<T> {

		
		private Containers container;
		
		public ContainerFactory(Containers container) {
			this.container = container;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T createMenu(int windowId, PlayerInventory playerInventory,
				PlayerEntity playerEntity) {
			return (T)container.getType().create(windowId, playerInventory);
		}

		@SuppressWarnings("unchecked")
		@Override
		public T create(int windowId, PlayerInventory inv, PacketBuffer data) {
			return (T)container.getType().create(windowId, inv, data);
		}

		@Override
		public ITextComponent getDisplayName() {
			return new StringTextComponent("");
		}
		
	}
	
	public <T extends Container> INamedContainerProvider getProvider(Containers containers) {
		return new ContainerFactory<T>(containers);
	}
	
	<T extends Container, U extends Screen & IHasContainer<T>> GUI(IScreenFactory<T, U> screenFactory, Containers containers) {
		this.factory = screenFactory;
		this.containerProvider = getProvider(containers);
		this.containerType = containers.getType();
	}

	public ContainerType<?> getContainerType() {
		return containerType;
	}

	public IScreenFactory<?, ?> getFactory() {
		return factory;
	}

	public INamedContainerProvider getContainerProvider() {
		return containerProvider;
	}
	
}
