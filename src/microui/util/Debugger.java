package microui.util;

public final class Debugger {
	private static String additionalInfo = "";
	
	private static boolean isDebugModeEnabled,isHotKeySwitchEnabled;
	
	static {
		setHotKeySwitchEnabled(true);
	}
	
	private Debugger() {
		
	}
	
	public static boolean isDebugModeEnabled() {
		return isDebugModeEnabled;
	}

	public static void setDebugModeEnabled(boolean isDebugModeEnabled) {
		Debugger.isDebugModeEnabled = isDebugModeEnabled;
	}

	public static String getAdditionalInfo() {
		return additionalInfo;
	}

	public static void setAdditionalInfo(String additionalInfo) {
		if(additionalInfo == null) {
			throw new NullPointerException("AdditionalInfo cannot be null");
		}
		
		Debugger.additionalInfo = additionalInfo;
	}

	public static boolean isHotKeySwitchEnabled() {
		return isHotKeySwitchEnabled;
	}

	public static void setHotKeySwitchEnabled(boolean isHotKeySwitchEnabled) {
		Debugger.isHotKeySwitchEnabled = isHotKeySwitchEnabled;
	}
	
}