package paulojjj.solarenergy.net;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.net.PacketManager.ContainerUpdateMessage;
import paulojjj.solarenergy.net.PacketManager.DefaultEncoder;
import paulojjj.solarenergy.net.PacketManager.TileEntityUpdateMessage;

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
		void fromBytes(FriendlyByteBuf buf);
		void toBytes(FriendlyByteBuf buf);
	}
	
	public static class DefaultEncoder {
		public static <T extends IMessage> void encode(T message, FriendlyByteBuf buffer) {
			message.toBytes(buffer);
		}

		public static <T extends IMessage> T decode(FriendlyByteBuf buffer, Class<T> clazz) {
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

		protected FriendlyByteBuf data;

		public GenericMessage() {
			
		}

		public GenericMessage(Object message) {
			this.message = message;
		}

		public FriendlyByteBuf getData() {
			return data;
		}

		@Override
		public void fromBytes(FriendlyByteBuf buf) {
			data = buf;
		}

		@Override
		public void toBytes(FriendlyByteBuf buf) {
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

		private BlockEntity tileEntity;

		private BlockPos pos;

		public TileEntityUpdateMessage() {
			super();
		}

		public TileEntityUpdateMessage(BlockEntity tileEntity, Object message) {
			super(message);
			this.tileEntity = tileEntity;
		}

		public BlockPos getPos() {
			return pos;
		}

		@Override
		public void fromBytes(FriendlyByteBuf buf) {
			int x = buf.readInt();
			int y = buf.readInt();
			int z = buf.readInt();
			pos = new BlockPos(x, y, z);

			super.fromBytes(buf);
		}

		@Override
		public void toBytes(FriendlyByteBuf buf) {
			BlockPos pos = tileEntity.getBlockPos();

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

		private static Player getPlayer(NetworkEvent.Context ctx) {
			return Main.getProxy().getFactory().getPlayerProvider().getPlayer(ctx);
		}

		@SuppressWarnings("unchecked")
		public static void onMessage(TileEntityUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
			final NetworkEvent.Context c;
			c = ctx.get();
			c.enqueueWork(() -> {
				Player player = getPlayer(c);
				BlockEntity tileEntity = player.getCommandSenderWorld().getBlockEntity(message.getPos());
				if(tileEntity instanceof IMessageListener<?>)  {
					Object tileMessage = PacketManager.readMessage((IMessageListener<?>)tileEntity, message);
					((IMessageListener<Object>)tileEntity).onMessage(tileMessage);
				}
			});
			c.setPacketHandled(true);
		}

	}

	public static class ContainerMessageHandler {

		private static Player getPlayer(NetworkEvent.Context ctx) {
			return Main.getProxy().getFactory().getPlayerProvider().getPlayer(ctx);
		}

		@SuppressWarnings("unchecked")
		public static void onMessage(ContainerUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
			final NetworkEvent.Context c;
			c = ctx.get();
			c.enqueueWork(() -> {
				Player player = getPlayer(c);
				AbstractContainerMenu container = player.containerMenu;
				if(container instanceof IMessageListener<?>)  {
					Object tileMessage = PacketManager.readMessage((IMessageListener<?>)container, message);
					((IMessageListener<Object>)container).onMessage(tileMessage);
				}
			});
			c.setPacketHandled(true);
		}

	}

	public static void sendToAllTracking(BlockEntity tileEntity, Object message) {
		BlockPos pos = tileEntity.getBlockPos();
		Level world = tileEntity.getLevel();
		if(!world.isAreaLoaded(pos, 0)) {
			return;
		}
		LevelChunk chunk = tileEntity.getLevel().getChunkAt(pos);
		wrapper.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new TileEntityUpdateMessage(tileEntity, message));
	}

	public static void sendTileEntityMessage(BlockEntity tileEntity, ServerPlayer player, Object message) {
		wrapper.send(PacketDistributor.PLAYER.with(() -> player), new TileEntityUpdateMessage(tileEntity, message));
	}

	public static void sendContainerUpdateMessage(ServerPlayer player, Object message) {
		wrapper.send(PacketDistributor.PLAYER.with(() -> player), new ContainerUpdateMessage(message));
	}

}
