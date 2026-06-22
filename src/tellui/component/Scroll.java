package tellui.component;

import static tellui.core.style.theme.ThemeManager.getTheme;
import static telluii.util.MathUtils.constrain;

import tellui.constants.Orientation;
import tellui.core.LinearRangeControl;
import tellui.core.style.AbstractColor;
import telluii.util.MathUtils;

/**
 * A scroll-bar component with a draggable thumb for selecting values within a
 * range. The Scroll extends LinearRangeControl to provide linear scrolling
 * functionality with both horizontal and vertical orientations.
 */
public class Scroll extends LinearRangeControl {
	private static final float DEFAULT_MIN_VALUE = 0;
	private static final float DEFAULT_MAX_VALUE = 100;
	private static final float DEFAULT_VALUE = 50;
	private static final float DEFAULT_THUMB_SIZE_RATIO = .1f;
	private static final float MIN_THUMB_SIZE_RATIO = 0;
	private static final float MAX_THUMB_SIZE_RATIO = 1;
	
	private final Button thumb;
	private float distToThumb, thumbSizeRatio;
	private boolean needRecalculateDistToThumb, pressedInsideThumb;

	/**
	 * Constructs a Scroll with specified position and dimensions. The scroll-bar is
	 * initialized with a thumb button, default styling, and a thumb size ratio of
	 * {@value #DEFAULT_THUMB_SIZE_RATIO} (10% of the track).
	 *
	 * @param x the x-coordinate of the scrollbar's top-left corner
	 * @param y the y-coordinate of the scrollbar's top-left corner
	 * @param w the width of the scroll-bar
	 * @param h the height of the scroll-bar
	 */
	public Scroll(float x, float y, float w, float h) {
		super(x, y, w, h);

		thumb = new Button("");
		thumb.setId(IGNORE_INTERNAL_COMPONENT_ID);
		thumb.setConstrainDimensionsEnabled(false);
		thumb.setRipplesEnabled(false);
		thumb.setTextVisible(false);
		thumb.setBackgroundColor(getTheme().getPrimaryColor());

		onDragStart(() -> {
			pressedInsideThumb = isInsideThumb();
		});
		
		onDragging(() -> {
			if(!pressedInsideThumb) {
				return;
			}

			calcDistFromMouseToThumb();

			switch (getOrientation()) {
			case HORIZONTAL:
				thumb.setX(constrain(ctx.mouseX + distToThumb, getX(), getX() + getWidth() - thumb.getWidth()));
				setValueWithoutActions(MathUtils.convert(thumb.getX(), getX(), getX() + getWidth() - thumb.getWidth(),
						getMinValue(), getMaxValue()));
				break;

			case VERTICAL:
				thumb.setY(constrain(ctx.mouseY + distToThumb, getY(), getY() + getHeight() - thumb.getHeight()));
				setValueWithoutActions(MathUtils.convert(thumb.getY(), getY() + getHeight() - thumb.getHeight(), getY(),
						getMinValue(), getMaxValue()));
				break;
			}

			notifyOnChangeValueListeners();
			onStartChangeValue();
		});

		onDragEnd(() -> needRecalculateDistToThumb = true);
		needRecalculateDistToThumb = true;

		setThumbSizeRatio(DEFAULT_THUMB_SIZE_RATIO);

		updateThumbTransforms();
		setValue(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, DEFAULT_VALUE);
		
	}

	/**
	 * Constructs a Scroll with default maximum size, centered on the screen. This
	 * is a convenience constructor for creating a centered scroll-bar.
	 */
	public Scroll() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	/**
	 * Swaps the orientation of the scroll-bar.
	 * Updates the thumb position and dimensions accordingly.
	 */
	@Override
	public void swapOrientation() {
		super.swapOrientation();
		onChangeBounds();
	}

	/**
	 * Gets the color of the ripple effects on the thumb.
	 *
	 * @return the current thumb ripple color
	 */
	public final AbstractColor getThumbRipplesColor() {
		return thumb.getRipplesColor();
	}

	/**
	 * Sets the color of the ripple effects on the thumb.
	 *
	 * @param color the color to use for thumb ripple effects
	 */
	public final void setThumbRipplesColor(AbstractColor color) {
		thumb.setRipplesColor(color);
	}

	/**
	 * Checks if ripple effects are enabled on the thumb.
	 *
	 * @return true if thumb ripple effects are enabled, false otherwise
	 */
	public final boolean isThumbRipplesEnabled() {
		return thumb.isRipplesEnabled();
	}

	/**
	 * Enables or disables ripple effects on the thumb.
	 *
	 * @param enabled true to enable ripple effects, false to disable
	 */
	public final void setThumbRipplesEnabled(boolean enabled) {
		thumb.setRipplesEnabled(enabled);
	}

	/**
	 * Checks if hover effects are enabled on the thumb.
	 *
	 * @return true if thumb hover effects are enabled, false otherwise
	 */
	public final boolean isThumbHoverEnabled() {
		return thumb.isHoverEnabled();
	}

	/**
	 * Enables or disables hover effects on the thumb.
	 *
	 * @param enabled true to enable hover effects, false to disable
	 */
	public final void setThumbHoverEnabled(boolean enabled) {
		thumb.setHoverEnabled(enabled);
	}

	/**
	 * Gets the color of the hover effect on the thumb.
	 *
	 * @return the current thumb hover color
	 */
	public AbstractColor getThumbHoverColor() {
		return thumb.getHoverColor();
	}

