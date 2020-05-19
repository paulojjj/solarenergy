package paulojjj.solarenergy.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.tiles.BatteryTileEntity;
import paulojjj.solarenergy.tiles.EnergyAssemblerTileEntity;
import paulojjj.solarenergy.tiles.EnergyCableTileEntity;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public enum TileEntities {
	SOLAR_GENERATOR("solar_generator_tile_entity", SolarGeneratorTileEntity::new, Blocks.BASIC_SOLAR_GENERATOR, Blocks.REGULAR_SOLAR_GENERATOR, Blocks.INTERMEDIATE_SOLAR_GENERATOR, Blocks.ADVANCED_SOLAR_GENERATOR, Blocks.ELITE_SOLAR_GENERATOR, Blocks.ULTIMATE_SOLAR_GENERATOR),
	BATTERY("battery_tile_entity", BatteryTileEntity::new, Blocks.BASIC_BATTERY, Blocks.REGULAR_BATTERY, Blocks.INTERMEDIATE_BATTERY, Blocks.ADVANCED_BATTERY, Blocks.ELITE_BATTERY, Blocks.ULTIMATE_BATTERY, Blocks.BASIC_DENSE_BATTERY, Blocks.REGULAR_DENSE_BATTERY, Blocks.INTERMEDIATE_DENSE_BATTERY, Blocks.ADVANCED_DENSE_BATTERY, Blocks.ELITE_DENSE_BATTERY, Blocks.ULTIMATE_DENSE_BATTERY),
	ENERGY_ASSEMBLER("energy_assembler_tile_entity", EnergyAssemblerTileEntity::new, Blocks.ENERGY_ASSEMBLER),
	ENERGY_CABLE("energy_cable_tile_entity", EnergyCableTileEntity::new, Blocks.ENERGY_CABLE);
	
	private TileEntityType<?> type;
	
	TileEntities(String registryName, Supplier<TileEntity> supplier, Blocks... blocks) {
		
		List<Block> validBlocks = new ArrayList<>();
		for(Blocks block : blocks) {
			validBlocks.add(block.getItemBlock().getBlock());
		}
		
		this.type = TileEntityType.Builder.<TileEntity>create(supplier, validBlocks.toArray(new Block[0])).build(null);
		this.type.setRegistryName(Main.MODID, registryName);
	}

	@SuppressWarnings("unchecked")
	public <T extends TileEntity> TileEntityType<T> getType() {
		return (TileEntityType<T>)type;
	}
	
}
