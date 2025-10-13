package microui.core.style.theme;

public final class ThemeManager {
	private static AbstractTheme currentTheme = new ThemeWhite();

	public static void setTheme(AbstractTheme theme) {
		if (theme == null) {
			throw new NullPointerException("theme cannot be null");
		}
		currentTheme = theme;
	}

	public static AbstractTheme getTheme() {
		return currentTheme;
	}
}