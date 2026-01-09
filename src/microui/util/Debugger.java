package microui.util;

import static java.util.Objects.requireNonNull;

/**
 * Debugging utility class for managing debug mode settings.
 * 
 * <p>Provides centralized control over debug mode with options for enabling/disabling,
 * hotkey switching, and storing additional debug information.</p>
 */
public final class Debugger {
    private static String additionalInfo = "";
    private static boolean enabled;
    private static boolean hotKeySwitchEnabled;
    
    static {
        setHotKeySwitchEnabled(false);
    }
    
    private Debugger() {
    }
    
    /**
     * Checks if debug mode is currently enabled.
     * 
     * @return true if debug mode is enabled, false otherwise
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables debug mode.
     * 
     * @param isDebugModeEnabled true to enable debug mode, false to disable
     */
    public static void setEnabled(boolean isDebugModeEnabled) {
        Debugger.enabled = isDebugModeEnabled;
    }

    /**
     * Gets the additional debug information.
     * 
     * @return the additional debug information string
     */
    public static String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the additional debug information.
     * 
     * @param additionalInfo the debug information to set, cannot be null
     * @throws NullPointerException if additionalInfo is null
     */
    public static void setAdditionalInfo(String additionalInfo) {
        Debugger.additionalInfo = requireNonNull(additionalInfo, "additionalInfo");
    }

    /**
     * Checks if hotkey switching of debug mode is enabled.
     * 
     * @return true if hotkey switching is enabled, false otherwise
     */
    public static boolean isHotKeySwitchEnabled() {
        return hotKeySwitchEnabled;
    }

    /**
     * Enables or disables hotkey switching of debug mode.
     * 
     * <p>When enabled, allows debug mode to be toggled via a hotkey (typically F3).
     * When disabled, debug mode can only be changed programmatically.</p>
     * 
     * @param isHotKeySwitchEnabled true to enable hotkey switching, false to disable
     */
    public static void setHotKeySwitchEnabled(boolean isHotKeySwitchEnabled) {
        Debugger.hotKeySwitchEnabled = isHotKeySwitchEnabled;
    }
}