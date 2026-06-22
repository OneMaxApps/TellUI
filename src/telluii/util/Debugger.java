package telluii.util;

import static java.util.Objects.requireNonNull;
import static tellui.TellUI.getContext;

import processing.core.PApplet;
import tellui.TellUI;
import tellui.core.style.AbstractColor;
import tellui.core.style.Color;

/**
 * Debugging utility class for managing debug mode settings.
 * 
 * <p>
 * Provides centralized control over debug mode with options for
 * enabling/disabling, hot-key switching, and storing additional debug
 * information.
 * </p>
 */
public final class Debugger {
	private static String additionalInfo = "";
	private static boolean enabled;
	private static boolean hotKeyEnabled;
	private static boolean showLayoutBoundsEnabled,
						   showMarginBoundsEnabled,
						   showPaddingBoundsEnabled,
						   showContentBoundsEnabled,
						   showUpdateBoundsEnabled,
						   showFpsEnabled;
	
	static {
		showAllLayers();
	}

	private Debugger() {
	}

	/**
	 * Checks whether layout bounds visualization is enabled.
	 *
	 * @return true if enabled, false otherwise.
	 */
	public static boolean isShowLayoutBoundsEnabled() {
		return showLayoutBoundsEnabled;
	}

	/**
	 * Enables or disables layout bounds visualization.
	 *
	 * @param showLayoutBoundsEnabled true to enable, false to disable.
	 */
	public static void setShowLayoutBoundsEnabled(boolean showLayoutBoundsEnabled) {
		Debugger.showLayoutBoundsEnabled = showLayoutBoundsEnabled;
	}

	/**
	 * Checks whether margin bounds visualization is enabled.
	 *
	 * @return true if enabled, false otherwise.
	 */
	public static boolean isShowMarginBoundsEnabled() {
		return showMarginBoundsEnabled;
	}

	/**
	 * Enables or disables margin bounds visualization.
	 *
	 * @param showMarginBoundsEnabled true to enable, false to disable.
	 */
	public static void setShowMarginBoundsEnabled(boolean showMarginBoundsEnabled) {
		Debugger.showMarginBoundsEnabled = showMarginBoundsEnabled;
	}

	/**
	 * Checks whether padding bounds visualization is enabled.
	 *
	 * @return true if enabled, false otherwise.
	 */
	public static boolean isShowPaddingBoundsEnabled() {
		return showPaddingBoundsEnabled;
	}

	/**
	 * Enables or disables padding bounds visualization.
	 *
	 * @param showPaddingBoundsEnabled true to enable, false to disable.
	 */
	public static void setShowPaddingBoundsEnabled(boolean showPaddingBoundsEnabled) {
		Debugger.showPaddingBoundsEnabled = showPaddingBoundsEnabled;
	}

	/**
	 * Checks whether content bounds visualization is enabled.
	 *
	 * @return true if enabled, false otherwise.
	 */
	public static boolean isShowContentBoundsEnabled() {
		return showContentBoundsEnabled;
	}

	/**
	 * Enables or disables content bounds visualization.
	 *
	 * @param showContentBoundsEnabled true to enable, false to disable.
	 */
	public static void setShowContentBoundsEnabled(boolean showContentBoundsEnabled) {
		Debugger.showContentBoundsEnabled = showContentBoundsEnabled;
	}

	/**
	 * Checks whether update bounds visualization is enabled.
	 *
	 * @return true if enabled, false otherwise.
	 */
	public static boolean isShowUpdateBoundsEnabled() {
		return showUpdateBoundsEnabled;
	}

	/**
	 * Enables or disables update bounds visualization.
	 *
	 * @param showUpdateBoundsEnabled true to enable, false to disable.
	 */
	public static void setShowUpdateBoundsEnabled(boolean showUpdateBoundsEnabled) {
		Debugger.showUpdateBoundsEnabled = showUpdateBoundsEnabled;
	}
	
	/**
	 * Checks whether FPS display is enabled.
	 *
	 * @return true if enabled, false otherwise.
	 */
	public static boolean isShowFpsEnabled() {
		return showFpsEnabled;
	}

	/**
	 * Enables or disables FPS display.
	 *
	 * @param showFpsEnabled true to enable, false to disable.
	 */
	public static void setShowFpsEnabled(boolean showFpsEnabled) {
		Debugger.showFpsEnabled = showFpsEnabled;
	}

