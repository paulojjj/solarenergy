package paulojjj.solarenergy.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import paulojjj.solarenergy.registry.Items;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerBlock(ItemBlock ib) {
		super.registerBlock(ib);
		registerModelResourceLocation(ib);
	}
	
	@Override
	public void registerItem(Items item) {
		super.registerItem(item);
		registerModelResourceLocation(item.getItem());
	}
	
	protected void registerModelResourceLocation(Item item) {
		String id = item.getRegistryName().toString();
		ModelResourceLocation mrl = new ModelResourceLocation(id, "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, mrl);		
	}
	
}
