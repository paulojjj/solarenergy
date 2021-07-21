package paulojjj.solarenergy;

public enum Tier {
	
		BASIC, REGULAR, INTERMEDIATE, ADVANCED, ELITE, ULTIMATE,
		BASIC_DENSE, REGULAR_DENSE, INTERMEDIATE_DENSE, ADVANCED_DENSE, ELITE_DENSE, ULTIMATE_DENSE;
		
		public boolean isDense() {
			return ordinal() > 5;
		}

}
