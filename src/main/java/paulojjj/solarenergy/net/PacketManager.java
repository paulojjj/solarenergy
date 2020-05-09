package paulojjj.solarenergy.net;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import paulojjj.solarenergy.Main;

public class PacketManager {

	public enum Message {
		TILE_ENTITY_MESSAGE, CONTAINER_UPDATE_MESSAGE
	}

	protected static SimpleNetworkWrapper wrapper;

	protected static MessageSerializer serializer = new MessageSerializer();


	public static void init() {
		wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("SolarEnergy");

		wrapper.registerMessage(TileEntityMessageHandler.class, TileEntityUpdateMessage.class, Message.TILE_ENTITY_MESSAGE.ordinal(), Side.CLIENT);
		wrapper.registerMessage(TileEntityMessageHandler.class, TileEntityUpdateMessage.class, Message.TILE_ENTITY_MESSAGE.ordinal(), Side.SERVER);		
		wrapper.registerMessage(ContainerMessageHandler.class, ContainerUpdateMessage.class, Message.CONTAINER_UPDATE_MESSAGE.ordinal(), Side.CLIENT);
		wrapper.registerMessage(ContainerMessageHandler.class, ContainerUpdateMessage.class, Message.CONTAINER_UPDATE_MESSAGE.ordinal(), Side.SERVER);		
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
		public void fromBytes(ByteBuf buf) {
			data = buf.copy();
		}

		@Override
		public void toBytes(ByteBuf buf) {
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
		public void fromBytes(ByteBuf buf) {
			int x = buf.readInt();
			int y = buf.readInt();
			int z = buf.readInt();
			pos = new BlockPos(x, y, z);

			super.fromBytes(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
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

	public static class TileEntityMessageHandler implements IMessageHandler<TileEntityUpdateMessage, IMessage> {

		private EntityPlayer getPlayer(MessageContext ctx) {
			return Main.getProxy().getFactory().getPlayerProvider().getPlayer(ctx);
		}

		@SuppressWarnings("unchecked")
		@Override
		public IMessage onMessage(TileEntityUpdateMessage message, MessageContext ctx) {
			EntityPlayer player = getPlayer(ctx);
			TileEntity tileEntity = player.getEntityWorld().getTileEntity(message.getPos());
			if(tileEntity instanceof IMessageListener<?>)  {
				Object tileMessage = PacketManager.readMessage((IMessageListener<?>)tileEntity, message);
				((IMessageListener<Object>)tileEntity).onMessage(tileMessage);
			}
			return null;
		}

	}

	public static class ContainerMessageHandler implements IMessageHandler<ContainerUpdateMessage, IMessage> {

		private EntityPlayer getPlayer(MessageContext ctx) {
			return Main.getProxy().getFactory().getPlayerProvider().getPlayer(ctx);
		}

		@SuppressWarnings("unchecked")
		@Override
		public IMessage onMessage(ContainerUpdateMessage message, MessageContext ctx) {
			EntityPlayer player = getPlayer(ctx);
			Container container = player.openContainer;
			if(container instanceof IMessageListener<?>)  {
				Object tileMessage = PacketManager.readMessage((IMessageListener<?>)container, message);
				((IMessageListener<Object>)container).onMessage(tileMessage);
			}
			return null;
		}

	}

	public static void sendToAllTracking(TileEntity tileEntity, Object message) {
		int dimension = tileEntity.getWorld().provider.getDimension();
		BlockPos pos = tileEntity.getPos();
		NetworkRegistry.TargetPoint point = new TargetPoint(dimension, pos.getX(), pos.getY(), pos.getZ(), 1.0);
		wrapper.sendToAllTracking(new TileEntityUpdateMessage(tileEntity, message), point);
	}

	public static void sendTileEntityMessage(TileEntity tileEntity, EntityPlayerMP player, Object message) {
		wrapper.sendTo(new TileEntityUpdateMessage(tileEntity, message), player);
	}

	public static void sendContainerUpdateMessage(EntityPlayerMP player, Object message) {
		wrapper.sendTo(new ContainerUpdateMessage(message), player);
	}

}
