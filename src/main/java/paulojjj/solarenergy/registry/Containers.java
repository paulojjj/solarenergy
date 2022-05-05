package paulojjj.solarenergy.registry;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;
import paulojjj.solarenergy.containers.BaseContainer;
import paulojjj.solarenergy.containers.BatteryContainer;
import paulojjj.solarenergy.containers.EnergyAssemblerContainer;
import paulojjj.solarenergy.containers.EnergyCableContainer;
import paulojjj.solarenergy.containers.SolarGeneratorContainer;
import paulojjj.solarenergy.proxy.CommonProxy;

public enum Containers {
	
	SOLAR_GENERATOR("solar_generator_container", SolarGeneratorContainer::new),
	ENERGY_ASSEMBLER("energy_assembler_container", EnergyAssemblerContainer::new),
	BATTERY("battery_container", BatteryContainer::new),
	ENERGY_CABLE("energy_cable_container", EnergyCableContainer::new);
	
	private RegistryObject<MenuType<?>> type;

	<T extends AbstractContainerMenu> Containers(String registryName, IContainerFactory<T> supplier) {
		this.type = CommonProxy.CONTAINERS.register(registryName, () -> IForgeMenuType.<T>create(supplier));
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractContainerMenu> MenuType<T> getType() {
		return (MenuType<T>)type.get();
	}
	
	public static class ContainerFactory<T extends BaseContainer<T>> implements MenuProvider, IContainerFactory<T> {

		private Containers container;
		private BlockPos pos;
		
		public ContainerFactory(Containers container, BlockPos pos) {
			this.container = container;
			this.pos = pos;
		}
		
		public ContainerFactory(Containers container) {
			this(container, null);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T createMenu(int windowId, Inventory playerInventory,
				Player playerEntity) {
			FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());		
			buffer.writeBlockPos(pos);
			T c = (T)container.getType().create(windowId, playerInventory, buffer);
			return c;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T create(int windowId, Inventory inv, FriendlyByteBuf data) {
			return (T)container.getType().create(windowId, inv, data);
		}

		@Override
		public Component getDisplayName() {
			return new TextComponent("");
		}
		
	}
	
	public MenuProvider getContainerProvider(BlockPos pos) {
		return new ContainerFactory<>(this, pos);
	}
	
}
