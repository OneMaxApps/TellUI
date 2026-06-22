package tellui.core.effect;

import static java.util.Objects.requireNonNull;

import tellui.component.Knob;
import tellui.core.base.ContentView;
import tellui.core.base.View;
import tellui.core.exception.ValueOutOfRangeException;
import tellui.core.style.AbstractColor;
import tellui.core.style.Color;

/**
 * Abstract base class for shadow effects that can be applied to ContentView
 * components. Provides common functionality for managing shadow properties such
 * as color, weight (intensity), and target component association.
 * <p>
 * Subclasses must implement the {@link #render()} method to define specific
 * shadow drawing logic. This class manages the relationship between shadow
 * effects and their target components, allowing shadows to be positioned and
 * sized relative to their targets.
 * </p>
 * 
 * @see ContentView
 * @see AbstractColor
 * @see View
 */
public abstract class AbstractShadow extends View {
	/** Default weight of left side */
	public static final int DEFAULT_WEIGHT_LEFT = 0;
	/** Default weight of top side */
	public static final int DEFAULT_WEIGHT_TOP = 0;
	/** Default weight of right side */
	public static final int DEFAULT_WEIGHT_RIGHT = 3;
	/** Default weight of bottom side */
	public static final int DEFAULT_WEIGHT_BOTTOM = 2;
	/** Minimal weight of shadow */
	public static final int MIN_WEIGHT = 0;
	/** Maximal weight of shadow */
	public static final int MAX_WEIGHT = 10;

	private AbstractColor color;
	private ContentView target, customTarget;

	private FormMode formMode;
	
	private float weightLeft, weightTop, weightRight, weightBottom;
	
	/**
	 * Constructs an AbstractShadow with default properties. Default shadow has
	 * right and bottom weight with transparent black color.
	 */
	public AbstractShadow() {
		super();
		setVisible(true);
		setWeight(DEFAULT_WEIGHT_LEFT, DEFAULT_WEIGHT_TOP, DEFAULT_WEIGHT_RIGHT, DEFAULT_WEIGHT_BOTTOM);
		setColor(new Color(0, 10));
		
		setFormMode(FormMode.RECTANGLE);
	}

	/**
	 * Sets custom bounds for this shadow, overriding the target's bounds.
	 * The shadow will use the specified position and size instead of the target's.
	 * 
	 * @param x position X of shadow
	 * @param y position Y of shadow
	 * @param width current width of shadow
	 * @param height current height of shadow
	 */
	public void setCustomBounds(float x, float y, float width, float height) {
		if (customTarget == null) {
			customTarget = new ContentView() {
				{
					setConstrainDimensionsEnabled(false);
				}
				@Override
				protected void render() {
				}
			};

			target = customTarget;
		}
		
		customTarget.setBounds(x, y, width, height);
	}

	/**
	 * Returns the target ContentView for this shadow.
	 * 
	 * @return the target ContentView, or null if not set
	 */
	public ContentView getTarget() {
		return target;
	}

	/**
	 * Sets the target ContentView for this shadow. The shadow will be positioned
	 * and sized relative to this target.
	 * 
	 * @param target the ContentView to apply shadow to (cannot be null)
	 * @return this AbstractShadow for method chaining
	 * @throws NullPointerException if target is null
	 */
	public AbstractShadow setTarget(ContentView target) {
		this.target = requireNonNull(target, "target");

		if (target instanceof Knob) {
			setFormMode(FormMode.ELLIPSE);
		}
		
		return this;
	}

	/**
	 * Returns the color of this shadow.
	 * 
	 * @return the shadow color
	 */
	public AbstractColor getColor() {
		return color;
	}

	/**
	 * Sets the color of this shadow.
	 * 
	 * @param color the shadow color to set (cannot be null)
	 * @return this AbstractShadow for method chaining
	 * @throws NullPointerException if color is null
	 */
	public AbstractShadow setColor(AbstractColor color) {
		this.color = requireNonNull(color, "color");

		return this;
	}

	/**
	 * Returns the left shadow weight (intensity).
	 * 
	 * @return the left shadow weight
	 */
	public float getWeightLeft() {
		return weightLeft;
	}

	/**
	 * Returns the top shadow weight (intensity).
	 * 
	 * @return the top shadow weight
	 */
	public float getWeightTop() {
		return weightTop;
	}

	/**
	 * Returns the right shadow weight (intensity).
	 * 
	 * @return the right shadow weight
	 */
	public float getWeightRight() {
		return weightRight;
	}

	/**
	 * Returns the bottom shadow weight (intensity).
	 * 
	 * @return the bottom shadow weight
	 */
	public float getWeightBottom() {
		return weightBottom;
	}

	/**
	 * Checks if left shadow weight is non-zero.
	 * 
	 * @return true if left weight is not 0, false otherwise
	 */
	public boolean hasWeightLeft() {
		return weightLeft != 0;
	}

	/**
	 * Checks if top shadow weight is non-zero.
	 * 
	 * @return true if top weight is not 0, false otherwise
	 */
	public boolean hasWeightTop() {
		return weightTop != 0;
	}

	/**
	 * Checks if right shadow weight is non-zero.
	 * 
	 * @return true if right weight is not 0, false otherwise
	 */
	public boolean hasWeightRight() {
		return weightRight != 0;
	}

