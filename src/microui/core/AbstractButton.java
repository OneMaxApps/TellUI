package microui.core;

import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.base.Component;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;

/**
 * Abstract base class for all button-like components.
 * 
 * @see Component
 */
public abstract class AbstractButton extends Component {
	private final Stroke stroke;

	/**
	 * Constructs an AbstractButton with specified position and dimensions.
	 * Initializes with default theme styling.
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

	protected final Stroke getStrokeInternal() {
		return stroke;
	}
}