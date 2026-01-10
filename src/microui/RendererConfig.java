package microui;

import static java.util.Objects.requireNonNull;
import static microui.RendererConfig.Mode.FLEXIBLE;

// Status: STABLE - Do not modify
// Last Reviewed: 08.12.2025

/**
 * Provides setting for configuration of rendering mode
 */
public final class RendererConfig {
	private static Mode mode;
	
	static {
		setMode(FLEXIBLE);
	}
	
	private RendererConfig() {
		
	}
	
	/**
	 * Returns the current rendering mode for MicroUI components.
	 * <p>
	 * The mode determines how strictly MicroUI components validate and
	 * execute their internal rendering logic. This affects only the
	 * behavior of MicroUI components, not the overall rendering architecture.
	 * </p>
	 * 
	 * @return the current rendering mode (never {@code null})
	 * 
	 * @see #setMode(Mode)
	 * @see Mode
	 */
	public static Mode getMode() {
		return mode;
	}

	/**
	 * Sets the rendering mode for all MicroUI components.
	 * <p>
	 * <b>Note:</b> This affects only MicroUI components like {@code Button},
	 * {@code Slider}, {@code Panel}, etc.
	 * </p>
	 * 
	 * @param mode the new rendering mode for MicroUI components (cannot be {@code null})
	 * @throws NullPointerException if {@code mode} is {@code null}
	 * 
	 * @see #getMode()
	 * @see Mode
	 */
	public static void setMode(Mode mode) {
		RendererConfig.mode = requireNonNull(mode,"mode");
	}

	/**
	 * Rendering modes for MicroUI component behavior.
	 * <p>
	 * These modes define different rendering strategies.
	 * <b>internal to MicroUI components only</b>.
	 * </p>
	 */
	public static enum Mode {
		/**
		 * Flexible rendering mode (default).
		 * 
		 * <p>
		 * Allows manual rendering in the draw method without conflicting with automatic rendering.
		 * </p>
		 */
		FLEXIBLE,
		
		/**
		 * Strict rendering mode.
		 * <p>
		 * Prohibits the manual rendering call for components in the draw method.
		 * </p>
		 */
		STRICT;
	}
	
}