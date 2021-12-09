package paulojjj.solarenergy.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import paulojjj.solarenergy.Main;

@OnlyIn(Dist.CLIENT)
public enum Textures {
	
	SOLAR_GENERATOR_SIDE("block/solar_generator_side"),
	ENERGY_CABLE_CENTER("block/energy_cable_center"),
	ENERGY_CABLE_HORIZONTAL("block/energy_cable_horizontal"),
	ENERGY_CABLE_VERTICAL("block/energy_cable_vertical");
	
	private ResourceLocation resourceLocation;
	
	private Textures(String location) {
		resourceLocation = new ResourceLocation(Main.MODID, location);
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}
	
	public TextureAtlasSprite getSprite() {
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(resourceLocation);
	}
}
