package paulojjj.solarenergy.registry;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import paulojjj.solarenergy.ModCreativeTab;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.blocks.BatteryItemBlock;
import paulojjj.solarenergy.blocks.EnergyAssembler;
import paulojjj.solarenergy.blocks.EnergyCable;
import paulojjj.solarenergy.blocks.SolarGeneratorItemBlock;

public enum Blocks {

	BASIC_SOLAR_GENERATOR("basic_solar_generator", new SolarGeneratorItemBlock(Tier.BASIC)),
	REGULAR_SOLAR_GENERATOR("regular_solar_generator", new SolarGeneratorItemBlock(Tier.REGULAR)),
	INTERMEDIATE_SOLAR_GENERATOR("intermediate_solar_generator", new SolarGeneratorItemBlock(Tier.INTERMEDIATE)),
	ADVANCED_SOLAR_GENERATOR("advanced_solar_generator", new SolarGeneratorItemBlock(Tier.ADVANCED)),
	ELITE_SOLAR_GENERATOR("elite_solar_generator", new SolarGeneratorItemBlock(Tier.ELITE)),
	ULTIMATE_SOLAR_GENERATOR("ultimate_solar_generator", new SolarGeneratorItemBlock(Tier.ULTIMATE)),
	BASIC_BATTERY("basic_battery", new BatteryItemBlock(Tier.BASIC)),
	REGULAR_BATTERY("regular_battery", new BatteryItemBlock(Tier.REGULAR)),
	INTERMEDIATE_BATTERY("intermediate_battery", new BatteryItemBlock(Tier.INTERMEDIATE)),
	ADVANCED_BATTERY("advanced_battery", new BatteryItemBlock(Tier.ADVANCED)),
	ELITE_BATTERY("elite_battery", new BatteryItemBlock(Tier.ELITE)),
	ULTIMATE_BATTERY("ultimate_battery", new BatteryItemBlock(Tier.ULTIMATE)),
	BASIC_DENSE_BATTERY("basic_dense_battery", new BatteryItemBlock(Tier.BASIC_DENSE)),
	REGULAR_DENSE_BATTERY("regular_dense_battery", new BatteryItemBlock(Tier.REGULAR_DENSE)),
	INTERMEDIATE_DENSE_BATTERY("intermediate_dense_battery", new BatteryItemBlock(Tier.INTERMEDIATE_DENSE)),
	ADVANCED_DENSE_BATTERY("advanced_dense_battery", new BatteryItemBlock(Tier.ADVANCED_DENSE)),
	ELITE_DENSE_BATTERY("elite_dense_battery", new BatteryItemBlock(Tier.ELITE_DENSE)),
	ULTIMATE_DENSE_BATTERY("ultimate_dense_battery", new BatteryItemBlock(Tier.ULTIMATE_DENSE)),
	ENERGY_ASSEMBLER("energy_assembler", new EnergyAssembler()),
	ENERGY_CABLE("energy_cable", new EnergyCable());
	
	private BlockItem itemBlock;
	
	Blocks(String name, Block block) {
		this(name, new BlockItem(block, new Item.Properties().group(ModCreativeTab.getInstance())));
	}

	Blocks(String name, BlockItem itemBlock) {
		this.itemBlock = itemBlock;
		
		itemBlock.setRegistryName(name);
		Block block = itemBlock.getBlock();
		block.setRegistryName(name);
	}
	
	public BlockItem getItemBlock() {
		return itemBlock;
	}
	
}
