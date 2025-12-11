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
	
	/**
	 * Private constructor to prevent instantiation.
	 * <p>
	 * This is a configuration utility class with only static methods.
	 * </p>
	 */
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
	 * This setting influences:
	 * <ul>
	 *   <li>Validation of component rendering parameters</li>
	 *   <li>Rendering performance optimizations</li>
	 *   <li>Error handling during component rendering</li>
	 *   <li>Automatic adjustments for edge cases</li>
	 * </ul>
	 * <p>
	 * <b>Note:</b> This affects only MicroUI components like {@code Button},
	 * {@code Slider}, {@code Panel}, etc. Manual Processing drawing
	 * (e.g., {@code rect()}, {@code ellipse()}) is unaffected.
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
	 * These modes define different rendering strategies and validation levels
	 * <b>internal to MicroUI components only</b>.
	 * </p>
	 */
	public static enum Mode {
		/**
		 * Flexible rendering mode (default).
		 * <p>
		 * MicroUI components prioritize robustness and graceful degradation:
		 * - Automatic adjustment of rendering when parameters are borderline
		 * - Silent handling of minor rendering issues
		 * - Performance optimizations that may sacrifice precision
		 * - Suitable for rapid development and prototypes
		 * </p>
		 * <p>
		 * <b>Example behavior for a {@code Button} component:</b>
		 * <ul>
		 *   <li>If text doesn't fit, automatically shrinks font or adds ellipsis</li>
		 *   <li>If position is slightly outside bounds, adjusts to stay visible</li>
		 *   <li>Uses faster but less precise rendering techniques</li>
		 * </ul>
		 * </p>
		 */
		FLEXIBLE,
		
		/**
		 * Strict rendering mode.
		 * <p>
		 * MicroUI components enforce exact rendering specifications:
		 * - Validates all rendering parameters before drawing
		 * - Throws exceptions for invalid rendering states
		 * - Uses precise but potentially slower rendering techniques
		 * - Ideal for production applications and pixel-perfect UI
		 * </p>
		 * <p>
		 * <b>Example behavior for a {@code Button} component:</b>
		 * <ul>
		 *   <li>Validates text fits within bounds before rendering</li>
		 *   <li>Throws exception for invalid colors or dimensions</li>
		 *   <li>Uses exact color matching and anti-aliasing</li>
		 *   <li>Performs bounds checking for all rendering operations</li>
		 * </ul>
		 * </p>
		 */
		STRICT;
	}
	
}