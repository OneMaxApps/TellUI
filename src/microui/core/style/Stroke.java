package microui.core.style;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.SQUARE;

import microui.util.Metrics;
import processing.core.PGraphics;

/**
 * Represents a stroke style with color and weight properties for drawing
 * outlines. Provides methods for applying stroke settings to both the main
 * Processing context and PGraphics objects, with validation and default theme
 * integration.
 * <p>
 * The Stroke class encapsulates all stroke-related drawing properties including
 * color, weight (thickness), and stroke cap style (always SQUARE). It
 * integrates with the Metrics system for resource tracking and supports copying
 * from other Stroke objects.
 * </p>
 * <p>
 * Status: STABLE - Do not modify Last Reviewed: 27.10.2025
 * </p>
 * 
 * @see AbstractColor
 * @see Metrics
 */
public final class Stroke {
	private static final int DEFAULT_STROKE_WEIGHT = 1;
	private AbstractColor color;
	private float weight;

	/**
	 * Constructs a Stroke with specified color and weight.
	 * 
	 * @param color  the stroke color (cannot be null)
	 * @param weight the stroke weight in pixels (must be greater than 0)
	 * @throws NullPointerException     if color is null
	 * @throws IllegalArgumentException if weight is less than or equal to 0
	 */
	public Stroke(AbstractColor color, float weight) {
		setColor(color);
		setWeight(weight);
		Metrics.register(this);
	}

	/**
	 * Constructs a Stroke with specified color and default weight.
	 * 
	 * @param color the stroke color (cannot be null)
	 * @throws NullPointerException if color is null
	 */
	public Stroke(AbstractColor color) {
		this(color, DEFAULT_STROKE_WEIGHT);
	}

	/**
	 * Constructs a Stroke with specified weight and theme color.
	 * 
	 * @param weight the stroke weight in pixels (must be greater than 0)
	 * @throws IllegalArgumentException if weight is less than or equal to 0
	 */
	public Stroke(float weight) {
		this(getTheme().getStrokeColor(), weight);
	}

	/**
	 * Constructs a Stroke with default theme color and weight.
	 */
	public Stroke() {
		this(DEFAULT_STROKE_WEIGHT);
	}

	/**
	 * Applies this stroke style to the main Processing context. Sets stroke cap to
	 * SQUARE, applies the color, and sets the weight.
	 */
	public void apply() {
		getContext().strokeCap(SQUARE);
		color.applyStroke();
		getContext().strokeWeight(weight);
	}

	/**
	 * Applies this stroke style to the specified PGraphics context.
	 * 
	 * @param pGraphics the PGraphics context to apply the stroke to (cannot be
	 *                  null)
	 * @throws NullPointerException if pGraphics is null
	 */
	public void apply(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		pGraphics.strokeCap(SQUARE);
		color.applyStroke(pGraphics);
		pGraphics.strokeWeight(weight);
	}

	/**
	 * Sets the stroke weight (thickness).
	 * 
	 * @param weight the stroke weight in pixels (must be greater than 0)
	 * @throws IllegalArgumentException if weight is less than or equal to 0
	 */
	public void setWeight(float weight) {
		if (weight <= 0) {
			throw new IllegalArgumentException("stroke weight cannot be lower than 1");
		}
		this.weight = weight;
	}

	/**
	 * Returns the current stroke weight.
	 * 
	 * @return the stroke weight in pixels
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * Copies stroke properties from another Stroke object.
	 * 
	 * @param stroke the Stroke object to copy from (cannot be null)
	 * @throws NullPointerException if stroke is null
	 */
	public void set(Stroke stroke) {
		requireNonNull(stroke, "stroke");

		color = stroke.getColor();
		weight = stroke.getWeight();
	}

	/**
	 * Returns the current stroke color.
	 * 
	 * @return the stroke color
	 */
	public AbstractColor getColor() {
		return color;
	}

	/**
	 * Sets the stroke color.
	 * 
	 * @param color the stroke color to set (cannot be null)
	 * @throws NullPointerException if color is null
	 */
	public void setColor(AbstractColor color) {
		this.color = requireNonNull(color, "color");
	}
}