package microui;

import static java.util.Objects.requireNonNull;

import processing.core.PApplet;

// Status: STABLE - Do not modify
// Last Reviewed: 01.03.2026

/**
 * Core initialization and metadata class for the MicroUI library.
 * <p>
 * This singleton-style class provides the foundational setup for MicroUI
 * components by establishing a connection to the Processing sketch context.
 * <b>Must be initialized</b> before using any MicroUI functionality.
 * </p>
 * 
 * @see #setContext(PApplet)
 * @see #getContext()
 */
public final class MicroUI {
	private static PApplet ctx;

	private static final int MAJOR = 1;
	private static final int MINOR = 0;
	private static final int PATCH = 0;

	private static final String VERSION = MAJOR + "." + MINOR + "." + PATCH;

	private MicroUI() {
	}

	/**
	 * Initializes the MicroUI library with the current Processing sketch context.
	 * <p>
	 * This method <b>must be called once</b> during sketch setup before any MicroUI
	 * components are used. Subsequent calls are silently ignored (idempotent
	 * operation). The context is typically the main sketch instance ({@code this}).
	 * </p>
	 * 
	 * @param context the main PApplet instance (typically {@code this} from the
	 *                sketch)
	 * @throws NullPointerException if {@code context} is {@code null}
	 * 
	 * @see #getContext()
	 */
	public static void setContext(PApplet context) {
		requireNonNull(context, "context");

		if (MicroUI.ctx == null) {
			MicroUI.ctx = context;
		}
	}

	/**
	 * Returns the Processing sketch context for MicroUI.
	 * <p>
	 * This method provides access to the initialized PApplet instance, which is
	 * required by MicroUI components for rendering and Processing API access.
	 * </p>
	 * 
	 * @return the initialized PApplet instance
	 * @throws IllegalStateException if {@link #setContext(PApplet)} has not been
	 *                               called
	 * 
	 * @see #setContext(PApplet)
	 */
	public static PApplet getContext() {
		if (ctx == null) {
			throw new IllegalStateException("Context (PApplet) for MicroUI is not initialized");
		}

		return ctx;
	}

	/**
	 * Returns the current version of the MicroUI library.
	 * 
	 * @return the version string in format "MAJOR.MINOR.PATCH"
	 */
	public static String getVersion() {
		return VERSION;
	}

}