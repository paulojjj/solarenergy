package paulojjj.solarenergy.networks;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import paulojjj.solarenergy.IUltraEnergyStorage;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.blocks.SolarGenerator;
import paulojjj.solarenergy.proxy.CommonProxy;

public abstract class BaseNetwork<T extends TileEntity & INetworkMember> implements INetwork<T> {

	protected boolean valid = true;

	private Set<T> tiles = new HashSet<>();
	private Map<T, IEnergyStorage> consumers = new HashMap<>();

	protected World world;

	protected abstract Class<T> getTileClass();

	private double energyStored = 0;
	private double maxEnergyStored = 0;
	private boolean canReceive = false;
	private boolean canExtract = false;

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

	protected INetwork<T> init(Set<T> tiles, Map<T, IEnergyStorage> consumers) {
		world = tiles.iterator().next().getWorld();
		this.tiles.addAll(tiles);
		this.consumers.putAll(consumers);
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
					orphans.remove(nextOrphan);
				}
			}
			if(nextOrphan == null) {
				break;
			}
			Set<T> newNetworkTiles = scanConnected(nextOrphan);
			INetwork<T> newNetwork = split(newNetworkTiles);
			orphans.removeAll(newNetwork.getTiles());
		}

		//Check new added
		for(T tile : connected) {
			if(!tiles.contains(tile)) {
				addTile(tile);
			}
		}

		updateConsumers();

		if(tiles.isEmpty()) {
			destroy();
		}

		return;
	}

	protected Set<IEnergyStorage> getConsumers(TileEntity tileEntity) {
		BlockPos pos = tileEntity.getPos();
		Set<IEnergyStorage> consumers = new HashSet<>();
		for(EnumFacing facing : EnumFacing.values()) {
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if(tile != null && !tile.getClass().equals(tileEntity.getClass()) && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
				IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
				consumers.add(energyStorage);
			}
		}
		return consumers;
	}

	protected void updateConsumers() {
		consumers.clear();
		for(T tile : tiles) {
			Set<IEnergyStorage> tileConsumers = getConsumers(tile);
			for(IEnergyStorage tileConsumer : tileConsumers) {
				consumers.put(tile, tileConsumer);
			}
		}
	}

	protected void addTile(T tile) {
		if(tile == null || !canAdd(tile)) {
			return;
		}
		Main.logger.info("Adding tile at " + tile.getPos() + " to network " + this);
		tiles.add(tile);
		tile.setNetwork(this);
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

	@Override
	public void onBlockRemoved(T tile) {
		tiles.remove(tile);
		tile.setNetwork(null);
		if(tiles.isEmpty()) {
			destroy();
			return;
		}
		updateNetwork(tiles.iterator().next());
	}

	@Override
	public void onNeighborChanged(T source, BlockPos neighborPos) {
		consumers.remove(source);
		for(IEnergyStorage consumer : getConsumers(source)) {
			consumers.put(source, consumer);
		}
	}

	protected INetwork<T> split(Set<T> newNetworkTiles) {
		Main.logger.info("Splitting network " + this);
		try {
			Map<T, IEnergyStorage> newNetworkConsumers = new HashMap<>();
			for(T tile : newNetworkTiles) {
				newNetworkConsumers.put(tile, consumers.get(tile));
			}
			@SuppressWarnings("unchecked")
			BaseNetwork<T>  newNetwork =  (BaseNetwork<T>)this.getClass().newInstance().init(newNetworkTiles, newNetworkConsumers);
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
		Set<T> scanned = new HashSet<>();
		Set<T> connected = new HashSet<>();
		if(canAdd(initialTile)) {
			connected.add(initialTile);
		}
		scanned.add(initialTile);
		scanNeighbors(initialTile, connected, scanned);

		Main.logger.info("Scanning returned " + connected);
		return connected;
	}

	protected void scanNeighbors(T tile, Set<T> connected, Set<T> scanned) {
		if(canAdd(tile) && ! scanned.contains(tile)) {
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

	Set<T> getNeighbors(T tile) {
		Set<T> neighbors = new HashSet<>();
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos neighborPos = tile.getPos().offset(facing);
			T neighbor = as(getTileClass(), world.getTileEntity(neighborPos));
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

			energyInput = 0;
			energyOutput = 0;
			
			energyStored = tiles.stream().map(x -> x.getUltraEnergyStored()).collect(Collectors.summingDouble(x -> x));
			maxEnergyStored = tiles.stream().map(x -> x.getMaxUltraEnergyStored()).collect(Collectors.summingDouble(x -> x));
			canExtract = tiles.stream().anyMatch(x -> x.canExtract());
			canReceive = tiles.stream().anyMatch(x -> x.canReceive());

			sendEnergyToConsumers();
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
		Collection<IEnergyStorage> consumers = this.consumers.values();
		if(consumers.size() == 0) {
			return;
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


	@Override
	public String toString() {
		return super.toString() + "[size=" + tiles.size() + ", tiles=" + tiles + "]";
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
					energyOutput += extracted;
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
					energyInput += received;
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
