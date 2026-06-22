package tellui.core.style.theme;

import tellui.core.style.AbstractColor;
import tellui.core.style.Color;
import tellui.core.style.LerpedLoopColor;

/**
 * Glass/transparent theme implementation with subtle animations and translucent
 * effects. Provides a modern, semi-transparent aesthetic with animated lerp
 * backgrounds and soft color transitions.
 * <p>
 * This theme features animated lerp backgrounds, translucent elements, and
 * soft color palettes to create a "glass" visual effect. It
 * includes looping animations for dynamic visual interest.
 * </p>
 * 
 * @see AbstractTheme
 * @see Color
 * @see LerpedLoopColor
 */
public class ThemeGlass extends AbstractTheme {

	/**
	 * Returns an animated looping lerp for the background. Transitions between
	 * light blue and white with transparency.
	 * 
	 * @return looping lerp from light blue to white
	 */
	@Override
	public AbstractColor getBackgroundColor() {
		return new LerpedLoopColor(new Color(200, 200, 255, 32), new Color(255, 32));
	}

	/**
	 * Returns a near-white color with high opacity for text views.
	 * 
	 * @return very light gray with high opacity (255 luminance, 232 alpha)
	 */
	@Override
	public AbstractColor getTextViewColor() {
		return new Color(255, 232);
	}

	/**
	 * Returns a light gray color for button text.
	 * 
	 * @return light gray (200 luminance)
	 */
	@Override
	public AbstractColor getButtonTextColor() {
		return new Color(200, 200);
	}

	/**
	 * Returns a light blue-gray color for strokes with medium transparency.
	 * 
	 * @return light blue-gray with medium opacity (RGB: 224,224,255, alpha: 128)
	 */
	@Override
	public AbstractColor getStrokeColor() {
		return new Color(224, 224, 255, 128);
	}

	/**
	 * Returns a semi-transparent cyan-blue for hover effects.
	 * 
	 * @return cyan-blue with low opacity (RGB: 0,200,255, alpha: 32)
	 */
	@Override
	public AbstractColor getHoverColor() {
		return new Color(0, 200, 255, 32);
	}

	/**
	 * Returns a very transparent gray-blue for ripple animations.
	 * 
	 * @return gray-blue with very low opacity (RGB: 132,132,154, alpha: 32)
	 */
	@Override
	public AbstractColor getRipplesColor() {
		return new Color(132, 132, 154, 32);
	}

	/**
	 * Returns an animated looping lerp for primary accents. Transitions between
	 * blue and purple tones with transparency.
	 * 
	 * @return looping lerp from blue to purple
	 */
	@Override
	public AbstractColor getPrimaryColor() {
		return new LerpedLoopColor(new Color(100, 200, 232, 100), new Color(164, 164, 232, 32));
	}

	/**
	 * Returns a very light blue-gray for editable text.
	 * 
	 * @return very light blue-gray with high opacity (RGB: 232,232,254, alpha: 232)
	 */
	@Override
	public AbstractColor getEditableTextColor() {
		return new Color(232, 232, 254, 232);
	}

	/**
	 * Returns a medium gray for selection highlighting.
	 * 
	 * @return medium gray (100 luminance, 100 alpha)
	 */
	@Override
	public AbstractColor getSelectColor() {
		return new Color(100, 100);
	}

	/**
	 * Returns a light blue-gray for text cursor/caret.
	 * 
	 * @return light blue-gray with high opacity (RGB: 200,200,232, alpha: 232)
	 */
	@Override
	public AbstractColor getCursorColor() {
		return new Color(200, 200, 232, 232);
	}

	/**
	 * Returns a light gray with low opacity for editable backgrounds.
	 * 
	 * @return light gray with low opacity (200 luminance, 32 alpha)
	 */
	@Override
	public AbstractColor getEditableBackgroundColor() {
		return new Color(200, 32);
	}

	/**
	 * Returns a light blue-gray for tooltip backgrounds.
	 * 
	 * @return light blue-gray with medium opacity (RGB: 200,200,232, alpha: 200)
	 */
	@Override
	public AbstractColor getTooltipBackgroundColor() {
		return new Color(200, 200, 232, 200);
	}

	/**
	 * Returns pure black for tooltip text.
	 * 
	 * @return black color
	 */
	@Override
	public AbstractColor getTooltipTextColor() {
		return new Color(0);
	}

	/**
	 * Returns a medium dark gray for menu button item backgrounds.
	 * 
	 * @return medium dark gray (64 luminance)
	 */
	@Override
	public AbstractColor getMenuButtonItemColor() {
		return Color.GRAY_64L;
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