package microui;

import processing.core.PApplet;

// Status: STABLE - Do not modify
// Last Reviewed: 12.09.2025
public final class MicroUI {
	private static PApplet ctx;

	private static final int MAJOR = 2;
	private static final int MINOR = 0;
	private static final int PATCH = 0;

	private static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;

	private static boolean flexibleRenderModeEnabled;

	private MicroUI() {
	}

	public static final void setContext(PApplet context) {
		if (context != null && MicroUI.ctx == null) {
			MicroUI.ctx = context;
		}
	}

	public static PApplet getContext() {
		return ctx;
	}

	public static String getVersion() {
		return VERSION;
	}

	public static boolean isFlexibleRenderModeEnabled() {
		return flexibleRenderModeEnabled;
	}

	public static void setFlexibleRenderModeEnabled(boolean flexibleRenderModeEnabled) {
		MicroUI.flexibleRenderModeEnabled = flexibleRenderModeEnabled;
	}

}