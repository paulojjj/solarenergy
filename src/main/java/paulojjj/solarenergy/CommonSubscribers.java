package paulojjj.solarenergy;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import paulojjj.solarenergy.blocks.ItemCraftedListener;

@EventBusSubscriber(modid = Main.MODID)
public class CommonSubscribers {

	//Using EventSubscribers(value = Side.Server) seems to only work on dedicated server (is not called in Single Player Games from GUI)
	@SubscribeEvent
	public static void itemCrafted(ItemCraftedEvent event) {
		Item craftedItem = null;
		if(event.getCrafting() != null) {
			craftedItem = event.getCrafting().getItem();
			if(craftedItem != null && craftedItem instanceof ItemCraftedListener) {
				((ItemCraftedListener)craftedItem).onItemCrafted(event);
			}
		}
	}

}
