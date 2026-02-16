package microui.util;

import java.util.Objects;

/**
 *  Environment class provides information about system
 */
public final class Environment {
	private static boolean isAndroid;
	private static Mode forcedMode;
	
	static {
		isAndroid = System.getProperty("java.vendor").contains("Android") || System.getProperty("os.name").contains("Android");
	}
	
	private Environment() {
		
	}
	
	/**
	 * Returns environment information about OS
	 * 
	 * @return true if environment is Android, false if isn't
	 */
	public static boolean isAndroid() {
		return forcedMode == null ? isAndroid : forcedMode == Mode.ANDROID;
	}
	
	/**
	 * Returns environment information about OS
	 * 
	 * @return true if environment is Desktop, false if isn't
	 */
	public static boolean isDesktop() {
		return forcedMode == null ? !isAndroid : forcedMode == Mode.DESKTOP;
	}
	
	/**
	 * Provides handle initialization for mode of environment.
	 * Can only be called once
	 * @param forcedMode the current mode for environment
	 */
	public static void setForcedMode(Mode forcedMode) {
		if (Environment.forcedMode != null) {
			throw new IllegalStateException("Environment mode already initialized");
		}
		
		Environment.forcedMode = Objects.requireNonNull(forcedMode,"forcedMode");
	}
	
	/**
	 * Mode is a simple enum for storing modes
	 */
	public static enum Mode {
		/**
		 * Desktop mode is mode when user work in Windows, Linux or MacOS like environments
		 */
		DESKTOP,
		
		/**
		 * Android mode is mode for android devices.
		 * It's change effects, interaction reaction etc.
		 */
		ANDROID;
	}
}