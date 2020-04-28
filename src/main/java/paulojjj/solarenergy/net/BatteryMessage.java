package paulojjj.solarenergy.net;

public class BatteryMessage {
	private double energy;
	private double capacity;
	private double in;
	private double out;
	
	public BatteryMessage() {
	}
	
	public BatteryMessage(double energy, double capacity, double in, double out) {
		super();
		this.energy = energy;
		this.capacity = capacity;
		this.in = in;
		this.out = out;
	}

	public double getEnergy() {
		return energy;
	}
	public double getCapacity() {
		return capacity;
	}
	public double getIn() {
		return in;
	}
	public double getOut() {
		return out;
	}
}
