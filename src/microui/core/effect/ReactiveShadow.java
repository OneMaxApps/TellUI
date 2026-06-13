package microui.core.effect;

import static microui.util.MathUtils.constrain;
import static microui.util.MathUtils.convert;
import static microui.util.MathUtils.dist;

import microui.core.base.ContentView;
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

	/**
	 * Constructs a ReactiveShadow with default properties. Initializes with all
	 * weights cleared and a semi-transparent black color.
	 */
	public ReactiveShadow() {
		super();
		clearAllWeight();
		setColor(new Color(0, 32));
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

		switch(getFormMode()) {
		case ELLIPSE:
			ellipseModeOnRender();
			break;
			
		case RECTANGLE: 
			rectangleModeOnRender();
			break;	
		}

	}
	
	private void ellipseModeOnRender() {
		final float x = getTargetX();
		final float y = getTargetY();
		final float w = getTargetWidth();
		final float h = getTargetHeight();
		
		final float tw = getTarget().getPadWidth();
		final float th = getTarget().getPadHeight();
		
		final int mx = ctx.mouseX;
		final int my = ctx.mouseY;

		for (int i = 0; i < MAX_WEIGHT; i++) {
			final int r = getColor().getRed();
			final int g = getColor().getGreen();
			final int b = getColor().getBlue();
			final int a = (int) convert(i, 0, MAX_WEIGHT, getColor().getAlpha() - constrain(dist(mx, my, x + tw / 2, y + th / 2) / 10, 0, getColor().getAlpha()), 0);
			
			ctx.stroke(r, g, b, a);
			
			final float newX = x - convert(i, 0, MAX_WEIGHT, 0, getWeightLeft());
			final float newY = y - convert(i, 0, MAX_WEIGHT, 0, getWeightTop());
			final float newX1 = w + convert(i, 0, MAX_WEIGHT, 0, getWeightRight());
			final float newY1 = h + convert(i, 0, MAX_WEIGHT, 0, getWeightBottom());

			ctx.ellipse(newX, newY, newX1, newY1);
		}
	}

	private void rectangleModeOnRender() {
		final float x = getTargetX();
		final float y = getTargetY();
		final float x1 = getTargetX() + getTargetWidth();
		final float y1 = getTargetY() + getTargetHeight();

		final float tw = getTarget().getPadWidth();
		final float th = getTarget().getPadHeight();
		
		final int mx = ctx.mouseX;
		final int my = ctx.mouseY;

		ctx.rectMode(PApplet.CORNERS);

		for (int i = 0; i < MAX_WEIGHT; i++) {
			final int r = getColor().getRed();
			final int g = getColor().getGreen();
			final int b = getColor().getBlue();
			final int a = (int) convert(i, 0, MAX_WEIGHT, getColor().getAlpha() - constrain(dist(mx, my, x + tw / 2, y + th / 2) / 10, 0, getColor().getAlpha()), 0);
			
			ctx.stroke(r, g, b, a);

			final float newX = x - convert(i, 0, MAX_WEIGHT, 0, getWeightLeft());
			final float newY = y - convert(i, 0, MAX_WEIGHT, 0, getWeightTop());
			final float newX1 = x1 + convert(i, 0, MAX_WEIGHT, 0, getWeightRight());
			final float newY1 = y1 + convert(i, 0, MAX_WEIGHT, 0, getWeightBottom());

			ctx.rect(newX, newY, newX1, newY1);
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