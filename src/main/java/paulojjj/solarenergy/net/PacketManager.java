package paulojjj.solarenergy.net;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import paulojjj.solarenergy.Main;

public class PacketManager {

	private static final String PROTOCOL_VERSION = "1";	
	
	public enum Message {
		TILE_ENTITY_MESSAGE, CONTAINER_UPDATE_MESSAGE
	}

	protected static SimpleChannel wrapper;

	protected static MessageSerializer serializer = new MessageSerializer();


	public static void init() {
		wrapper = NetworkRegistry.newSimpleChannel(
				new ResourceLocation(Main.MODID, "main"),
				() -> PROTOCOL_VERSION,
				PROTOCOL_VERSION::equals,
				PROTOCOL_VERSION::equals
		);

		wrapper.registerMessage(Message.TILE_ENTITY_MESSAGE.ordinal(), TileEntityUpdateMessage.class, DefaultEncoder::encode, (b) -> DefaultEncoder.decode(b, TileEntityUpdateMessage.class), TileEntityMessageHandler::onMessage);
		wrapper.registerMessage(Message.CONTAINER_UPDATE_MESSAGE.ordinal(), ContainerUpdateMessage.class, DefaultEncoder::encode, (b) -> DefaultEncoder.decode(b, ContainerUpdateMessage.class), ContainerMessageHandler::onMessage);
	}
	
	public static interface IMessage {
		void fromBytes(PacketBuffer buf);
		void toBytes(PacketBuffer buf);
	}
	
	public static class DefaultEncoder {
		public static <T extends IMessage> void encode(T message, PacketBuffer buffer) {
			message.toBytes(buffer);
		}

		public static <T extends IMessage> T decode(PacketBuffer buffer, Class<T> clazz) {
			T message;
			try {
				message = clazz.newInstance();
				message.fromBytes(buffer);
				return message;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class GenericMessage implements IMessage {

		protected static MessageSerializer serializer = new MessageSerializer();

		protected Object message;

		protected ByteBuf data;

		public GenericMessage() {
			
		}

		public GenericMessage(Object message) {
			this.message = message;
		}

		public ByteBuf getData() {
			return data;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
			data = buf.copy();
		}

		@Override
		public void toBytes(PacketBuffer buf) {
			serializer.write(message, buf);
		}

	}

	public static class ContainerUpdateMessage extends GenericMessage {
		public ContainerUpdateMessage() {
		}
		public ContainerUpdateMessage(Object message) {
			super(message);
		}
	}

	public static class TileEntityUpdateMessage extends GenericMessage {

		private TileEntity tileEntity;

		private BlockPos pos;

		public TileEntityUpdateMessage() {
			super();
		}

		public TileEntityUpdateMessage(TileEntity tileEntity, Object message) {
			super(message);
			this.tileEntity = tileEntity;
		}

		public BlockPos getPos() {
			return pos;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
			int x = buf.readInt();
			int y = buf.readInt();
			int z = buf.readInt();
			pos = new BlockPos(x, y, z);

			super.fromBytes(buf);
		}

		@Override
		public void toBytes(PacketBuffer buf) {
			BlockPos pos = tileEntity.getPos();

			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());

			super.toBytes(buf);
		}

	}

	public static Class<?> getGenericClass(IMessageListener<?> listener) {
		Class<?> listenerClass = listener.getClass(); 
		while(listenerClass != null) {
			for(Type type : listenerClass.getGenericInterfaces()) {
				if(type instanceof ParameterizedType) {
					ParameterizedType pType = (ParameterizedType)type;
					if(pType.getRawType().equals(IMessageListener.class)) {
						return (Class<?>)pType.getActualTypeArguments()[0];
					}
				}
			}
			listenerClass = listenerClass.getSuperclass();
		}
		throw new RuntimeException("Could not get MessageListener generic class");
	}

	public static Object readMessage(IMessageListener<?> listener, GenericMessage message) {
		Class<?> messageClass = getGenericClass(listener);
		Object data = serializer.read(messageClass, message.getData());
		message.getData().release();
		return data;
	}

	public static class TileEntityMessageHandler {

		private static PlayerEntity getPlayer(NetworkEvent.Context ctx) {
			return Main.getProxy().getFactory().getPlayerProvider().getPlayer(ctx);
		}

		@SuppressWarnings("unchecked")
		public static void onMessage(TileEntityUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
			final NetworkEvent.Context c;
			c = ctx.get();
			c.enqueueWork(() -> {
				PlayerEntity player = getPlayer(c);
				TileEntity tileEntity = player.getEntityWorld().getTileEntity(message.getPos());
				if(tileEntity instanceof IMessageListener<?>)  {
					Object tileMessage = PacketManager.readMessage((IMessageListener<?>)tileEntity, message);
					((IMessageListener<Object>)tileEntity).onMessage(tileMessage);
				}
			});
			c.setPacketHandled(true);
		}

	}

	public static class ContainerMessageHandler {

		private static PlayerEntity getPlayer(NetworkEvent.Context ctx) {
			return Main.getProxy().getFactory().getPlayerProvider().getPlayer(ctx);
		}

		@SuppressWarnings("unchecked")
		public static void onMessage(ContainerUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
			final NetworkEvent.Context c;
			c = ctx.get();
			c.enqueueWork(() -> {
				PlayerEntity player = getPlayer(c);
				Container container = player.openContainer;
				if(container instanceof IMessageListener<?>)  {
					Object tileMessage = PacketManager.readMessage((IMessageListener<?>)container, message);
					((IMessageListener<Object>)container).onMessage(tileMessage);
				}
			});
			c.setPacketHandled(true);
		}

	}

	public static void sendToAllTracking(TileEntity tileEntity, Object message) {
		BlockPos pos = tileEntity.getPos();
		World world = tileEntity.getWorld();
		if(!world.isAreaLoaded(pos, 0)) {
			return;
		}
		Chunk chunk = tileEntity.getWorld().getChunkAt(pos);
		wrapper.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new TileEntityUpdateMessage(tileEntity, message));
	}

	public static void sendTileEntityMessage(TileEntity tileEntity, ServerPlayerEntity player, Object message) {
		wrapper.send(PacketDistributor.PLAYER.with(() -> player), new TileEntityUpdateMessage(tileEntity, message));
	}

	public static void sendContainerUpdateMessage(ServerPlayerEntity player, Object message) {
		wrapper.send(PacketDistributor.PLAYER.with(() -> player), new ContainerUpdateMessage(message));
	}

}
