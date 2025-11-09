package microui.util;

import static java.util.Objects.requireNonNull;

public final class Debugger {
	private static String additionalInfo = "";
	
	private static boolean enabled,hotKeySwitchEnabled;
	
	static {
		setHotKeySwitchEnabled(true);
	}
	
	private Debugger() {
		
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean isDebugModeEnabled) {
		Debugger.enabled = isDebugModeEnabled;
	}

	public static String getAdditionalInfo() {
		return additionalInfo;
	}

	public static void setAdditionalInfo(String additionalInfo) {
		Debugger.additionalInfo = requireNonNull(additionalInfo,"additionalInfo");
	}

	public static boolean isHotKeySwitchEnabled() {
		return hotKeySwitchEnabled;
	}

	public static void setHotKeySwitchEnabled(boolean isHotKeySwitchEnabled) {
		Debugger.hotKeySwitchEnabled = isHotKeySwitchEnabled;
	}
	
}