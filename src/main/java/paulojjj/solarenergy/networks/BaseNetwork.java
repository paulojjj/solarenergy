package paulojjj.solarenergy.networks;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.Log;
import paulojjj.solarenergy.TickHandler;

public abstract class BaseNetwork<T extends TileEntity & INetworkMember> implements INetwork<T> {

	protected boolean valid = true;

	protected Set<T> tiles = new HashSet<>();
	protected Map<T, Map<Direction, IEnergyStorage>> storages = new HashMap<>();

	protected World world;

	protected double energyStored = 0;
	protected double maxEnergyStored = 0;
	protected boolean canReceive = false;
	protected boolean canExtract = false;

	protected double receivedSinceLastTick = 0;
	protected double sentSinceLastTick = 0;

	protected double energyInput = 0;
	protected double energyOutput = 0;
	
	protected long lastUpdatedTick = 0;

	ReentrantLock lock = new ReentrantLock();


	protected boolean canAdd(T tileEntity) {
		return tileEntity != null && !tileEntity.isRemoved() && getTileClass().isInstance(tileEntity) && tileEntity.hasWorld() && world.isAreaLoaded(tileEntity.getPos(), 0);
	}

	@Override
	public INetwork<T> init(T initialTile) {
		if(initialTile == null) {
			return this;
		}
		world = initialTile.getWorld();

		//Check if initialTile can be added to existing network
		Set<T> neighbors = getNeighbors(initialTile);
		if(!neighbors.isEmpty()) {
			T neighbor = neighbors.iterator().next();
			@SuppressWarnings("unchecked")
			BaseNetwork<T> network = (BaseNetwork<T>)neighbor.getNetwork();
			if(network != null) {
				network.addTile(initialTile);
				destroy();
				return network;
			}
		}

		updateNetwork(initialTile);

		if(tiles.isEmpty()) {
			destroy(false);
		}

		return this;
	}

	protected INetwork<T> init(Set<T> tiles, Map<T, Map<Direction, IEnergyStorage>> storages) {
		world = tiles.iterator().next().getWorld();
		this.tiles.addAll(tiles);
		this.storages.putAll(storages);
		for(T tile : tiles) {
			tile.setNetwork(this);
		}
		return this;
	}

	protected void updateNetwork(T initialTile) {
		if(!canAdd(initialTile)) {
			return;
		}
		if(!tiles.contains(initialTile)) {
			addTile(initialTile);
		}

		Set<T> connected = scanConnected(initialTile);
		Set<T> orphans = new HashSet<>();

		//Check for possible splits
		for(T tile : tiles) {
			if(!connected.contains(tile)) {
				orphans.add(tile);
			}
		}
		while(!orphans.isEmpty()) {

			T nextOrphan = null;
			Iterator<T> it = orphans.iterator();
			while(it.hasNext()) {
				nextOrphan = it.next(); 
				if(canAdd(nextOrphan)) {
					break;
				}
				else {
					it.remove();
				}
			}
			if(nextOrphan == null) {
				break;
			}
			Set<T> newNetworkTiles = scanConnected(nextOrphan);
			if(newNetworkTiles.size() > 0) {
				INetwork<T> newNetwork = split(newNetworkTiles);
				orphans.removeAll(newNetwork.getTiles());
			}
		}

		//Check new added
		for(T tile : connected) {
			if(!tiles.contains(tile)) {
				addTile(tile);
			}
		}

		updateStorages();

		if(tiles.isEmpty()) {
			destroy();
		}

		return;
	}
	
	//World.getTileEntity in unloaded chunks triggers TileEntity.onLoad
	protected TileEntity getTileEntity(BlockPos pos) {
		if(!world.isAreaLoaded(pos, 0)) {
			return null;
		}
		return world.getTileEntity(pos);
	}
	
