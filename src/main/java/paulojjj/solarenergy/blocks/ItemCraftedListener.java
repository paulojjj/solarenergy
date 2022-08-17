package paulojjj.solarenergy.blocks;

import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public interface ItemCraftedListener {
	
	public void onItemCrafted(ItemCraftedEvent event);

}
