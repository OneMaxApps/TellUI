package microui.util;

public final class Environment {
	private static final boolean isAndroid;
	
	static {
		isAndroid = System.getProperty("java.vendor").contains("Android");
	}
	
	private Environment() {
		
	}
	
	public static boolean isAndroid() {
		return isAndroid;
	}
	
}