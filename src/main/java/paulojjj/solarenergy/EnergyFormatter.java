package paulojjj.solarenergy;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EnergyFormatter {
	
	private static final NumberFormat format = new DecimalFormat("#0.00");
	private static final NumberFormat intFormat = new DecimalFormat("#0");
	
	private static final String[] suffixes = { "", "k", "M", "G", "T", "P", "E", "Z", "Y"  };
	
	protected static double log1000(double value) {
		return Math.log10(value)/3.0;
	}
	
	public static String format(double energy) {
		String unit = "FE";
		
		double exp = energy == 0 ? 0 : log1000(energy);
		String suffix = "";
		
		int expInt = (int)Math.floor(exp);
		if(exp < suffixes.length) {
			suffix = suffixes[expInt];
		}
		else {
			suffix = "x10^" + expInt * 3 + " ";
		}
		double div = Math.pow(1000, expInt);
		double value = energy / div;
		
		return (expInt == 0 ? intFormat.format(value) : format.format(value)) + " " + suffix + unit;
	}

}
