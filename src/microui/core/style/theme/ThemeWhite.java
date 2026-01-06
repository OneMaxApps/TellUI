package microui.core.style.theme;

import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.core.style.LerpedLoopColor;

/**
 * Light theme implementation with a white/gray color scheme and gradient effects.
 * Provides a clean, modern light theme with subtle animations and good contrast
 * for readability.
 * <p>
 * This theme uses a predominantly light color palette with dark text and
 * animated gradient effects for interactive elements. It is designed for
 * good readability and a modern aesthetic.
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see AbstractTheme
 * @see Color
 * @see LerpedColor
 * @see LerpedLoopColor
 */
public class ThemeWhite extends AbstractTheme {

	/**
	 * Returns a very light gray for backgrounds.
	 * 
	 * @return very light gray (232 luminance)
	 */
	@Override
	public AbstractColor getBackgroundColor() {
		return Color.GRAY_232L;
	}

	/**
	 * Returns pure black for non-editable text.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getTextViewColor() {
		return Color.BLACK;
	}

	/**
	 * Returns pure black for button text.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getButtonTextColor() {
		return Color.BLACK;
	}

	/**
	 * Returns pure black for strokes and borders.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getStrokeColor() {
		return Color.BLACK;
	}

	/**
	 * Returns a semi-transparent light blue for hover effects.
	 * 
	 * @return light blue with low opacity (RGB: 100,200,255, alpha: 24)
	 */
	@Override
	public AbstractColor getHoverColor() {
		return new Color(100, 200, 255, 24);
	}

	/**
	 * Returns pure white for ripple animations.
	 * 
	 * @return white color
	 */
	@Override
	public AbstractColor getRipplesColor() {
		return Color.WHITE;
	}

	/**
	 * Returns a medium gray for primary accents.
	 * 
	 * @return medium gray (128 luminance)
	 */
	@Override
	public AbstractColor getPrimaryColor() {
		return Color.GRAY_128L;
	}

	/**
	 * Returns pure black for editable text.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getEditableTextColor() {
		return Color.BLACK;
	}

	/**
	 * Returns a semi-transparent blue for selection highlighting.
	 * 
	 * @return blue with low opacity (RGB: 0,0,200, alpha: 32)
	 */
	@Override
	public AbstractColor getSelectColor() {
		return new Color(0, 0, 200, 32);
	}

	/**
	 * Returns a near-black color for text cursor/caret.
	 * 
	 * @return near-black with high opacity (0 luminance, 232 alpha)
	 */
	@Override
	public AbstractColor getCursorColor() {
		return new Color(0, 232);
	}

	/**
	 * Returns pure white for editable text field backgrounds.
	 * 
	 * @return white color
	 */
	@Override
	public AbstractColor getEditableBackgroundColor() {
		return Color.WHITE;
	}

	/**
	 * Returns an animated gradient for tooltip backgrounds.
	 * Transitions from transparent to very light gray.
	 * 
	 * @return gradient from transparent to very light gray with animation
	 */
	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new LerpedColor(new Color(232, 0), Color.GRAY_232L, () -> true);

	}

	/**
	 * Returns a complex animated gradient for tooltip text.
	 * Features nested gradients with looping animation effects.
	 * 
	 * @return multi-layer gradient with looping animation
	 */
	@Override
	public AbstractColor getTooltipTextColor() {
		return new LerpedColor(Color.TRANSPARENT, new LerpedLoopColor(Color.BLACK, new Color(0, 154)), () -> true);
	}
	
	/**
	 * Returns a very light gray for menu button item backgrounds.
	 * 
	 * @return very light gray (232 luminance)
	 */
	@Override
	public AbstractColor getMenuButtonItemColor() {
		return Color.GRAY_232L;
	}

	/**
	 * Returns pure black for menu button item text.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getMenuButtonItemTextColor() {
		return Color.BLACK;
	}
}