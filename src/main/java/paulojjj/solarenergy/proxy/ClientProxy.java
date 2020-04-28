package paulojjj.solarenergy.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientProxy extends CommonProxy {
	
	protected static long tick = 0;
	
	@Override
	public void registerBlock(ItemBlock ib) {
		super.registerBlock(ib);
		String id = ib.getBlock().getRegistryName().toString();
		ModelResourceLocation mrl = new ModelResourceLocation(id, "inventory");
		ModelLoader.setCustomModelResourceLocation(ib, 0, mrl);
	}
	
	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(this);		
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent evt) {
		tick++;
	}

	public static long getTick() {
		return tick;
	}
}
