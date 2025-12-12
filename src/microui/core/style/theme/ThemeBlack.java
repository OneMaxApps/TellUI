package microui.core.style.theme;

import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.GradientColor;
import microui.core.style.GradientLoopColor;

/**
 * Dark theme implementation with black/dark gray color scheme and gradient effects.
 * Provides a modern dark theme with subtle animations and visual effects for MicroUI components.
 * <p>
 * This theme uses a predominantly dark color palette with accent colors and gradient
 * effects for interactive elements. It includes animated tooltips and gradient effects
 * for enhanced visual feedback.
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see AbstractTheme
 * @see Color
 * @see GradientColor
 * @see GradientLoopColor
 */
public class ThemeBlack extends AbstractTheme {

	/**
	 * Returns the dark background color for containers.
	 * 
	 * @return dark gray color (32 luminance)
	 */
	@Override
	public AbstractColor getBackgroundColor() {
		return new Color(32);
	}

	/**
	 * Returns the light gray color for non-editable text.
	 * 
	 * @return light gray color (200 luminance)
	 */
	@Override
	public AbstractColor getTextViewColor() {
		return Color.GRAY_200L;
	}

	/**
	 * Returns the medium gray color for button text.
	 * 
	 * @return medium gray color (164 luminance)
	 */
	@Override
	public AbstractColor getButtonTextColor() {
		return new Color(164);
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
	 * Returns a semi-transparent light gray for hover effects.
	 * 
	 * @return light gray with low opacity (200 luminance, 24 alpha)
	 */
	@Override
	public AbstractColor getHoverColor() {
		return new Color(200, 24);
	}

	/**
	 * Returns a very transparent gray for ripple animations.
	 * 
	 * @return gray with very low opacity (100 luminance, 10 alpha)
	 */
	@Override
	public AbstractColor getRipplesColor() {
		return new Color(100, 10);
	}

	/**
	 * Returns a semi-transparent light gray for primary accents.
	 * 
	 * @return light gray with medium opacity (232 luminance, 128 alpha)
	 */
	@Override
	public AbstractColor getPrimaryColor() {
		return new Color(232, 128);
	}

	/**
	 * Returns pure white for editable text.
	 * 
	 * @return white color
	 */
	@Override
	public AbstractColor getEditableTextColor() {
		return Color.WHITE;
	}

	/**
	 * Returns a semi-transparent dark blue for selection highlighting.
	 * 
	 * @return dark blue with low opacity (RGB: 0,0,154, alpha: 32)
	 */
	@Override
	public AbstractColor getSelectColor() {
		return new Color(0, 0, 154, 32);
	}

	/**
	 * Returns a near-white color for text cursor/caret.
	 * 
	 * @return very light gray (255 luminance, 232 alpha)
	 */
	@Override
	public AbstractColor getCursorColor() {
		return new Color(255, 232);
	}

	/**
	 * Returns medium gray for editable text field backgrounds.
	 * 
	 * @return medium gray (128 luminance)
	 */
	@Override
	public AbstractColor getEditableBackgroundColor() {
		return Color.GRAY_128L;
	}

	/**
	 * Returns an animated gradient for tooltip backgrounds.
	 * Transitions from transparent to semi-transparent black.
	 * 
	 * @return gradient from transparent to black with animation
	 */
	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new GradientColor(Color.TRANSPARENT, new Color(0, 200), () -> true);
	}

	/**
	 * Returns a complex animated gradient for tooltip text.
	 * Features nested gradients with looping animation effects.
	 * 
	 * @return multi-layer gradient with looping animation
	 */
	@Override
	public AbstractColor getTooltipTextColor() {
		return new GradientColor(Color.TRANSPARENT, new GradientLoopColor(Color.WHITE, new Color(255, 154)),
				() -> true);
	}

	/**
	 * Returns pure black for menu button item backgrounds.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getMenuButtonItemColor() {
		return Color.BLACK;
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