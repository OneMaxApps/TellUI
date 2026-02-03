package microui.component;

import static microui.core.style.theme.ThemeManager.getTheme;
import static microui.util.MathUtils.constrain;

import microui.constants.Orientation;
import microui.core.LinearRangeControl;
import microui.core.style.AbstractColor;
import microui.util.MathUtils;

/**
 * A scrollbar component with a draggable thumb for selecting values within a
 * range. The Scroll extends LinearRangeControl to provide linear scrolling
 * functionality with both horizontal and vertical orientations.
 * 
 * <p>
 * The scrollbar consists of a track and a draggable thumb. The thumb's size is
 * determined by the thumbSizeRatio, which represents the proportion of the
 * track that the thumb occupies.
 * </p>
 * 
 * <p>
 * Users can drag the thumb along the track to change the value, and the thumb
 * provides visual feedback through hover and ripple effects.
 * </p>
 */
public class Scroll extends LinearRangeControl {
	private final Button thumb;
	private float distToThumb, thumbSizeRatio;
	private boolean needRecalculateDistToThumb;

	/**
	 * Constructs a Scroll with specified position and dimensions. The scrollbar is
	 * initialized with a thumb button, default styling, and a thumb size ratio of
	 * 0.1 (10% of the track).
	 *
	 * @param x the x-coordinate of the scrollbar's top-left corner
	 * @param y the y-coordinate of the scrollbar's top-left corner
	 * @param w the width of the scrollbar
	 * @param h the height of the scrollbar
	 */
	public Scroll(float x, float y, float w, float h) {
		super(x, y, w, h);

		thumb = new Button("");
		thumb.setConstrainDimensionsEnabled(false);
		thumb.setRipplesEnabled(false);
		thumb.setTextVisible(false);
		thumb.setBackgroundColor(getTheme().getPrimaryColor());

		thumb.onDragging(() -> {
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

			onChangeValue();
			onStartChangeValue();
		});

		thumb.onDragEnd(() -> needRecalculateDistToThumb = true);
		needRecalculateDistToThumb = true;

		setThumbSizeRatio(.1f);

		updateThumbTransforms();
		setValue(0, 100, 50);
	}

	/**
	 * Constructs a Scroll with default maximum size, centered on the screen. This
	 * is a convenience constructor for creating a centered scrollbar.
	 */
	public Scroll() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	/**
	 * Swaps the orientation of the scrollbar (horizontal to vertical or vice
	 * versa). Updates the thumb position and dimensions accordingly.
	 */
	@Override
	public void swapOrientation() {
		super.swapOrientation();
		onChangeBounds();
	}

	/**
	 * Checks if the thumb is currently being clicked.
	 *
	 * @return true if the thumb is being clicked, false otherwise
	 */
	public final boolean isThumbClicked() {
		return thumb.isClick();
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
	 * @param isEnabled true to enable ripple effects, false to disable
	 */
	public final void setThumbRipplesEnabled(boolean isEnabled) {
		thumb.setRipplesEnabled(isEnabled);
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
	 * @param isEnabled true to enable hover effects, false to disable
	 */
	public final void setThumbHoverEnabled(boolean isEnabled) {
		thumb.setHoverEnabled(isEnabled);
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
	 * Sets the stroke color (border color) of the thumb.
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
	 * orientation. For horizontal scrollbars, returns width × thumbSizeRatio. For
	 * vertical scrollbars, returns height × thumbSizeRatio.
	 *
	 * @return the calculated thumb size in pixels
	 */
	public final float getCalculatedThumbSize() {
		return getOrientation() == Orientation.HORIZONTAL ? getWidth() * thumbSizeRatio : getHeight() * thumbSizeRatio;
	}

	/**
	 * Sets the ratio that determines the thumb size relative to the track. The
	 * thumbSizeRatio must be between 0 and 1 inclusive.
	 *
	 * @param thumbSizeRatio the ratio of thumb size to track size (0 to 1)
	 * @throws IllegalStateException if thumbSizeRatio is less than 0 or greater
	 *                               than 1
	 */
	public final void setThumbSizeRatio(float thumbSizeRatio) {
		if (thumbSizeRatio < 0 || thumbSizeRatio > 1) {
			throw new IllegalStateException("thumb ratio in scroll cannot be lower than zero or be greater than 1");
		}
		this.thumbSizeRatio = thumbSizeRatio;
	}

	/**
	 * Renders the scrollbar and its thumb. The rendering includes the track (from
	 * parent class) and the draggable thumb.
	 */
	@Override
	protected void render() {
		super.render();
		thumb.draw();
	}

	/**
	 * Called when the scrollbar's bounds change. Updates the thumb's position and
	 * dimensions to match the new bounds.
	 */
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
}