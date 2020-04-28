package paulojjj.solarenergy.net;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

public class PacketManager {
	
	public enum Message {
		TILE_ENTITY_MESSAGE
	}
	
	protected static SimpleNetworkWrapper wrapper;
	
	public static void init() {
		wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("SolarEnergy");
		
		wrapper.registerMessage(TileEntityMessageHandler.class, TileEntityMessage.class, Message.TILE_ENTITY_MESSAGE.ordinal(), Side.CLIENT);
		wrapper.registerMessage(TileEntityMessageHandler.class, TileEntityMessage.class, Message.TILE_ENTITY_MESSAGE.ordinal(), Side.SERVER);		
	}
	
	public static class TileEntityMessage implements IMessage {
		
		private static MessageSerializer serializer = new MessageSerializer();
		
		private TileEntity tileEntity;
		private Object message;
		
		private BlockPos pos;
		private ByteBuf data;
		
		public TileEntityMessage() {
			
		}
		
		public TileEntityMessage(TileEntity tileEntity, Object message) {
			this.tileEntity = tileEntity;
			this.message = message;
		}

	    public ByteBuf getData() {
			return data;
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
			
			data = buf.copy();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			BlockPos pos = tileEntity.getPos();
			
			buf.writeInt(pos.getX());
			buf.writeInt(pos.getY());
			buf.writeInt(pos.getZ());
			
			serializer.write(message, buf);
		}
		
	}
	
	public static class TileEntityMessageHandler implements IMessageHandler<TileEntityMessage, IMessage> {

		private static MessageSerializer serializer = new MessageSerializer();
		
		private EntityPlayer getPlayer(MessageContext ctx) {
	        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
	            return ctx.getServerHandler().player;
	        }
	        return Minecraft.getMinecraft().player;			
		}
		
		public Class<?> getGenericClass(IMessageListener<?> listener) {
			for(Type type : listener.getClass().getGenericInterfaces()) {
				if(type instanceof ParameterizedType) {
					ParameterizedType pType = (ParameterizedType)type;
					if(pType.getRawType().equals(IMessageListener.class)) {
						return (Class<?>)pType.getActualTypeArguments()[0];
					}
				}
			}
			throw new RuntimeException("Could not get MessageListener generic class");
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public IMessage onMessage(TileEntityMessage message, MessageContext ctx) {
			EntityPlayer player = getPlayer(ctx);
			TileEntity tileEntity = player.getEntityWorld().getTileEntity(message.getPos());
			if(tileEntity instanceof IMessageListener<?>)  {
				Class<?> messageClass = getGenericClass((IMessageListener<?>)tileEntity);
				Object tileMessage = serializer.read(messageClass, message.getData());
				((IMessageListener<Object>)tileEntity).onMessage(tileMessage);
			}
			return null;
		}
		
	}
	
	public static void sendTileEntityMessage(BatteryTileEntity tileEntity, EntityPlayerMP player, Object message) {
		wrapper.sendTo(new TileEntityMessage(tileEntity, message), player);
	}

	
}