	/**
	 * Sets the color of the hover effect on the thumb.
	 *
	 * @param color the color to use for thumb hover effects
	 */
	public void setThumbHoverColor(AbstractColor color) {
		thumb.setHoverColor(color);
	}

	/**
	 * Gets the animation speed of the hover effect on the thumb.
	 *
	 * @return the current thumb hover speed
	 */
	public final float getThumbHoverSpeed() {
		return thumb.getHoverSpeed();
	}

	/**
	 * Sets the animation speed of the hover effect on the thumb.
	 *
	 * @param speed the animation speed for thumb hover effects
	 */
	public final void setThumbHoverSpeed(float speed) {
		thumb.setHoverSpeed(speed);
	}

	/**
	 * Gets the stroke weight (border thickness) of the thumb.
	 *
	 * @return the current thumb stroke weight
	 */
	public final float getThumbStrokeWeight() {
		return thumb.getStrokeWeight();
	}

	/**
	 * Sets the stroke weight (border thickness) of the thumb.
	 *
	 * @param weight the stroke weight for the thumb border
	 */
	public final void setThumbStrokeWeight(int weight) {
		thumb.setStrokeWeight(weight);
	}

	/**
	 * Gets the stroke color (border color) of the thumb.
	 *
	 * @return the current thumb stroke color
	 */
	public final AbstractColor getThumbStrokeColor() {
		return thumb.getStrokeColor();
	}

	/**
	 * Sets the stroke color of the thumb.
	 *
	 * @param color the color for the thumb border
	 */
	public final void setThumbStrokeColor(AbstractColor color) {
		thumb.setStrokeColor(color);
	}

	/**
	 * Gets the background color of the thumb.
	 *
	 * @return the current thumb background color
	 */
	public final AbstractColor getThumbColor() {
		return thumb.getBackgroundColor();
	}

	/**
	 * Sets the background color of the thumb.
	 *
	 * @param color the background color for the thumb
	 */
	public final void setThumbColor(AbstractColor color) {
		thumb.setBackgroundColor(color);
	}

	/**
	 * Gets the calculated size of the thumb based on the thumbSizeRatio and
	 * orientation. For horizontal scroll-bars, returns width × thumbSizeRatio. For
	 * vertical scroll-bars, returns height × thumbSizeRatio.
	 *
	 * @return the calculated thumb size in pixels
	 */
	public final float getCalculatedThumbSize() {
		return getOrientation() == Orientation.HORIZONTAL ? getWidth() * thumbSizeRatio : getHeight() * thumbSizeRatio;
	}

	/**
	 * Sets the ratio that determines the thumb size relative to the track. The
	 * thumbSizeRatio must be between {@value #MIN_THUMB_SIZE_RATIO} and {@value #MAX_THUMB_SIZE_RATIO} inclusive.
	 *
	 * @param thumbSizeRatio the ratio of thumb size to track size ({@value #MIN_THUMB_SIZE_RATIO} to {@value #MAX_THUMB_SIZE_RATIO})
	 * @throws IllegalStateException if thumbSizeRatio is less than {@value #MIN_THUMB_SIZE_RATIO} or greater
	 *                               than {@value #MAX_THUMB_SIZE_RATIO}
	 */
	public final void setThumbSizeRatio(float thumbSizeRatio) {
		if (thumbSizeRatio < MIN_THUMB_SIZE_RATIO || thumbSizeRatio > MAX_THUMB_SIZE_RATIO) {
			throw new IllegalStateException("Thumb ratio in scroll cannot be lower than " + MIN_THUMB_SIZE_RATIO + " or be greater than " + MAX_THUMB_SIZE_RATIO);
		}
		this.thumbSizeRatio = thumbSizeRatio;
	}
	
	@Override
	protected void render() {
		super.render();
		thumb.draw();
		
		if (!ctx.mousePressed) {
			pressedInsideThumb = false;
		}
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (thumb == null) {
			return;
		}
		updateThumbTransforms();
	}

	private final void calcDistFromMouseToThumb() {
		if (!needRecalculateDistToThumb) {
			return;
		}

		switch (getOrientation()) {
		case HORIZONTAL:
			distToThumb = thumb.getX() - ctx.mouseX;
			break;

		case VERTICAL:
			distToThumb = thumb.getY() - ctx.mouseY;
			break;
		}

		needRecalculateDistToThumb = false;
	}

	private final void updateThumbTransforms() {
		thumb.setBounds(getX(), getY(), getWidth(), getHeight());

		float ratio = getCalculatedThumbSize();

		switch (getOrientation()) {
		case HORIZONTAL:
			thumb.setWidth(ratio);
			if (hasEqualMinMax()) {
				return;
			}
			final float newX = MathUtils.convert(getValue(), getMinValue(), getMaxValue(), getX(),
					getX() + getWidth() - ratio);
			thumb.setX(constrain(newX, getX(), getX() + getWidth() - ratio));
			break;
		case VERTICAL:
			thumb.setHeight(ratio);
			if (hasEqualMinMax()) {
				return;
			}
			final float newY = MathUtils.convert(getValue(), getMaxValue(), getMinValue(), getY(),
					getY() + getHeight() - ratio);
			thumb.setY(constrain(newY, getY(), getY() + getHeight() - ratio));
			break;
		}
	}
	
	private boolean isInsideThumb() {
		final float x = thumb.getPadX();
		final float y = thumb.getPadY();
		final float w = thumb.getPadWidth();
		final float h = thumb.getPadHeight();
		
		final float mx = ctx.mouseX;
		final float my = ctx.mouseY;
		
		return mx > x && mx < x+w && my > y && my < y+h;
	}
}