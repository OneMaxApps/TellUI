package microui.util;

import java.util.Objects;

/**
 *  The Environment class provides information about the system.
 */
public final class Environment {
	private static boolean android;
	private static Mode forcedMode;
	
	static {
		android = System.getProperty("java.vendor").contains("Android") || System.getProperty("os.name").contains("Android");
	}
	
	private Environment() {
		
	}
	
	/**
	 * Returns environment information about OS
	 * 
	 * @return true if the environment is Android, false otherwise
	 */
	public static boolean isAndroid() {
		return forcedMode == null ? android : forcedMode == Mode.ANDROID;
	}
	
	/**
	 * Returns environment information about OS
	 * 
	 * @return true if the environment is Desktop, false otherwise
	 */
	public static boolean isDesktop() {
		return forcedMode == null ? !android : forcedMode == Mode.DESKTOP;
	}
	
	/**
	 * Handles the initialization of the environment modes.
	 * Can only be called once
	 * @param forcedMode the current mode for environment
	 * @throws NullPointerException if forcedMode == null
	 * @throws IllegalStateException if Environment.forcedMode != null
	 */
	public static void setForcedMode(Mode forcedMode) {
		if (Environment.forcedMode != null) {
			throw new IllegalStateException("Environment mode already initialized");
		}
		
		Environment.forcedMode = Objects.requireNonNull(forcedMode,"forcedMode");
	}
	
	/**
	 * Mode is an enumeration representing the available environment modes.
	 */
	public static enum Mode {
		/**
		 * Desktop mode is used when the user works in Windows, Linux or MacOS-like environments.
		 */
		DESKTOP,
		
		/**
		 * Android mode is the mode for Android devices.
		 * It changes visual effects, interaction reactions etc, etc.
		 */
		ANDROID;
	}
}