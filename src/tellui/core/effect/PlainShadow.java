package tellui.core.effect;

import static telluii.util.MathUtils.convert;

import processing.core.PApplet;
import tellui.core.base.ContentView;

/**
 * A simple shadow effect that creates a solid outline around a ContentView.
 * Extends AbstractShadow to provide a basic shadow implementation with optional
 * alpha fade-out.
 * <p>
 * The PlainShadow can render either a solid outline or a gradient fade-out
 * effect based on the weight values set for each side. When alpha fade-out is
 * enabled, the shadow creates a smooth gradient from full opacity to
 * transparent.
 * </p>
 * 
 * @see AbstractShadow
 * @see ContentView
 */
public class PlainShadow extends AbstractShadow {
	private boolean alphaFadeOutEnabled;

	/**
	 * Constructs a PlainShadow with default properties. Alpha fade-out is enabled
	 * by default.
	 */
	public PlainShadow() {
		super();
		setAlphaFadeOutEnabled(true);
	}

	/**
	 * Constructs a PlainShadow with a specified target ContentView.
	 * 
	 * @param target the ContentView to apply the shadow to
	 */
	public PlainShadow(ContentView target) {
		this();
		setTarget(target);
	}

	/**
	 * Checks if alpha fade-out effect is enabled.
	 * 
	 * @return true if alpha fade-out is enabled, false for solid outline
	 */
	public boolean isAlphaFadeOutEnabled() {
		return alphaFadeOutEnabled;
	}

	/**
	 * Enables or disables the alpha fade-out effect.
	 * 
	 * @param alphaFadeOutEnabled true to enable gradient fade-out, false for solid
	 *                            outline
	 * @return this PlainShadow for method chaining
	 */
	public PlainShadow setAlphaFadeOutEnabled(boolean alphaFadeOutEnabled) {
		this.alphaFadeOutEnabled = alphaFadeOutEnabled;

		return this;
	}

	/**
	 * Sets the left shadow weight. Overrides parent method to provide type-correct
	 * return value.
	 * 
	 * @param weightLeft the left weight to set (must be between MIN_WEIGHT and
	 *                   MAX_WEIGHT
	 * @return this PlainShadow for method chaining
	 */
	@Override
	public AbstractShadow setWeightLeft(float weightLeft) {
		return super.setWeightLeft(weightLeft);
	}

	/**
	 * Sets the top shadow weight. Overrides parent method to provide type-correct
	 * return value.
	 * 
	 * @param weightTop the top weight to set (must be between MIN_WEIGHT and
	 *                  MAX_WEIGHT)
	 * @return this PlainShadow for method chaining
	 */
	@Override
	public AbstractShadow setWeightTop(float weightTop) {
		return super.setWeightTop(weightTop);
	}

	/**
	 * Sets the right shadow weight. Overrides parent method to provide type-correct
	 * return value.
	 * 
	 * @param weightRight the right weight to set (must be between MIN_WEIGHT and
	 *                    MAX_WEIGHT)
	 * @return this PlainShadow for method chaining
	 */
	@Override
	public AbstractShadow setWeightRight(float weightRight) {
		return super.setWeightRight(weightRight);
	}

	/**
	 * Sets the bottom shadow weight. Overrides parent method to provide
	 * type-correct return value.
	 * 
	 * @param weightBottom the bottom weight to set (must be between MIN_WEIGHT and
	 *                     MAX_WEIGHT)
	 * @return this PlainShadow for method chaining
	 */
	@Override
	public AbstractShadow setWeightBottom(float weightBottom) {
		return super.setWeightBottom(weightBottom);
	}

	/**
	 * Sets uniform shadow weight for all sides. Overrides parent method to provide
	 * type-correct return value.
	 * 
	 * @param weight the weight value for all sides (must be between MIN_WEIGHT and
	 *               MAX_WEIGHT)
	 * @return this PlainShadow for method chaining
	 */
	@Override
	public AbstractShadow setWeight(float weight) {
		return super.setWeight(weight);
	}

	/**
	 * Sets shadow weight for horizontal and vertical sides separately. Overrides
	 * parent method to provide type-correct return value.
	 * 
	 * @param weightHorizontal the weight for left and right sides (must be between
	 *                         MIN_WEIGHT and MAX_WEIGHT)
	 * @param weightVertical   the weight for top and bottom sides (must be between
	 *                         MIN_WEIGHT and MAX_WEIGHT)
	 * @return this PlainShadow for method chaining
	 */
	@Override
	public AbstractShadow setWeight(float weightHorizontal, float weightVertical) {
		return super.setWeight(weightHorizontal, weightVertical);
	}

	/**
	 * Sets individual shadow weights for each side. Overrides parent method to
	 * provide type-correct return value. (must be between MIN_WEIGHT and
	 * MAX_WEIGHT)
	 * 
	 * @param left   the left weight
	 * @param top    the top weight
	 * @param right  the right weight
	 * @param bottom the bottom weight
	 * @return this PlainShadow for method chaining
	 */
	@Override
	public AbstractShadow setWeight(float left, float top, float right, float bottom) {
		return super.setWeight(left, top, right, bottom);
	}

	/**
	 * Renders the plain shadow effect. Creates either a gradient fade-out or solid
	 * outline based on alpha fade-out setting.
	 */
	@Override
	protected void render() {
		getColor().apply();

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

		final int r = getColor().getRed();
		final int g = getColor().getGreen();
		final int b = getColor().getBlue();

		for (int i = 0; i < MAX_WEIGHT; i++) {
		final float a = isAlphaFadeOutEnabled() ? convert(i, 0, MAX_WEIGHT, getColor().getAlpha(), 0) : getColor().getAlpha();
		
		ctx.noFill();
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
}