	/**
	 * Checks if bottom shadow weight is non-zero.
	 * 
	 * @return true if bottom weight is not 0, false otherwise
	 */
	public boolean hasWeightBottom() {
		return weightBottom != 0;
	}

	/**
	 * Checks if any shadow weight is non-zero.
	 * 
	 * @return true if any weight (left, top, right, or bottom) is not 0, false
	 *         otherwise
	 */
	public boolean hasWeight() {
		return hasWeightLeft() || hasWeightTop() || hasWeightRight() || hasWeightBottom();
	}

	/**
	 * Returns the x-coordinate of the target's padded area. Used for positioning
	 * the shadow relative to the target.
	 * 
	 * @return the target's padded x-coordinate, or 0 if no target is set
	 */
	protected float getTargetX() {
		if (target == null) {
			return 0;
		}

		return target.getPadX();
	}

	/**
	 * Returns the y-coordinate of the target's padded area. Used for positioning
	 * the shadow relative to the target.
	 * 
	 * @return the target's padded y-coordinate, or 0 if no target is set
	 */
	protected float getTargetY() {
		if (target == null) {
			return 0;
		}

		return target.getPadY();
	}

	/**
	 * Returns the width of the target's padded area. Used for sizing the shadow
	 * relative to the target.
	 * 
	 * @return the target's padded width, or 0 if no target is set
	 */
	protected float getTargetWidth() {
		if (target == null) {
			return 0;
		}

		return target.getPadWidth();
	}

	/**
	 * Returns the height of the target's padded area. Used for sizing the shadow
	 * relative to the target.
	 * 
	 * @return the target's padded height, or 0 if no target is set
	 */
	protected float getTargetHeight() {
		if (target == null) {
			return 0;
		}

		return target.getPadHeight();
	}

	/**
	 * Validates that a weight value is within allowed bounds.
	 * 
	 * @param weight the weight value to validate
	 * @throws ValueOutOfRangeException if the weight is less than MIN_WEIGHT ({@value #MIN_WEIGHT}) or greater
	 *                                  than MAX_WEIGHT ({@value #MAX_WEIGHT})
	 */
	protected void checkWeight(float weight) {
		if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
			throw new ValueOutOfRangeException("weight", weight, MIN_WEIGHT, MAX_WEIGHT);
		}
	}

	/**
	 * Sets the left shadow weight.
	 * 
	 * @param weightLeft the left weight to set (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and
	 *                   MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow setWeightLeft(float weightLeft) {
		checkWeight(weightLeft);
		this.weightLeft = weightLeft;

		return this;
	}

	/**
	 * Sets the top shadow weight.
	 * 
	 * @param weightTop the top weight to set (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and
	 *                   MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow setWeightTop(float weightTop) {
		checkWeight(weightTop);
		this.weightTop = weightTop;
		
		return this;
	}

	/**
	 * Sets the right shadow weight.
	 * 
	 * @param weightRight the right weight to set (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and
	 *                   MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow setWeightRight(float weightRight) {
		checkWeight(weightRight);
		this.weightRight = weightRight;
		
		return this;
	}

	/**
	 * Sets the bottom shadow weight.
	 * 
	 * @param weightBottom the bottom weight to set (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and
	 *                   MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow setWeightBottom(float weightBottom) {
		checkWeight(weightBottom);
		this.weightBottom = weightBottom;
		
		return this;
	}

	/**
	 * Clears all shadow weights (sets all to zero).
	 * 
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow clearAllWeight() {
		weightLeft = weightTop = weightRight = weightBottom = 0;
		return this;
	}

	/**
	 * Sets uniform shadow weight for all sides.
	 * 
	 * @param weight the weight value for all sides (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and
	 *                   MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow setWeight(float weight) {
		checkWeight(weight);
		weightLeft = weightTop = weightRight = weightBottom = weight;
		return this;
	}

	/**
	 * Sets shadow weight for horizontal and vertical sides separately.
	 * 
	 * @param weightHorizontal the weight for left and right sides (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * @param weightVertical   the weight for top and bottom sides (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow setWeight(float weightHorizontal, float weightVertical) {
		checkWeight(weightHorizontal);
		checkWeight(weightVertical);

		weightLeft = weightRight = weightHorizontal;
		weightTop = weightBottom = weightVertical;
		return this;
	}

	/**
	 * Sets individual shadow weights for each side.
	 * (must be between MIN_WEIGHT ({@value #MIN_WEIGHT}) and MAX_WEIGHT ({@value #MAX_WEIGHT}))
	 * 
	 * @param left   the left weight
	 * @param top    the top weight
	 * @param right  the right weight
	 * @param bottom the bottom weight
	 * @return this AbstractShadow for method chaining
	 */
	protected AbstractShadow setWeight(float left, float top, float right, float bottom) {
		checkWeight(left);
		checkWeight(top);
		checkWeight(right);
		checkWeight(bottom);

		weightLeft = left;
		weightTop = top;
		weightRight = right;
		weightBottom = bottom;
		return this;
	}
	
	protected FormMode getFormMode() {
		return formMode;
	
	}
	
	private void setFormMode(FormMode formMode) {
		this.formMode = formMode;
	}
	
	/**
	 * Provides setting for type of shadow
	 */
	protected static enum FormMode {
		ELLIPSE,
		RECTANGLE;
	}
}