package paulojjj.solarenergy.recipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnergyAssemblerRecipe {
	
	private Item input;
	private ItemStack output;
	private double energyNeeded;
	
	public EnergyAssemblerRecipe(Item input, ItemStack output, double energyNeeded) {
		super();
		this.input = input;
		this.output = output;
		this.energyNeeded = energyNeeded;
	}

	public Item getInput() {
		return input;
	}

	public ItemStack getOutput() {
		return output;
	}

	public double getEnergyNeeded() {
		return energyNeeded;
	}
	
}
