package microui.core.effect;

import static microui.util.MathUtils.constrain;
import static microui.util.MathUtils.convert;
import static microui.util.MathUtils.dist;

import microui.core.base.ContentView;
import microui.core.exception.ValueOutOfRangeException;
import microui.core.style.Color;
import processing.core.PApplet;

/**
 * A reactive shadow effect that dynamically adjusts its intensity based on
 * mouse position. Creates an interactive shadow that responds to mouse
 * movement, with shadow weights calculated relative to the mouse's position
 * relative to the target component.
 * <p>
 * The ReactiveShadow creates a gradient shadow effect where each side's
 * intensity is determined by the mouse's proximity to that side of the target
 * component. The shadow dynamically updates as the mouse moves, creating an
 * interactive visual feedback effect.
 * </p>
 * 
 * @see AbstractShadow
 * @see ContentView
 */
public class ReactiveShadow extends AbstractShadow {
	/** Default value for fall-off radius */
	public static final int DEFAULT_FALLOFF_RADIUS = 10;
	/** Min value for fall-off radius */
	public static final int MIN_FALLOFF_RADIUS = 1;
	/** Max value for fall-off radius */
	public static final int MAX_FALLOFF_RADIUS = 100;

	private int falloffRadius;

	/**
	 * Constructs a ReactiveShadow with default properties. Initializes with all
	 * weights cleared and a semi-transparent black color.
	 */
	public ReactiveShadow() {
		super();
		clearAllWeight();
		setColor(new Color(0, 32));
		setFalloffRadius(DEFAULT_FALLOFF_RADIUS);
	}

	/**
	 * Constructs a ReactiveShadow with a specified target ContentView.
	 * 
	 * @param target the ContentView to apply the reactive shadow to
	 */
	public ReactiveShadow(ContentView target) {
		this();
		setTarget(target);

	}

	/**
	 * Requests an immediate update of shadow weights based on current mouse
	 * position. Useful for forcing a weight recalculation when needed.
	 */
	public void requestUpdateWeights() {
		updateWeights();
	}

	/**
	 * Getter for falloffRadius value. (default value is
	 * {@value #DEFAULT_FALLOFF_RADIUS})
	 * 
	 * @return falloffRadius
	 */
	public final int getFalloffRadius() {
		return falloffRadius;
	}

	/**
	 * Setter for falloffRadius. (Must be between {@value #MIN_FALLOFF_RADIUS} and
	 * {@value #MAX_FALLOFF_RADIUS})
	 * 
	 * @param falloffRadius radius for fall-off effect
	 * @throws ValueOutOfRangeException if falloffRadius is not between
	 *                                  {@value #MIN_FALLOFF_RADIUS} and
	 *                                  {@value #MAX_FALLOFF_RADIUS}
	 */
	public final void setFalloffRadius(int falloffRadius) {
		if (falloffRadius < MIN_FALLOFF_RADIUS || falloffRadius > MAX_FALLOFF_RADIUS) {
			throw new ValueOutOfRangeException("falloffRadius", falloffRadius, MIN_FALLOFF_RADIUS, MAX_FALLOFF_RADIUS);
		}

		this.falloffRadius = falloffRadius;
	}

	/**
	 * Renders the reactive shadow effect. Automatically updates shadow weights when
	 * the mouse is moving, then draws the gradient shadow based on current weight
	 * values.
	 */
	@Override
	protected void render() {
		if (isMouseMoving()) {
			updateWeights();
		}

		ctx.noFill();

		shapeOnRender(getFormMode());

	}

	private void shapeOnRender(FormMode mode) {
		final float x = getTargetX();
		final float y = getTargetY();
		float x1 = 0;
		float y1 = 0;

		switch (mode) {
		case RECTANGLE:
			x1 = getTargetX() + getTargetWidth();
			y1 = getTargetY() + getTargetHeight();
			break;

		case ELLIPSE:
			x1 = getTargetWidth();
			y1 = getTargetHeight();
			break;
		}

		final float tw = getTarget().getPadWidth();
		final float th = getTarget().getPadHeight();

		final int mx = ctx.mouseX;
		final int my = ctx.mouseY;

		final int r = getColor().getRed();
		final int g = getColor().getGreen();
		final int b = getColor().getBlue();

		for (int i = 0; i < MAX_WEIGHT; i++) {
			final int a = (int) convert(i, 0, MAX_WEIGHT, getColor().getAlpha()
					- constrain(dist(mx, my, x + tw / 2, y + th / 2) / getFalloffRadius(), 0, getColor().getAlpha()),
					0);

			ctx.stroke(r, g, b, a);

			final float newX = x - convert(i, 0, MAX_WEIGHT, 0, getWeightLeft());
			final float newY = y - convert(i, 0, MAX_WEIGHT, 0, getWeightTop());
			final float newX1 = x1 + convert(i, 0, MAX_WEIGHT, 0, getWeightRight());
			final float newY1 = y1 + convert(i, 0, MAX_WEIGHT, 0, getWeightBottom());

			switch (mode) {
			case RECTANGLE:
				ctx.rectMode(PApplet.CORNERS);
				ctx.rect(newX, newY, newX1, newY1);
				break;

			case ELLIPSE:
				ctx.ellipse(newX, newY, newX1, newY1);
				break;
			}
		}
	}

	private void updateWeights() {
		final float tx = getTarget().getPadX();
		final float ty = getTarget().getPadY();
		final float tw = getTarget().getPadWidth();
		final float th = getTarget().getPadHeight();

		final int mx = ctx.mouseX;
		final int my = ctx.mouseY;

		setWeightLeft(constrain(convert(mx, tx, tx + tw, MIN_WEIGHT, MAX_WEIGHT), MIN_WEIGHT, MAX_WEIGHT));
		setWeightTop(constrain(convert(my, ty, ty + th, MIN_WEIGHT, MAX_WEIGHT), MIN_WEIGHT, MAX_WEIGHT));
		setWeightRight(constrain(convert(mx, tx + tw, tx, MIN_WEIGHT, MAX_WEIGHT), MIN_WEIGHT, MAX_WEIGHT));
		setWeightBottom(constrain(convert(my, ty + th, ty, MIN_WEIGHT, MAX_WEIGHT), MIN_WEIGHT, MAX_WEIGHT));

	}

	private boolean isMouseMoving() {
		return ctx.mouseX != ctx.pmouseX || ctx.mouseY != ctx.pmouseY;
	}

}