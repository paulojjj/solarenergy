package paulojjj.solarenergy.blocks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraft.world.storage.loot.LootParameters;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.registry.GUI;

public class BaseBlock extends Block {
	
	private GUI gui;
	private Function<Void, ? extends TileEntity> createTileEntity;
	private BiConsumer<List<ItemStack>, TileEntity> getDrops;
	private BlockRenderType blockRenderType;
	
	private List<IProperty<?>> properties;

	
	public BaseBlock(Block.Properties properties) {
		super(properties);
	}
	
	public BaseBlock(PropertiesBuilder builder) {
		super(builder.build());
	}
	
	public static PropertiesBuilder propertiesBuilder() {
		return new PropertiesBuilderImpl();
	}
	
	public ConfigBuilder configBuilder() {
		return new ConfigBuilderImpl();
	}
	
	public interface PropertiesBuilder {
		PropertiesBuilder hardness(float value);
		PropertiesBuilder resistance(float value);
		Block.Properties build();
	}
	
	public interface ConfigBuilder {
		public <T extends Comparable<T>, V extends T> ConfigBuilder with(IProperty<T> property, V value);
		
		public ConfigBuilder renderType(BlockRenderType value);
		
		public ConfigBuilder gui(GUI value);
		public ConfigBuilder createTileEntity(Function<Void, ? extends TileEntity> value);
		public ConfigBuilder getDrops(BiConsumer<List<ItemStack>, TileEntity> value);
		
		public void init();
	}
	
	public static class PropertiesBuilderImpl implements PropertiesBuilder {
		
		Block.Properties properties;
		private float hardness = 50.0f;
		private float resistance = 3.5f;

		public PropertiesBuilderImpl() {
			this(Material.ROCK);
		}
		
		public PropertiesBuilderImpl(Material material) {
			properties = Block.Properties.create(material);
			properties.hardnessAndResistance(50.0f,  3.5f);
		}

		@Override
		public PropertiesBuilder hardness(float value) {
			this.hardness = value;
			return this;
		}

		@Override
		public PropertiesBuilder resistance(float value) {
			this.resistance = value;
			return this;
		}
		
		@Override
		public Properties build() {
			properties.hardnessAndResistance(hardness, resistance);
			return properties;
		}
		
		
	}
	
	public class ConfigBuilderImpl implements ConfigBuilder {
		
		private BaseBlock block = BaseBlock.this;
		private Map<IProperty<?>, Comparable<?>> properties = new LinkedHashMap<>();
		
		public ConfigBuilderImpl() {
			blockRenderType = BlockRenderType.MODEL;
		}

		@Override
		public <T extends Comparable<T>, V extends T> ConfigBuilder with(IProperty<T> property, V value) {
			properties.put(property, value);
			return this;
		}
		
		@SuppressWarnings("unchecked")
		protected <T extends Comparable<T>, V extends T> void setProperty(BlockState state, IProperty<?> t, Comparable<?> v) {
			properties.put(t, v);
			state.with((IProperty<T>)t, (V)v);			
		}
		
		public void init() {
			StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(BaseBlock.this);
			//builder.
			//BlockState. state = new BlockState(BaseBlock.this, null);
			
			BaseBlock.this.properties = new ArrayList<>();
			for(IProperty<?> property: properties.keySet()) {
				BaseBlock.this.properties.add(property);
				builder.add(property);
			}

			StateContainer<Block, BlockState> sc = builder.create(BlockState::new);
			BlockState state = sc.getBaseState();
			block.setDefaultState(sc.getBaseState());
			for(Entry<IProperty<?>, Comparable<?>> entry: properties.entrySet()) {
				IProperty<? extends Comparable<?>> key = entry.getKey();
				Comparable<?> value = entry.getValue();
				setProperty(state, key, value);
			}
			block.setDefaultState(state);
		}

		@Override
		public ConfigBuilder gui(GUI value) {
			block.gui = value;
			return this;
		}

		@Override
		public ConfigBuilder createTileEntity(Function<Void, ? extends TileEntity> value) {
			block.createTileEntity = value;
			return this;
		}

		@Override
		public ConfigBuilder getDrops(BiConsumer<List<ItemStack>, TileEntity> value) {
			block.getDrops = value;
			return this;
		}

		@Override
		public ConfigBuilder renderType(BlockRenderType value) {
			block.blockRenderType = value;
			return this;
		}

	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(worldIn.isRemote || gui == null) {
			return ActionResultType.PASS;
		}
		GuiHandler.openGui(player, worldIn, gui, pos);
		return ActionResultType.CONSUME;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return createTileEntity != null;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if(createTileEntity != null) {
			TileEntity te = createTileEntity.apply(null); 
			return te;
		}
		return super.createTileEntity(state, world);
	}
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player,
			boolean willHarvest, IFluidState fluid) {
		if(getDrops != null && willHarvest) {
			return true;
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
	
	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te,
			ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		if(getDrops != null) {
			worldIn.removeBlock(pos, false);
		}
	}
	
	@Override
	@Deprecated
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> stacks = super.getDrops(state, builder);
		if(getDrops != null) {
			TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
			getDrops.accept(stacks, te);
		}
		return stacks;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return blockRenderType;
	}
	
	@Override
	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, BlockState> builder) {
		if(properties != null) {
			for(IProperty<?> p : properties) {
				builder.add(p);
			}
		}
	}
	
}
