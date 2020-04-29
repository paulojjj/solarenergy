package paulojjj.solarenergy.networks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.proxy.CommonProxy;

public abstract class BaseNetwork<T extends TileEntity & INetworkMember> implements INetwork<T> {

	protected boolean valid = true;

	private Set<T> tiles = new HashSet<>();
	private Map<T, Set<IEnergyStorage>> consumers = new HashMap<>();
	private Map<T, Set<IEnergyStorage>> producers = new HashMap<>();

	protected World world;

	protected abstract Class<T> getTileClass();

	private double energyStored = 0;
	private double maxEnergyStored = 0;
	private boolean canReceive = false;
	private boolean canExtract = false;

	private double receivedSinceLastTick = 0;
	private double sentSinceLastTick = 0;

	private double energyInput = 0;
	private double energyOutput = 0;
	
	protected long lastUpdatedTick = 0;

	ReentrantLock lock = new ReentrantLock();


	protected boolean canAdd(T tileEntity) {
		return tileEntity != null && !tileEntity.isInvalid() && getTileClass().isInstance(tileEntity) && tileEntity.hasWorld() && world.isBlockLoaded(tileEntity.getPos());
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
			destroy();
		}

		return this;
	}

	protected INetwork<T> init(Set<T> tiles, Map<T, Set<IEnergyStorage>> consumers, Map<T, Set<IEnergyStorage>> producers) {
		world = tiles.iterator().next().getWorld();
		this.tiles.addAll(tiles);
		this.consumers.putAll(consumers);
		this.producers.putAll(producers);
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

		updateConsumers();
		updateProducers();

		if(tiles.isEmpty()) {
			destroy();
		}

		return;
	}
	
	//World.getTileEntity in unloaded chunks triggers TileEntity.onLoad
	protected TileEntity getTileEntity(BlockPos pos) {
		return world.isBlockLoaded(pos) ? world.getTileEntity(pos) : null;
	}
	
	protected Set<IEnergyStorage> getNeighborStorages(TileEntity tileEntity, BiFunction<IEnergyStorage, EnumFacing,  Boolean> canAdd) {
		BlockPos pos = tileEntity.getPos();
		Set<IEnergyStorage> storages = new HashSet<>();
		for(EnumFacing facing : EnumFacing.values()) {
			TileEntity tile = getTileEntity(pos.offset(facing));
			if(tile != null && !tile.getClass().equals(tileEntity.getClass()) && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
				IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
				if(canAdd.apply(energyStorage, facing)) {
					storages.add(energyStorage);
				}
			}
		}
		return storages;
	}

	protected Set<IEnergyStorage> getConsumers(TileEntity tileEntity) {
		return getNeighborStorages(tileEntity, (s, f) -> s.canReceive());
	}

	protected Set<IEnergyStorage> getProducers(TileEntity tileEntity) {
		return getNeighborStorages(tileEntity, (s, f) -> s.canExtract());
	}
	
	protected Set<IEnergyStorage> getOrCreateConsumerSet(T tile) {
		Set<IEnergyStorage> tc = consumers.get(tile);
		if(tc == null) {
			tc = new HashSet<>();
			consumers.put(tile, tc);
		}
		return tc;
	}

	protected Set<IEnergyStorage> getOrCreateProducerSet(T tile) {
		Set<IEnergyStorage> tc = producers.get(tile);
		if(tc == null) {
			tc = new HashSet<>();
			producers.put(tile, tc);
		}
		return tc;
	}
	
	protected void updateConsumers() {
		consumers.clear();
		for(T tile : tiles) {
			Set<IEnergyStorage> tileConsumers = getConsumers(tile);
			consumers.put(tile, tileConsumers);
		}
	}

	protected void updateProducers() {
		producers.clear();
		for(T tile : tiles) {
			Set<IEnergyStorage> tileProducers = getProducers(tile);
			producers.put(tile, tileProducers);
		}
	}
	
	protected void addTile(T tile) {
		if(tile == null || !canAdd(tile) || tiles.contains(tile)) {
			return;
		}
		Main.logger.info("Adding tile at " + tile.getPos() + " to network " + this);
		tiles.add(tile);
		tile.setNetwork(this);
		
		Set<IEnergyStorage> storages = getConsumers(tile);
		if(!storages.isEmpty()) {
			consumers.put(tile, storages);
		}
		storages = getProducers(tile);
		if(!storages.isEmpty()) {
			producers.put(tile, storages);
		}
		
		Set<T> neighbors = getNeighbors(tile);
		for(T neighbor : neighbors) {
			INetwork<?> neighborNetwork = neighbor.getNetwork();
			if(neighborNetwork != null && neighborNetwork != this) {
				merge(neighborNetwork);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void merge(INetwork<?> network) {
		Main.logger.info("Merging network " + this + " with " + network);
		for(T tile : (Set<T>)network.getTiles()) {
			tiles.add(tile);
			tile.setNetwork(this);
		}
		Main.logger.info("Final network " + this);
		network.destroy();
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
		Main.logger.info("Chunk  " + chunkPos + "unloaded");
		tilesInChunk = getTilesInChunk(chunkPos);
		if(tilesInChunk.size() == 0) {
			return;
		}
		removeTiles(tilesInChunk);
	}

	@Override
	public void onNeighborChanged(T source, BlockPos neighborPos) {
		consumers.remove(source);
		producers.remove(source);
		for(IEnergyStorage consumer : getConsumers(source)) {
			getOrCreateConsumerSet(source).add(consumer);
		}
		for(IEnergyStorage producer : getProducers(source)) {
			getOrCreateProducerSet(source).add(producer);
		}
	}

	protected INetwork<T> split(Set<T> newNetworkTiles) {
		Main.logger.info("Splitting network " + this);
		try {
			Map<T, Set<IEnergyStorage>> newNetworkConsumers = new HashMap<>();
			Map<T, Set<IEnergyStorage>> newNetworkProducers = new HashMap<>();
			for(T tile : newNetworkTiles) {
				if(consumers.containsKey(tile)) {
					newNetworkConsumers.put(tile, consumers.get(tile));
				}
				if(producers.containsKey(tile)) {
					newNetworkProducers.put(tile, producers.get(tile));
				}
			}
			@SuppressWarnings("unchecked")
			BaseNetwork<T>  newNetwork =  (BaseNetwork<T>)this.getClass().newInstance().init(newNetworkTiles, newNetworkConsumers, newNetworkProducers);
			tiles.removeAll(newNetwork.getTiles());
			Main.logger.info("Network tiles: " + this);
			Main.logger.info("Network created: " + newNetwork);
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
		Main.logger.info("Scanning connected tiles for " + initialTile.getPos());
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
		Main.logger.info(String.format("Scanning returned %d tiles in %.3fms", connected.size(), ms));
		return connected;
	}

	protected void scanNeighbors(T tile, Set<T> connected, Set<T> scanned) {
		if(canAdd(tile) && !scanned.contains(tile)) {
			connected.add(tile);
			scanned.add(tile);
		}
		for(T neighbor : getNeighbors(tile)) {
			if(!scanned.contains(neighbor)) {
				tiles.add(neighbor);
				connected.add(neighbor);
				scanned.add(neighbor);
				scanNeighbors(neighbor, connected, scanned);
			}
		}
	}
	
	public EnumFacing[] getPossibleNeighborsPositions(T tile) {
		return EnumFacing.VALUES;
		
	}

	Set<T> getNeighbors(T tile) {
		Set<T> neighbors = new HashSet<>();
		for(EnumFacing facing : getPossibleNeighborsPositions(tile)) {
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
	public boolean isValid() {
		return valid;
	}

	@Override
	public void destroy() {
		tiles.clear();
		valid = false;
		Main.logger.info("Network " + this + " destroyed");
	}

	public void update() {
		if(world.isRemote) {
			return;
		}
		if(CommonProxy.getTick() == lastUpdatedTick) {
			return;
		}
		synchronized(this) {
			lastUpdatedTick = CommonProxy.getTick();

			energyStored = tiles.stream().map(x -> x.getUltraEnergyStored()).collect(Collectors.summingDouble(x -> x));
			maxEnergyStored = tiles.stream().map(x -> x.getMaxUltraEnergyStored()).collect(Collectors.summingDouble(x -> x));
			canExtract = tiles.stream().anyMatch(x -> x.canExtract());
			canReceive = tiles.stream().anyMatch(x -> x.canReceive());

			sendEnergyToConsumers();
			extractEnergyFromProducers();
			
			energyOutput = sentSinceLastTick;
			energyInput = receivedSinceLastTick;
			receivedSinceLastTick = 0;
			sentSinceLastTick = 0;
		}
	}

	double sendEnergy(IEnergyStorage consumer, double maxEnergy) {
		if(consumer instanceof IUltraEnergyStorage) {
			return (int)sendEnergy((IUltraEnergyStorage)consumer, maxEnergy);
		}
		int maxEnergyInt = (int)Math.min(Integer.MAX_VALUE, maxEnergy);
		double sent = consumer.receiveEnergy(maxEnergyInt, false);
		if(sent == 0) {
			return 0;
		}
		return extractEnergy((int)sent, false);
	}

	double sendEnergy(IUltraEnergyStorage consumer, double maxEnergy) {
		double sent = consumer.receiveUltraEnergy(maxEnergy, false);
		System.out.println("Sent " + sent + " energy");
		if(sent == 0) {
			return 0;
		}
		return extractUltraEnergy(sent, false);
	}

	protected void sendEnergyToConsumers() {
		Collection<Set<IEnergyStorage>> consumersSets = this.consumers.values();
		Collection<IEnergyStorage> consumers = new ArrayList<>();
		for(Set<IEnergyStorage> consumersSet : consumersSets) {
			consumers.addAll(consumersSet);
		}
		
		Map<IEnergyStorage, Double> mapWeights = new HashMap<>();

		//Calculate weights, used to simulate energy consumer priority
		double sentSum = 0;
		for(IEnergyStorage consumer : consumers) {
			double sent = 0;
			if(consumer instanceof IUltraEnergyStorage) {
				sent = ((IUltraEnergyStorage)consumer).receiveUltraEnergy(energyStored, true);
			}
			else {
				sent = consumer.receiveEnergy((int)Math.min(Integer.MAX_VALUE, energyStored), true);
			}
			mapWeights.put(consumer, sent);
			sentSum += sent;
		}
		if(sentSum == 0) {
			return;
		}
		double energyPerWeight = energyStored/sentSum;


		//Send weighted energy to consumers
		for(IEnergyStorage consumer : consumers) {
			double weight = mapWeights.get(consumer);
			double energyToSend = Math.floor(energyPerWeight * weight);
			sendEnergy(consumer, energyToSend);
		}
		//Send remaining energy to consumers (remaining from rounding)
		if(energyStored > 0) {
			for(IEnergyStorage consumer : consumers) {
				if(energyStored == 0) {
					break;
				}
				sendEnergy(consumer, energyStored);
			}
		}
	}

	double extractEnergy(IEnergyStorage producer, double maxExtract) {
		if(producer instanceof IUltraEnergyStorage) {
			return (int)extractEnergy((IUltraEnergyStorage)producer, maxExtract);
		}
		int maxExtractInt = (int)Math.min(Integer.MAX_VALUE, maxExtract);
		double received = producer.extractEnergy(maxExtractInt, false);
		if(received == 0) {
			return 0;
		}
		return receiveEnergy((int)received, false);
	}

	double extractEnergy(IUltraEnergyStorage producer, double maxExtract) {
		double received = producer.receiveUltraEnergy(maxExtract, false);
		System.out.println("Received " + received + " energy");
		if(received == 0) {
			return 0;
		}
		return extractUltraEnergy(received, false);
	}

	protected void extractEnergyFromProducers() {
		Collection<Set<IEnergyStorage>> producersSets = this.producers.values();
		Collection<IEnergyStorage> producers = new ArrayList<>();
		for(Set<IEnergyStorage> producersSet : producersSets) {
			producers.addAll(producersSet);
		}
		
		for(IEnergyStorage producer : producers) {
			double maxExtract = getMaxUltraEnergyStored() - getEnergyStored();
			extractEnergy(producer, maxExtract);
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
		if(maxEnergyStored == 0 || energyStored == maxEnergyStored) {
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
