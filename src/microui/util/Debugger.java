package microui.util;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;

import microui.MicroUI;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import processing.core.PApplet;

/**
 * Debugging utility class for managing debug mode settings.
 * 
 * <p>
 * Provides centralized control over debug mode with options for
 * enabling/disabling, hotkey switching, and storing additional debug
 * information.
 * </p>
 */
public final class Debugger {
	private static String additionalInfo = "";
	private static boolean enabled;
	private static boolean hotKeyEnabled;

	private Debugger() {
	}
	
	/**
	 * Drawing text about frame rate and additional information if that typed
	 */
	public static void draw() {
		if (!enabled) {
			return;
		}
		
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
	 * Checks if hotkey switching of debug mode is enabled.
	 * 
	 * @return true if hotkey switching is enabled, false otherwise
	 */
	public static boolean isHotKeyEnabled() {
		return hotKeyEnabled;
	}

	/**
	 * Enables or disables hotkey switching of debug mode.
	 * 
	 * <p>
	 * When enabled, allows debug mode to be toggled via a hotkey (ALT).
	 * When disabled, debug mode can only be changed programmatically.
	 * </p>
	 * 
	 * @param hotKeyEnabled true to enable hotkey switching, false to
	 *                              disable
	 */
	public static void setHotKeyEnabled(boolean hotKeyEnabled) {
		Debugger.hotKeyEnabled = hotKeyEnabled;
	}
	
	/**
	 * TextConfig a class for settings for text drawing
	 */
	public static final class TextConfig {
		/**
		 * Color for text
		 */
		public static AbstractColor color;
		
		/**
		 * Position x for text
		 */
		public static int x;
		
		/**
		 * Position y for text
		 */
		public static int y;
		
		/**
		 * Size for text
		 */
		public static int size;
		
		static {
			color = Color.WHITE;
			
			final PApplet p =  MicroUI.getContext();
			
			size = (int) (Math.max(4,p.height* .08f));
			
			x = size/4;
			y = size;
		}
		
		/**
		 * return the color of text
		 * @return current color of text
		 */
		public static AbstractColor getColor() {
			return color;
		}
		
		/**
		 * setter for color of text
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
		 * setter for text size
		 * text size must be greater than 2
		 * @param size for text size
		 */
		public static void setSize(int size) {
			if (size <= 2) {
				throw new IllegalArgumentException("Text size must be greater that 2");
			}
			TextConfig.size = size;
		}
		
	}
}