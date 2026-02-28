package microui.core;

import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.base.Component;
import microui.core.effect.Hover;
import microui.core.effect.Ripples;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;

/**
 * Abstract base class for all button-like components in the microui framework.
 * Provides common button functionality including ripple effects, hover effects,
 * stroke/border styling, and visual feedback.
 * 
 * <p>
 * <strong>Status:</strong> STABLE - Do not modify
 * </p>
 * <p>
 * <strong>Last Reviewed:</strong> 21.10.2025
 * </p>
 * 
 * @see Component
 * @see Ripples
 * @see Hover
 * @see Stroke
 */
public abstract class AbstractButton extends Component {
	private final Stroke stroke;

	/**
	 * Constructs an AbstractButton with specified position and dimensions.
	 * Initializes with default theme styling and creates ripple, hover, and stroke
	 * effects.
	 *
	 * @param x the x-coordinate of the button's top-left corner
	 * @param y the y-coordinate of the button's top-left corner
	 * @param w the width of the button
	 * @param h the height of the button
	 */
	public AbstractButton(float x, float y, float w, float h) {
		super(x, y, w, h);
		setBackgroundColor(getTheme().getBackgroundColor());
		
		stroke = new Stroke();
		
		
	}

	/**
	 * Gets the stroke weight (border thickness) of the button.
	 *
	 * @return the current stroke weight
	 */
	public final float getStrokeWeight() {
		return stroke.getWeight();
	}

	/**
	 * Sets the stroke weight (border thickness) of the button.
	 *
	 * @param weight the stroke weight to set
	 * @return this AbstractButton instance for method chaining
	 */
	public final AbstractButton setStrokeWeight(int weight) {
		stroke.setWeight(weight);
		return this;
	}

	/**
	 * Gets the stroke color (border color) of the button.
	 *
	 * @return the current stroke color
	 */
	public final AbstractColor getStrokeColor() {
		return stroke.getColor();
	}

	/**
	 * Sets the stroke color (border color) of the button.
	 *
	 * @param color the stroke color to set
	 * @return this AbstractButton instance for method chaining
	 */
	public final AbstractButton setStrokeColor(AbstractColor color) {
		stroke.setColor(color);
		return this;
	}

	/**
	 * Gets the internal Stroke styling instance. Protected access for use by
	 * subclasses in their rendering.
	 *
	 * @return the internal Stroke instance
	 */
	protected final Stroke getStrokeInternal() {
		return stroke;
	}
}