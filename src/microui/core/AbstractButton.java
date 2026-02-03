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
	private final Ripples ripples;
	private final Hover hover;
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
		ripples = new Ripples(this);
		hover = new Hover(this);
		stroke = new Stroke();
	}

	/**
	 * Gets the color of the ripple effects.
	 *
	 * @return the current ripple effect color
	 */
	public final AbstractColor getRipplesColor() {
		return ripples.getColor();
	}

	/**
	 * Sets the color of the ripple effects.
	 *
	 * @param color the color to use for ripple effects
	 * @return this AbstractButton instance for method chaining
	 */
	public final AbstractButton setRipplesColor(AbstractColor color) {
		ripples.setColor(color);
		return this;
	}

	/**
	 * Checks if ripple effects are enabled.
	 *
	 * @return true if ripple effects are enabled, false otherwise
	 */
	public final boolean isRipplesEnabled() {
		return ripples.isEnabled();
	}

	/**
	 * Enables or disables ripple effects.
	 *
	 * @param isEnabled true to enable ripple effects, false to disable
	 * @return this AbstractButton instance for method chaining
	 */
	public final AbstractButton setRipplesEnabled(boolean isEnabled) {
		ripples.setEnabled(isEnabled);
		return this;
	}

	/**
	 * Checks if hover effects are enabled.
	 *
	 * @return true if hover effects are enabled, false otherwise
	 */
	public final boolean isHoverEnabled() {
		return hover.isEnabled();
	}

	/**
	 * Enables or disables hover effects.
	 *
	 * @param isEnabled true to enable hover effects, false to disable
	 * @return this AbstractButton instance for method chaining
	 */
	public final AbstractButton setHoverEnabled(boolean isEnabled) {
		hover.setEnabled(isEnabled);
		return this;
	}

	/**
	 * Gets the color of the hover effect.
	 *
	 * @return the current hover effect color
	 */
	public AbstractColor getHoverColor() {
		return hover.getColor();
	}

	/**
	 * Sets the color of the hover effect.
	 *
	 * @param color the color to use for hover effects
	 * @return this AbstractButton instance for method chaining
	 */
	public AbstractButton setHoverColor(AbstractColor color) {
		hover.setColor(color);
		return this;
	}

	/**
	 * Gets the animation speed of the hover effect.
	 *
	 * @return the current hover animation speed
	 */
	public final float getHoverSpeed() {
		return hover.getSpeed();
	}

	/**
	 * Sets the animation speed of the hover effect.
	 *
	 * @param speed the animation speed for hover effects
	 * @return this AbstractButton instance for method chaining
	 */
	public final AbstractButton setHoverSpeed(float speed) {
		hover.setSpeed(speed);
		return this;
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
	 * Renders the button's base visual representation. Draws a rectangle with
	 * stroke and background color. Subclasses should call super.render() and add
	 * their specific rendering.
	 */
	@Override
	protected void render() {
		ctx.pushStyle();
		stroke.apply();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();
	}

	/**
	 * Gets the internal Ripples effect instance. Protected access for use by
	 * subclasses in their rendering.
	 *
	 * @return the internal Ripples instance
	 */
	protected final Ripples getRipplesInternal() {
		return ripples;
	}

	/**
	 * Gets the internal Hover effect instance. Protected access for use by
	 * subclasses in their rendering.
	 *
	 * @return the internal Hover instance
	 */
	protected final Hover getHoverInternal() {
		return hover;
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