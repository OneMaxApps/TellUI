package microui.core.style.theme;

import static java.util.Objects.requireNonNull;

/**
 * Manager for handling theme selection and retrieval in the MicroUI framework.
 * Provides a centralized way to set and get the current theme used by all UI components.
 * <p>
 * The ThemeManager follows the singleton pattern for theme management, allowing
 * global theme changes that affect all components. By default, it uses ThemeWhite
 * as the initial theme.
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see AbstractTheme
 * @see ThemeWhite
 */
public final class ThemeManager {
	/** The currently active theme, initialized to ThemeWhite by default. */
	private static AbstractTheme currentTheme = new ThemeWhite();

	/**
	 * Sets the current theme for all UI components.
	 * 
	 * @param theme the theme to set as current (cannot be null)
	 * @throws NullPointerException if theme is null
	 */
	public static void setTheme(AbstractTheme theme) {
		currentTheme = requireNonNull(theme,"theme");
	}

	/**
	 * Returns the current theme being used by all UI components.
	 * 
	 * @return the current theme
	 */
	public static AbstractTheme getTheme() {
		return currentTheme;
	}
}