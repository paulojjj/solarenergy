package paulojjj.solarenergy.blocks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import paulojjj.solarenergy.gui.GuiHandler;
import paulojjj.solarenergy.gui.GuiHandler.GUI;

public class BaseBlock extends Block {
	
	private GUI gui;
	private Function<Void, ? extends TileEntity> createTileEntity;
	private BiConsumer<List<ItemStack>, TileEntity> getDrops;
	private BlockRenderLayer blockRenderLayer;
	private EnumBlockRenderType blockRenderType;
	
	private Function<Void, BlockStateContainer> createBlockStateContainer;
	
	public BaseBlock() {
		super(Material.ROCK);
	}
	
	public ConfigBuilder configBuilder() {
		return new ConfigBuilderImpl();
	}
	
	public interface ConfigBuilder {
		public ConfigBuilder resistance(float value);
		public ConfigBuilder hardness(float value);
		public <T extends Comparable<T>, V extends T> ConfigBuilder with(IProperty<T> property, V value);
		
		public ConfigBuilder blockLayer(BlockRenderLayer value);
		public ConfigBuilder renderType(EnumBlockRenderType value);
		
		public ConfigBuilder gui(GUI value);
		public ConfigBuilder createTileEntity(Function<Void, ? extends TileEntity> value);
		public ConfigBuilder getDrops(BiConsumer<List<ItemStack>, TileEntity> value);
		
		public void init();
	}
	
	public class ConfigBuilderImpl implements ConfigBuilder {
		
		private BaseBlock block = BaseBlock.this;
		private Map<IProperty<?>, Comparable<?>> properties = new LinkedHashMap<>();
		
		public ConfigBuilderImpl() {
			block.setResistance(50.0f);
			block.setHardness(3.5f);
			blockRenderLayer = BlockRenderLayer.SOLID;
			blockRenderType = EnumBlockRenderType.MODEL;
		}

		@Override
		public ConfigBuilder resistance(float value) {
			block.setResistance(value);
			return this;
		}

		@Override
		public ConfigBuilder hardness(float value) {
			block.setHardness(value);
			return this;
		}

		@Override
		public <T extends Comparable<T>, V extends T> ConfigBuilder with(IProperty<T> property, V value) {
			properties.put(property, value);
			return this;
		}
		
		@SuppressWarnings("unchecked")
		protected <T extends Comparable<T>, V extends T> void setProperty(IBlockState state, IProperty<?> t, Comparable<?> v) {
			state.withProperty((IProperty<T>)t, (V)v);			
		}
		
		public void init() {
			IBlockState state = block.getDefaultState();
			
			/*IProperty<?>[] containerProperties = new IProperty<?>[properties.size()];
			int i=0;
			for(IProperty<?> property: properties.keySet()) {
				containerProperties[i++] = property;
			}
			createBlockStateContainer = (x) -> new BlockStateContainer(block, containerProperties);*/

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
		public ConfigBuilder blockLayer(BlockRenderLayer value) {
			block.blockRenderLayer = value;
			return this;
		}

		@Override
		public ConfigBuilder renderType(EnumBlockRenderType value) {
			block.blockRenderType = value;
			return this;
		}

	}
	
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote || gui == null) {
			return true;
		}
		return GuiHandler.openGui(playerIn, worldIn, gui, pos);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return createTileEntity != null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		if(createTileEntity != null) {
			TileEntity te = createTileEntity.apply(null); 
			return te;
		}
		return super.createTileEntity(world, state);
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player,
			boolean willHarvest) {
		if(getDrops != null && willHarvest) {
			return true;
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		if(getDrops != null) {
			worldIn.setBlockToAir(pos);
		}
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		if(getDrops != null) {
			TileEntity te = world.getTileEntity(pos);
			getDrops.accept(drops, te);
		}
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return blockRenderLayer;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return blockRenderType;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		if(createBlockStateContainer == null) {
			return super.createBlockState();
		}
		return createBlockStateContainer.apply(null);
	}
	
	
	

}
