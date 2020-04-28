package paulojjj.solarenergy;

import java.io.File;

import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import paulojjj.solarenergy.proxy.CommonProxy;
import paulojjj.solarenergy.proxy.Proxy;
import paulojjj.solarenergy.tiles.BatteryTileEntity;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
	public static final String MODID = "solarenergy";
	public static final String NAME = "Solar Energy MOD";
	public static final String VERSION = "1.0";

	public static Logger logger;

	public static SoundEvent sound = null;
	
	public static SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("SolarEnergy");

	@Instance(value = MODID)
	public static Main instance;
	
	@SidedProxy(clientSide = "paulojjj.solarenergy.proxy.ClientProxy", serverSide = "paulojjj.solarenergy.proxy.CommonProxy")
	private static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		File configFile = event.getSuggestedConfigurationFile();
		Configuration cfg = new Configuration(configFile);
		Config.init(cfg);

		proxy.registerBlocks();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerGuiHandler();
		proxy.registerHandlers();
		
		wrapper.registerMessage(BatteryTileMessageHandler.class, BatteryTileUpdateMessage.class, 0, Side.CLIENT);
		wrapper.registerMessage(BatteryTileMessageHandler.class, BatteryTileUpdateMessage.class, 0, Side.SERVER);
	}
	
	public static class BatteryTileMessageHandler implements IMessageHandler<BatteryTileUpdateMessage, IMessage> {
		
		private EntityPlayer getPlayer(MessageContext ctx) {
	        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
	            return ctx.getServerHandler().player;
	        }
	        return Minecraft.getMinecraft().player;			
		}

		@Override
		public IMessage onMessage(BatteryTileUpdateMessage message, MessageContext ctx) {
			EntityPlayer player = getPlayer(ctx);
			BatteryTileEntity tileEntity = (BatteryTileEntity)player.getEntityWorld().getTileEntity(message.getPos());
			if(tileEntity != null && !tileEntity.isInvalid()) {
				tileEntity.readFrom(message.getData());
			}
			else {
				logger.warn("Received message for invalid TileEntity at " + message.getPos());				
			}
			
			return null;
		}
		
	}
	
	public static class BatteryTileUpdateMessage implements IMessage {
		
		private BlockPos pos;
		private ByteBuf data;
		
		private BatteryTileEntity tileEntity;
		
		public BatteryTileUpdateMessage() {
			
		}
		
		public BatteryTileUpdateMessage(BatteryTileEntity tileEntity) {
			this.tileEntity = tileEntity;
		}

	    public ByteBuf getData() {
			return data;
		}

		public BlockPos getPos() {
			return pos;
		}

		public static void writeNBT(ByteBuf output, NBTTagCompound nbtTags) {
	        ByteBufUtils.writeTag(output, nbtTags);
	    }

	    public static NBTTagCompound readNBT(ByteBuf input) {
	        return ByteBufUtils.readTag(input);
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
			
			tileEntity.writeTo(buf);
			
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                System.out.println("Sent BatteryTileUpdateMessage");
            }
			
		}
		
		
	}
	
	public static void sendTileUpdate(BatteryTileEntity tileEntity, EntityPlayerMP player) {
		//int dimension = tileEntity.getWorld().provider.getDimension();
		//BlockPos pos = tileEntity.getPos();
		//TargetPoint point = new TargetPoint(dimension, pos.getX(), pos.getY(), pos.getZ(), 1);
		wrapper.sendTo(new BatteryTileUpdateMessage(tileEntity), player);
		//wrapper.sendToAllTracking(new TileUpdateMessage(tileEntity), point);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		proxy.registerCommands();
	}
	
	public static Proxy getProxy() {
		return proxy;
	}

}
