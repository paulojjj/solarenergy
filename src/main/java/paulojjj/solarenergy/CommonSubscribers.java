package paulojjj.solarenergy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import paulojjj.solarenergy.blocks.ItemCraftedListener;

@EventBusSubscriber(modid = Main.MODID)
public class CommonSubscribers {

	//Using EventSubscribers(value = Side.Server) seems to only work on dedicated server (is not called in Single Player Games from GUI)
	@SubscribeEvent
	public static void itemCrafted(ItemCraftedEvent event) {
		Item craftedItem = null;
		if(event.crafting != null) {
			craftedItem = event.crafting.getItem();
		}
		
		if(craftedItem != null && craftedItem instanceof ItemCraftedListener) {
			((ItemCraftedListener)craftedItem).onItemCrafted(event);
		}
	}
}
