package paulojjj.solarenergy;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

	public static final Logger LOGGER = LogManager.getLogger(Main.MODID);
	
	private static Level level = Config.getInstance().getLogLevel();
	
	public static boolean canLog(Level level) {
		return Log.level.intLevel() >= level.intLevel();
	}
	
	public static final void debug(String message) {
		if(!canLog(Level.DEBUG)) {
			return;
		}
		LOGGER.debug(message);
	}
	
	public static final void info(String message) {
		if(!canLog(Level.INFO)) {
			return;
		}
		LOGGER.info(message);
	}
	
	public static final void warn(String message) {
		if(!canLog(Level.WARN)) {
			return;
		}
		LOGGER.warn(message);
	}
	
	public static final void error(String message) {
		if(!canLog(Level.ERROR)) {
			return;
		}
		LOGGER.error(message);
	}
}
