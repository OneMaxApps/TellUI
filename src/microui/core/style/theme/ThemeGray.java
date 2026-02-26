package microui.core.style.theme;

import microui.core.style.AbstractColor;
import microui.core.style.Color;

/**
 * Gray theme implementation with a monochromatic color scheme. Provides a
 * clean, professional aesthetic using various shades of gray with subtle color
 * accents for interactive elements.
 * <p>
 * This theme uses a restrained gray palette with carefully selected luminance
 * values to create good contrast and readability while maintaining a neutral,
 * professional appearance.
 * </p>
 * 
 * @see AbstractTheme
 * @see Color
 */
public class ThemeGray extends AbstractTheme {

	/**
	 * Returns a medium gray for backgrounds.
	 * 
	 * @return medium gray (124 luminance)
	 */
	@Override
	public AbstractColor getBackgroundColor() {
		return new Color(100);
	}

	/**
	 * Returns a very dark gray for text views.
	 * 
	 * @return very dark gray (12 luminance)
	 */
	@Override
	public AbstractColor getTextViewColor() {
		return new Color(12);
	}

	/**
	 * Returns a dark gray for button text.
	 * 
	 * @return dark gray (32 luminance)
	 */
	@Override
	public AbstractColor getButtonTextColor() {
		return new Color(32);
	}

	/**
	 * Returns a medium gray for strokes and borders.
	 * 
	 * @return medium gray (128 luminance)
	 */
	@Override
	public AbstractColor getStrokeColor() {
		return new Color(128);
	}

	/**
	 * Returns a medium gray with medium opacity for hover effects.
	 * 
	 * @return medium gray with medium opacity (132 luminance, 128 alpha)
	 */
	@Override
	public AbstractColor getHoverColor() {
		return new Color(132, 128);
	}

	/**
	 * Returns a light gray with medium opacity for ripple animations.
	 * 
	 * @return light gray with medium opacity (200 luminance, 128 alpha)
	 */
	@Override
	public AbstractColor getRipplesColor() {
		return new Color(200, 128);
	}

	/**
	 * Returns a dark gray for primary accents.
	 * 
	 * @return dark gray (64 luminance)
	 */
	@Override
	public AbstractColor getPrimaryColor() {
		return new Color(64);
	}

	/**
	 * Returns pure black for editable text.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getEditableTextColor() {
		return new Color(0);
	}

	/**
	 * Returns a semi-transparent cyan for selection highlighting.
	 * 
	 * @return cyan with low opacity (RGB: 0,200,255, alpha: 32)
	 */
	@Override
	public AbstractColor getSelectColor() {
		return new Color(0, 200, 255, 32);
	}

	/**
	 * Returns pure black for text cursor/caret.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getCursorColor() {
		return new Color(0);
	}

	/**
	 * Returns a very light gray for editable text field backgrounds.
	 * 
	 * @return very light gray (232 luminance)
	 */
	@Override
	public AbstractColor getEditableBackgroundColor() {
		return new Color(232);
	}

	/**
	 * Returns a light gray for tooltip backgrounds.
	 * 
	 * @return light gray (200 luminance)
	 */
	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new Color(200);
	}

	/**
	 * Returns a dark gray for tooltip text.
	 * 
	 * @return dark gray (32 luminance)
	 */
	@Override
	public AbstractColor getTooltipTextColor() {
		return new Color(32);
	}

	/**
	 * Returns a dark gray for menu button item backgrounds.
	 * 
	 * @return dark gray (32 luminance)
	 */
	@Override
	public AbstractColor getMenuButtonItemColor() {
		return Color.GRAY_32L;
	}

	/**
	 * Returns pure white for menu button item text.
	 * 
	 * @return white color
	 */
	@Override
	public AbstractColor getMenuButtonItemTextColor() {
		return Color.WHITE;
	}

}