	protected Map<Direction, IEnergyStorage> getNeighborStorages(TileEntity tileEntity, BiFunction<IEnergyStorage, Direction,  Boolean> canAdd) {
		BlockPos pos = tileEntity.getPos();
		Map<Direction, IEnergyStorage> storages = new HashMap<>();
		for(Direction facing : Direction.values()) {
			TileEntity tile = getTileEntity(pos.offset(facing));
			if(tile != null && !tile.getClass().equals(tileEntity.getClass())) {
				IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).orElse(null);
				if(energyStorage != null && canAdd.apply(energyStorage, facing)) {
					storages.put(facing, energyStorage);
				}
			}
		}
		return storages;
	}
	
	protected Map<Direction, IEnergyStorage> getStorages(TileEntity tileEntity) {
		return getNeighborStorages(tileEntity, (s, f) -> true);
	}

	protected Map<Direction, IEnergyStorage> getOrCreateStorageMap(T tile) {
		Map<Direction, IEnergyStorage> storagesMap = storages.get(tile);
		if(storagesMap == null) {
			storagesMap = new HashMap<>();
			storages.put(tile, storagesMap);
		}
		return storagesMap;
	}
	
	protected void updateStorages() {
		storages.clear();
		for(T tile : tiles) {
			Map<Direction, IEnergyStorage> tileStorages = getStorages(tile);
			storages.put(tile, tileStorages);
		}
	}

	@SuppressWarnings("unchecked")
	protected void addTile(T tile) {
		if(tile == null || !canAdd(tile) || tiles.contains(tile)) {
			return;
		}
		Log.info("Adding tile at " + tile.getPos() + " to network " + this);
		tile.setNetwork(this);
		tiles.add(tile);
		
		Map<Direction, IEnergyStorage> storages = getStorages(tile);
		if(!storages.isEmpty()) {
			this.storages.put(tile, storages);
		}
		
		Set<T> neighbors = getNeighbors(tile);
		for(T neighbor : neighbors) {
			INetwork<?> neighborNetwork = neighbor.getNetwork();
			if(neighborNetwork != null && neighborNetwork != this) {
				if(this.getTileClass().equals(neighborNetwork.getTileClass())) {
					merge((INetwork<T>)neighborNetwork);
				}
			}
		}
	}

	protected void merge(INetwork<T> other) {
		Log.info("Merging network " + this + " with " + other);
		for(T tile : (Set<T>)other.getTiles()) {
			tiles.add(tile);
			tile.setNetwork(this);
			storages.putAll(other.getStorages());
		}
		Log.info("Final network " + this);
		other.destroy();
	}
	
	protected void removeTiles(Collection<T> tilesRemoved) {
		tiles.removeAll(tilesRemoved);
		for(T tile : tilesRemoved) {
			tile.setNetwork(null);
		}
		if(tiles.isEmpty()) {
			destroy();
			return;
		}
		updateNetwork(tiles.iterator().next());
	}

	@Override
	public void onBlockRemoved(T tile) {
		removeTiles(Arrays.asList(tile));
	}
	
	public Set<T> getTilesInChunk(ChunkPos chunkPos) {
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		
		Set<T> tilesInChunk = new HashSet<>();
		for(T tile : getTiles()) {
			BlockPos pos = tile.getPos();
			int tileChunkX = pos.getX() >> 4; 
			int tileChunkZ = pos.getZ() >> 4; 
			if(tileChunkX == chunkX && tileChunkZ == chunkZ) {
				tilesInChunk.add(tile);
			}
		}
		return tilesInChunk;
	}
	
	@Override
	public void onChunkUnload(BlockPos pos) {
		//world.getChunkFromBlockCoords triggers chunk load
		ChunkPos chunkPos = new ChunkPos(pos);
		Set<T> tilesInChunk = new HashSet<>();
		Log.info("Chunk  " + chunkPos + "unloaded");
		tilesInChunk = getTilesInChunk(chunkPos);
		if(tilesInChunk.size() == 0) {
			return;
		}
		removeTiles(tilesInChunk);
	}

	@Override
	public void onNeighborChanged(T source, BlockPos neighborPos) {
		storages.remove(source);
		for(Entry<Direction, IEnergyStorage> entry : getStorages(source).entrySet()) {
			getOrCreateStorageMap(source).put(entry.getKey(), entry.getValue());
		}
	}

	protected INetwork<T> split(Set<T> newNetworkTiles) {
		Log.info("Splitting network " + this);
		try {
			Map<T, Map<Direction, IEnergyStorage>> newNetworkStorages = new HashMap<>();
			for(T tile : newNetworkTiles) {
				if(storages.containsKey(tile)) {
					newNetworkStorages.put(tile, storages.get(tile));
				}
			}
			@SuppressWarnings("unchecked")
			BaseNetwork<T>  newNetwork =  (BaseNetwork<T>)this.getClass().newInstance().init(newNetworkTiles, newNetworkStorages);
			tiles.removeAll(newNetwork.getTiles());
			Log.info("Network tiles: " + this);
			Log.info("Network created: " + newNetwork);
			return newNetwork;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Network split error", e);
		}
	}

	protected static <TE extends TileEntity> TE as(Class<TE> tileClass, Object obj) {
		if(tileClass.isInstance(obj)) {
			return tileClass.cast(obj);
		}
		return null;
	}

	protected Set<T> scanConnected(T initialTile) {
		Log.info("Scanning connected tiles for " + initialTile.getPos());
		long start = System.nanoTime();
		Set<T> scanned = new HashSet<>();
		Set<T> connected = new HashSet<>();
		if(canAdd(initialTile)) {
			connected.add(initialTile);
		}
		scanned.add(initialTile);
		scanNeighbors(initialTile, connected, scanned);

		double nanos = System.nanoTime() - start;
		double ms = nanos / 1000000.0;
		Log.info(String.format("Scanning returned %d tiles in %.3fms", connected.size(), ms));
		return connected;
	}

	protected void scanNeighbors(T tile, Set<T> connected, Set<T> scanned) {
		if(canAdd(tile) && !scanned.contains(tile)) {
			connected.add(tile);
			scanned.add(tile);
		}
		for(T neighbor : getNeighbors(tile)) {
			if(!scanned.contains(neighbor)) {
				connected.add(neighbor);
				scanned.add(neighbor);
				scanNeighbors(neighbor, connected, scanned);
			}
		}
	}
	
	public Direction[] getPossibleNeighborsPositions(T tile) {
		return Direction.values();
		
	}

	Set<T> getNeighbors(T tile) {
		Set<T> neighbors = new HashSet<>();
		for(Direction facing : getPossibleNeighborsPositions(tile)) {
			BlockPos neighborPos = tile.getPos().offset(facing);
			T neighbor = as(getTileClass(), getTileEntity(neighborPos));
			if(neighbor != null && canAdd(tile)) {
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}

	@Override
	public Set<T> getTiles() {
		return this.tiles;
	}
	
	@Override
	public Map<T, Map<Direction, IEnergyStorage>> getStorages() {
		return storages;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public void destroy() {
		destroy(true);
	}
	
	public void destroy(boolean log) {
		tiles.clear();
		valid = false;
		if(log) {
			Log.info("Network " + this + " destroyed");
		}
	}

	public void update() {
		if(world.isRemote) {
			return;
		}
		if(TickHandler.getTick() == lastUpdatedTick) {
			return;
		}
		synchronized(this) {
			lastUpdatedTick = TickHandler.getTick();

			energyStored = tiles.stream().map(x -> x.getUltraEnergyStored()).collect(Collectors.summingDouble(x -> x));
			maxEnergyStored = tiles.stream().map(x -> x.getMaxUltraEnergyStored()).collect(Collectors.summingDouble(x -> x));
			canExtract = tiles.iterator().next().canExtract();
			canReceive = tiles.iterator().next().canReceive();

			if(canExtract()) {
				double sent = sendToConsumers(energyStored, false);
				double extracted = extractUltraEnergy(sent, false);
				energyStored -= extracted;
			}
			
			energyOutput = sentSinceLastTick;
			energyInput = receivedSinceLastTick;
			receivedSinceLastTick = 0;
			sentSinceLastTick = 0;
		}
	}

	double sendEnergy(IEnergyStorage consumer, double maxEnergy, boolean simulate) {
		if(consumer instanceof IUltraEnergyStorage) {
			return sendEnergy((IUltraEnergyStorage)consumer, maxEnergy, simulate);
		}
		
		int maxEnergyInt = (int)Math.min(Integer.MAX_VALUE, maxEnergy);
		int sent = consumer.receiveEnergy(maxEnergyInt, simulate);
		return sent;
	}

	double sendEnergy(IUltraEnergyStorage consumer, double maxEnergy, boolean simulate) {
		double sent = consumer.receiveUltraEnergy(maxEnergy, simulate);
		if(sent == 0) {
			return 0;
		}
		return sent;
	}
	
	
	protected static interface TriFunction<T, U, V, R> {
	    R apply(T t, U u, V v);		
	}
	
	
	protected Set<IEnergyStorage> getStorages(TriFunction<T, Direction, IEnergyStorage, Boolean> filter) {
		Set<IEnergyStorage> storages = new HashSet<>();
		for(Entry<T, Map<Direction, IEnergyStorage>> storageEntry : this.storages.entrySet()) {
			for(Entry<Direction, IEnergyStorage> entry : storageEntry.getValue().entrySet()) {
				if(filter.apply(storageEntry.getKey(), entry.getKey(), entry.getValue())) {
					storages.add(entry.getValue());
				}
			}
		}
		return storages;
	}
	
	protected Set<IEnergyStorage> getConsumers() {
		return getStorages((t, f, s) -> s.canReceive());
	}

	protected Set<IEnergyStorage> getProducers() {
		return getStorages((t, f, s) -> s.canExtract());
	}
	
	protected double sendEqually(Set<IEnergyStorage> activeConsumers, double maxEnergy, boolean simulate) {
		double totalSent = 0;
		double lastTotalSent = -1;
		
		while(!activeConsumers.isEmpty() && totalSent < maxEnergy && lastTotalSent != totalSent) {
			lastTotalSent = totalSent;
			Iterator<IEnergyStorage> it = activeConsumers.iterator();
			double consumerSlice = Math.floor((maxEnergy - totalSent) / activeConsumers.size());
			if(consumerSlice == 0) {
				consumerSlice = 1;
			}
			while(it.hasNext()) {
				IEnergyStorage consumer = it.next();
				double consumerMax = Math.min(maxEnergy - totalSent, consumerSlice);
				double sent = sendEnergy(consumer, consumerMax, simulate);
				if(sent == 0) {
					it.remove();
				}
				totalSent += sent;
			}
		}
		return totalSent;
	}
	
	protected double sendToConsumers(double maxEnergy, boolean simulate) {
		double energyNeeded = 0;
		Set<IEnergyStorage> activeConsumers = new HashSet<>();
		for(IEnergyStorage consumer : getConsumers()) {
			double consumerNeeds = sendEnergy(consumer, Double.MAX_VALUE, true);
			energyNeeded += consumerNeeds;
			if(consumerNeeds > 0) {
				activeConsumers.add(consumer);
			}
		}
		if(simulate) {
			return energyNeeded;
		}
		
		return sendEqually(activeConsumers, maxEnergy, simulate);
	}

	double extractEnergy(IEnergyStorage producer, double maxExtract, boolean simulate) {
		if(producer instanceof IUltraEnergyStorage) {
			return (int)extractEnergy((IUltraEnergyStorage)producer, maxExtract, simulate);
		}
		int maxExtractInt = (int)Math.min(Integer.MAX_VALUE, maxExtract);
		double received = producer.extractEnergy(maxExtractInt, simulate);
		if(received == 0) {
			return 0;
		}
		return receiveEnergy((int)received, simulate);
	}

	double extractEnergy(IUltraEnergyStorage producer, double maxExtract, boolean simulate) {
		double received = producer.extractUltraEnergy(maxExtract, simulate);
		if(received == 0) {
			return 0;
		}
		return receiveUltraEnergy(received, simulate);
	}

	protected void extractEnergyFromProducers(boolean simulate) {
		Collection<IEnergyStorage> producers = getProducers();
		
		for(IEnergyStorage producer : producers) {
			double maxExtract = getMaxUltraEnergyStored() - getUltraEnergyStored();
			extractEnergy(producer, maxExtract, simulate);
		}
	}

	@Override
	public String toString() {
		return super.toString() + "[size=" + tiles.size() + "]";
	}

	void forEachTile(Consumer<T> consumer) {
		for(T tile : tiles) {
			consumer.accept(tile);
		}
	}

	@Override
	public double getUltraEnergyStored() {
		return energyStored;
	}

	@Override
	public double getMaxUltraEnergyStored() {
		return maxEnergyStored;
	}

	@Override
	public double extractUltraEnergy(double maxExtract, boolean simulate) {
		maxExtract = Math.min(maxExtract, energyStored);
		if(energyStored == 0) {
			return 0;
		}
		double totalExtracted = 0;
		for(T tile : tiles) {
			if(tile.canExtract()) {
				double extracted = tile.extractUltraEnergy(maxExtract - totalExtracted, simulate);
				totalExtracted += extracted;
				if(!simulate) {
					energyStored -= extracted;
					sentSinceLastTick += extracted;
				}
				if(extracted == maxExtract) {
					break;
				}
			}
		}
		return totalExtracted;
	}

	@Override
	public double receiveUltraEnergy(double maxReceive, boolean simulate) {
		if(maxEnergyStored == 0 || energyStored == maxEnergyStored || maxReceive == 0) {
			return 0;
		}
		double totalReceived = 0;
		for(T tile : tiles) {
			if(tile.canReceive()) {
				double received = tile.receiveUltraEnergy(maxReceive - totalReceived, simulate);
				totalReceived += received;
				if(!simulate) {
					energyStored += received;
					receivedSinceLastTick += received;
				}
				if(totalReceived == maxReceive) {
					break;
				}
			}
		}
		return totalReceived;
	}

	@Override
	public boolean canExtract() {
		return canExtract;
	}

	@Override
	public boolean canReceive() {
		return canReceive;
	}

	@Override
	public double getEnergyInput() {
		return energyInput;
	}

	@Override
	public double getEnergyOutput() {
		return energyOutput;
	}
}
