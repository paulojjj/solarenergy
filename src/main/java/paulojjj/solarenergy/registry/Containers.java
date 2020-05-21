package paulojjj.solarenergy.registry;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.containers.BaseContainer;
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
	
	public static class ContainerFactory<T extends BaseContainer<T>> implements INamedContainerProvider, IContainerFactory<T> {

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
		public T createMenu(int windowId, PlayerInventory playerInventory,
				PlayerEntity playerEntity) {
			PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());		
			buffer.writeBlockPos(pos);
			T c = (T)container.getType().create(windowId, playerInventory, buffer);
			return c;
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
	
	public INamedContainerProvider getContainerProvider(BlockPos pos) {
		return new ContainerFactory<>(this, pos);
	}
	
}
