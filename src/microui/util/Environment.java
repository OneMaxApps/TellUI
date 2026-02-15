package microui.util;

/**
 *  Environment class provides information about system
 */
public final class Environment {
	private static final boolean isAndroid;
	
	static {
		isAndroid = System.getProperty("java.vendor").contains("Android");
	}
	
	private Environment() {
		
	}
	
	/**
	 * Returns environment information about OS
	 * 
	 * @return true if environment is Android, false if isn't
	 */
	public static boolean isAndroid() {
		return isAndroid;
	}
	
}