	/**
	 * Enables all debug visualization layers.
	 */
	public static void showAllLayers() {
		showLayoutBoundsEnabled = 
		showMarginBoundsEnabled = 
		showPaddingBoundsEnabled = 
		showContentBoundsEnabled = 
		showUpdateBoundsEnabled =
		showFpsEnabled = true;
	}
	
	/**
	 * Disables all debug visualization layers.
	 */
	public static void hideAllLayers() {
		showLayoutBoundsEnabled = 
		showMarginBoundsEnabled = 
		showPaddingBoundsEnabled = 
		showContentBoundsEnabled = 
		showUpdateBoundsEnabled = 
		showFpsEnabled = false;
	}

	/**
	 * Draws text displaying the frame rate and any additional typed information.
	 */
	public static void draw() {
		if (!enabled) {
			return;
		}
		
		if (isShowFpsEnabled()) {
			fpsOnDraw();
		}
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
	 * @param enabled true to enable debug mode, false to disable
	 */
	public static void setEnabled(boolean enabled) {
		Debugger.enabled = enabled;
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
	 * Checks if hot-key switching of debug mode is enabled.
	 * 
	 * @return true if hot-key switching is enabled, false otherwise
	 */
	public static boolean isHotKeyEnabled() {
		return hotKeyEnabled;
	}

	/**
	 * Enables or disables hot-key switching of debug mode.
	 * 
	 * <p>
	 * When enabled, allows debug mode to be toggled via a hot-key (ALT).
	 * When disabled, debug mode can only be changed programmatically.
	 * </p>
	 * 
	 * @param hotKeyEnabled true to enable hot-key switching, false to
	 *                              disable
	 */
	public static void setHotKeyEnabled(boolean hotKeyEnabled) {
		Debugger.hotKeyEnabled = hotKeyEnabled;
	}
	
	private static void fpsOnDraw() {
		final String text = "fps: " + (int) getContext().frameRate + "\n" + getAdditionalInfo();
		final AbstractColor c = TextConfig.getColor();
		final int x = TextConfig.getX();
		final int y = TextConfig.getY();
		final int textSize = TextConfig.getSize();
		
		getContext().push();
		c.apply();
		getContext().textSize(textSize);
		getContext().text(text, x, y);
		getContext().pop();
	}
	
	/**
	 * TextConfig is a class containing settings for text drawing.
	 */
	public static final class TextConfig {
		private static AbstractColor color;
		private static int x;
		private static int y;
		private static int size;
		
		static {
			color = Color.WHITE;
			
			final PApplet p =  TellUI.getContext();
			
			size = (int) (Math.max(4,p.height* .08f));
			
			x = size/4;
			y = size;
		}
		
		/**
		 * Returns the text color.
		 * 
		 * @return current color of text
		 */
		public static AbstractColor getColor() {
			return color;
		}
		
		/**
		 * Sets the text color.
		 * 
		 * @param color for text
		 */
		public static void setColor(AbstractColor color) {
			TextConfig.color = requireNonNull(color,"color");
		}
		
		/**
		 * coordinate X of text
		 * @return coordinate X of text
		 */
		public static int getX() {
			return x;
		}
		
		/**
		 * setter for coordinate X of text
		 * @param x coordinate X of text
		 */
		public static void setX(int x) {
			TextConfig.x = x;
		}
		
		/**
		 * coordinate Y of text
		 * @return coordinate Y of text
		 */
		public static int getY() {
			return y;
		}
		
		/**
		 * setter for coordinate Y of text
		 * @param y coordinate Y of text
		 */
		public static void setY(int y) {
			TextConfig.y = y;
		}
		
		/**
		 * @return text size
		 */
		public static int getSize() {
			return size;
		}
		
		/**
		 * Setter for text size.
		 * 
		 * (Text size must be greater than 1)
		 * 
		 * @param size for text size
		 * @throws IllegalArgumentException if size is lower or equal to 1
		 */
		public static void setSize(int size) {
			if (size <= 1) {
				throw new IllegalArgumentException("Text size must be greater than 1");
			}
			TextConfig.size = size;
		}
		
	}
}