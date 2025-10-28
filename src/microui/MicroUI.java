package microui;

import static java.util.Objects.requireNonNull;

import processing.core.PApplet;

// Status: STABLE - Do not modify
// Last Reviewed: 28.10.2025

public final class MicroUI {
	private static PApplet ctx;

	private static final int MAJOR = 2;
	private static final int MINOR = 0;
	private static final int PATCH = 0;

	private static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;

	private MicroUI() {
	}

	public static final void setContext(PApplet context) {
		requireNonNull(context,"context");
		
		if (MicroUI.ctx == null) {
			MicroUI.ctx = context;
		}
	}

	public static PApplet getContext() {
		if(ctx == null) {
			throw new IllegalStateException("Context (PApplet) for MicroUI is not initialized");
		}
		
		return ctx;
	}

	public static String getVersion() {
		return VERSION;
	}

}