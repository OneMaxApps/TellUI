package microui.component;

import static java.lang.Math.PI;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static processing.core.PApplet.dist;

import microui.core.RangeControl;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.util.MathUtils;
import processing.event.MouseEvent;

/**
 * A circular knob control for selecting values within a range. The Knob
 * component allows users to adjust a numeric value by dragging the knob or
 * using mouse wheel scrolling. It provides visual feedback through an arc
 * indicator that shows the current value.
 * 
 * <p>
 * The knob is circular and responds to mouse dragging within its diameter as
 * well as mouse wheel events when hovered.
 * </p>
 * 
 * @see RangeControl
 */
// TODO add JavaDoc for all public methods
public final class Knob extends RangeControl {
	private static final float START = (float) PI/8;
	private static final float END = (float) (PI * 2 - PI/8);
	private static final int MIN_INDICATOR_WEIGHT = 1;
	private AbstractColor indicatorColor;
	private float centerX, centerY, diameter, indicatorWeight;
	private boolean canDrag;

	/**
	 * Constructs a Knob with specified position and dimensions. The knob is
	 * initialized with the theme's primary color for the indicator and
	 * minimum/maximum sizes of 10 and 50 pixels respectively.
	 *
	 * @param x      the x-coordinate of the knob's bounding box
	 * @param y      the y-coordinate of the knob's bounding box
	 * @param width  the width of the knob's bounding box
	 * @param height the height of the knob's bounding box
	 */
	public Knob(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinMaxSize(10, 50);

		indicatorColor = new Color(0,200,0);

		onDragging(() -> {
			if (isMouseInDiameter()) {
				canDrag = true;
			}
		});
		
		recalculateDiameter();
		setIndicatorWeight(diameter);
	}

	/**
	 * Constructs a Knob with default maximum size, centered on the screen. This is
	 * a convenience constructor for creating a centered knob.
	 */
	public Knob() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	/**
	 * Handles mouse wheel events when the mouse is within the knob's diameter. The
	 * scrolling delta is captured for value adjustment.
	 *
	 * @param mouseEvent the mouse wheel event
	 */
	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		if (isMouseInDiameter()) {
			getMutableScrolling().init(mouseEvent);
		}
	}

	/**
	 * Gets the color used for the indicator arc.
	 *
	 * @return the current indicator color
	 */
	public AbstractColor getIndicatorColor() {
		return indicatorColor;
	}

	/**
	 * Sets the color used for the indicator arc.
	 *
	 * @param indicatorColor the color to use for the indicator
	 * @throws NullPointerException if indicatorColor is null
	 */
	public void setIndicatorColor(AbstractColor indicatorColor) {
		this.indicatorColor = requireNonNull(indicatorColor, "indicatorColor");
	}
	
	public float getIndicatorWeight() {
		return indicatorWeight;
	}

	public void setIndicatorWeight(float indicatorWeight) {
		if(indicatorWeight < MIN_INDICATOR_WEIGHT) {
			throw new IllegalArgumentException("Indicator weight for Knob must be greater than " + MIN_INDICATOR_WEIGHT);
		}
		this.indicatorWeight = indicatorWeight;
	}

	/**
	 * Renders the knob and handles user interaction. The rendering includes:
	 * <ol>
	 * <li>The circular background</li>
	 * <li>The arc indicator showing the current value</li>
	 * <li>Interaction state updates</li>
	 * <li>Value updates based on dragging or scrolling</li>
	 * </ol>
	 */
	@Override
	protected void render() {
		indicatorOnDraw();

		if (!ctx.mousePressed) {
			canDrag = false;
		}

		if (canDrag) {
			getMutableValue().append(ctx.pmouseY - ctx.mouseY);
		}

		getMutableValue().append(getMutableScrolling().get());
	}

	/**
	 * Called when the knob's bounds change. Recalculates the center point and
	 * diameter based on the new bounds.
	 */
	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		recalculateCenter();
		recalculateDiameter();
	}

	private void recalculateCenter() {
		centerX = getX() + getWidth() / 2;
		centerY = getY() + getHeight() / 2;
	}

	private void recalculateDiameter() {
		diameter = min(getWidth(), getHeight());
	}

	private boolean isMouseInDiameter() {
		return dist(centerX, centerY, ctx.mouseX, ctx.mouseY) < diameter / 2;
	}

	private void indicatorOnDraw() {
		ctx.push();

		ctx.translate(centerX, centerY);
		ctx.rotate((float) PI / 2);
		ctx.noFill();
		ctx.strokeWeight(indicatorWeight);

		getBackgroundColor().applyStroke();
		ctx.arc(0, 0, diameter * .8f, diameter * .8f, START, END);

		indicatorColor.applyStroke();
		ctx.arc(0, 0, diameter * .8f, diameter * .8f, START, MathUtils.convert(getMutableValue().get(),
				getMutableValue().getMin(), getMutableValue().getMax(), START, END));

		ctx.pop();
	}

	
}