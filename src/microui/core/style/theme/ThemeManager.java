package microui.core.style.theme;

import static java.util.Objects.requireNonNull;

public final class ThemeManager {
	private static AbstractTheme currentTheme = new ThemeWhite();

	public static void setTheme(AbstractTheme theme) {
		currentTheme = requireNonNull(theme,"theme");
	}

	public static AbstractTheme getTheme() {
		return currentTheme;
	}
}