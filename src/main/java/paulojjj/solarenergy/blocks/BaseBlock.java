package paulojjj.solarenergy.blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.registry.Containers;
import paulojjj.solarenergy.tiles.BaseTileEntity;

public class BaseBlock extends Block implements EntityBlock {
	
	private Containers guiContainer;
	private BiFunction<BlockPos, BlockState, ? extends BlockEntity> createTileEntity;
	private BiConsumer<List<ItemStack>, BlockEntity> getDrops;
	private RenderShape blockRenderType = RenderShape.MODEL;
	private RenderLayer renderLayer;
	
	private List<Property<?>> properties;
	
	public enum RenderLayer {
		SOLID, CUTOUT, CUTOUT_MIPPED, TRANSLUCENT
	}

	
	public BaseBlock(Block.Properties properties) {
		super(properties);
	}
	
	public BaseBlock(PropertiesBuilder builder) {
		super(builder.build());
	}
	
	public RenderLayer getRenderLayer() {
		return renderLayer;
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
		PropertiesBuilder notSolid();
		Block.Properties build();
	}
	
	public interface ConfigBuilder {
		public <T extends Comparable<T>, V extends T> ConfigBuilder property(Property<T> property);
		
		public ConfigBuilder renderType(RenderShape value);
		public ConfigBuilder renderLayer(RenderLayer value);
		
		public ConfigBuilder guiContainer(Containers value);
		public ConfigBuilder createTileEntity(BiFunction<BlockPos, BlockState, ? extends BlockEntity> value);
		public ConfigBuilder getDrops(BiConsumer<List<ItemStack>, BlockEntity> value);
		
		public void init();
	}
	
	public static class PropertiesBuilderImpl implements PropertiesBuilder {
		
		Block.Properties properties;
		private float hardness = 2.0f;
		private float resistance = 50f;

		public PropertiesBuilderImpl() {
			this(Material.STONE);
		}
		
		public PropertiesBuilderImpl(Material material) {
			properties = Block.Properties.of(material);
			properties.strength(hardness,  resistance);
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
			properties.strength(hardness, resistance);
			return properties;
		}

		@Override
		public PropertiesBuilder notSolid() {
			properties.noOcclusion();
			return this;
		}
		
		
	}
	
	public class ConfigBuilderImpl implements ConfigBuilder {
		
		private BaseBlock block = BaseBlock.this;
		private Set<Property<?>> properties = new HashSet<>();
		
		public ConfigBuilderImpl() {
		}

		@Override
		public <T extends Comparable<T>, V extends T> ConfigBuilder property(Property<T> property) {
			properties.add(property);
			return this;
		}
		
		public void init() {
			BaseBlock.this.properties = new ArrayList<>();
			for(Property<?> property: properties) {
				block.properties.add(property);
			}
		}

		@Override
		public ConfigBuilder guiContainer(Containers value) {
			block.guiContainer = value;
			return this;
		}

		@Override
		public ConfigBuilder createTileEntity(BiFunction<BlockPos, BlockState, ? extends BlockEntity> value) {
		block.createTileEntity = value;
			return this;
		}

		@Override
		public ConfigBuilder getDrops(BiConsumer<List<ItemStack>, BlockEntity> value) {
			block.getDrops = value;
			return this;
		}

		@Override
		public ConfigBuilder renderType(RenderShape value) {
			block.blockRenderType = value;
			return this;
		}

		@Override
		public ConfigBuilder renderLayer(RenderLayer value) {
			block.renderLayer = value;
			return this;
		}

	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos,
			Player player, InteractionHand handIn, BlockHitResult hit) {
		if(worldIn.isClientSide || guiContainer == null) {
			return InteractionResult.SUCCESS;
		}
		GuiHandler.openGui(player, worldIn, guiContainer, pos);
		return InteractionResult.SUCCESS;
	}
		
	@Override
	@Deprecated
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> stacks = super.getDrops(state, builder);
		//Since 1.14 blocks have no drops by default (they must be defined in loot tables).
		if(stacks.isEmpty()) {
			stacks.add(new ItemStack(this));
		}
		if(getDrops != null) {
			BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
			getDrops.accept(stacks, te);
		}
		return stacks;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return blockRenderType;
	}
	
	@Override
	protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		if(properties != null) {
			for(Property<?> p : properties) {
				builder.add(p);
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
			return createTileEntity.apply(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return BaseTileEntity::tick;
	}
	
}
