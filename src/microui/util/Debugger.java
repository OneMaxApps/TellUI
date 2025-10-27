package microui.util;

import static java.util.Objects.requireNonNull;

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
		Debugger.additionalInfo = requireNonNull(additionalInfo,"additionalInfo");
	}

	public static boolean isHotKeySwitchEnabled() {
		return isHotKeySwitchEnabled;
	}

	public static void setHotKeySwitchEnabled(boolean isHotKeySwitchEnabled) {
		Debugger.isHotKeySwitchEnabled = isHotKeySwitchEnabled;
	}
	
}