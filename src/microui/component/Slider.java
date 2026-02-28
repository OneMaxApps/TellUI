package microui.component;

import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.LinearRangeControl;
import microui.core.base.SpatialView;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;
import microui.util.MathUtils;

/**
 * A slider component for selecting values within a linear range. The Slider
 * extends LinearRangeControl to provide a draggable interface with a visual
 * progress indicator that fills according to the current value.
 * 
 * <p>
 * The slider supports both horizontal and vertical orientations and provides
 * visual feedback through a progress rectangle that grows/shrinks as the value
 * changes.
 * </p>
 * 
 * <p>
 * Users can click or drag anywhere on the slider track to set the value, with
 * the progress indicator updating in real-time.
 * </p>
 */
public class Slider extends LinearRangeControl {
	private final Rect progress;

	/**
	 * Constructs a Slider with specified position and dimensions. The slider is
	 * initialized with a progress indicator and default value range.
	 *
	 * @param x the x-coordinate of the slider's top-left corner
	 * @param y the y-coordinate of the slider's top-left corner
	 * @param w the width of the slider
	 * @param h the height of the slider
	 */
	public Slider(float x, float y, float w, float h) {
		super(x, y, w, h);
		progress = new Rect(x, y, w, h);

		setValue(0, 100, 0);

		onDragging(() -> {
			recalculateProgressBounds();
		});

		onPress(() -> {
			recalculateProgressBounds();
		});
	}

	/**
	 * Constructs a Slider with default maximum size, centered on the screen. This
	 * is a convenience constructor for creating a centered slider.
	 */
	public Slider() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	/**
	 * Swaps the orientation of the slider (horizontal to vertical or vice versa).
	 * Updates the progress indicator accordingly.
	 */
	@Override
	public void swapOrientation() {
		super.swapOrientation();
		onChangeBounds();
	}

	/**
	 * Gets the stroke weight (border thickness) of the progress indicator.
	 *
	 * @return the current progress stroke weight
	 */
	public final float getProgressStrokeWeight() {
		return progress.stroke.getWeight();
	}

	/**
	 * Sets the stroke weight (border thickness) of the progress indicator.
	 *
	 * @param weight the stroke weight for the progress indicator border
	 */
	public final void setProgressStrokeWeight(int weight) {
		progress.stroke.setWeight(weight);
	}

	/**
	 * Gets the stroke color (border color) of the progress indicator.
	 *
	 * @return the current progress stroke color
	 */
	public final AbstractColor getProgressStrokeColor() {
		return progress.stroke.getColor();
	}

	/**
	 * Sets the stroke color (border color) of the progress indicator.
	 *
	 * @param color the color for the progress indicator border
	 */
	public final void setProgressStrokeColor(AbstractColor color) {
		progress.stroke.setColor(color);
	}

	/**
	 * Gets the fill color of the progress indicator.
	 *
	 * @return the current progress fill color
	 */
	public final AbstractColor getProgressColor() {
		return progress.color;
	}

	/**
	 * Sets the fill color of the progress indicator.
	 *
	 * @param color the fill color for the progress indicator
	 */
	public final void setProgressColor(AbstractColor color) {
		progress.color = color;
	}

	/**
	 * Renders the slider and its progress indicator. The rendering includes the
	 * track (from parent class) and the progress rectangle.
	 */
	@Override
	protected void render() {
		super.render();
		progress.draw();
	}

	/**
	 * Called when the slider's bounds change. Updates the progress indicator to
	 * match the new bounds.
	 */
	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		updateProgressBounds();
	}

	private void recalculateProgressBounds() {
		switch (getOrientation()) {
		case HORIZONTAL:
			setValue(MathUtils.convert(ctx.mouseX, getX(), getX() + getWidth(), getMinValue(), getMaxValue()));
			break;
		case VERTICAL:
			setValue(MathUtils.convert(ctx.mouseY, getY(), getY() + getHeight(), getMaxValue(), getMinValue()));
			break;
		}
		updateProgressBounds();

		onStartChangeValue();
		onChangeValue();
	}

	private final class Rect extends SpatialView {
		public final Stroke stroke;

		public AbstractColor color;

		private Rect(float x, float y, float w, float h) {
			super(x, y, w, h);
			setVisible(true);
			setNegativeDimensionsEnabled(true);

			stroke = new Stroke();
			stroke.setWeight(1);
			color = getTheme().getPrimaryColor();
		}

		@Override
		public void render() {
			ctx.pushStyle();
			stroke.apply();
			color.apply();
			ctx.rect(getX(), getY(), getWidth(), getHeight());
			ctx.popStyle();
		}
	}

	private void updateProgressBounds() {
		if (progress == null) {
			return;
		}

		progress.setBounds(getX(), getY(), getWidth(), getHeight());

		switch (getOrientation()) {
		case HORIZONTAL:
			progress.setWidth(MathUtils.convert(getValue(), getMinValue(), getMaxValue(), 0, getWidth()));
			break;

		case VERTICAL:
			progress.setY(getY() + getHeight());
			progress.setWidth(getWidth());
			progress.setHeight(MathUtils.convert(getValue(), getMinValue(), getMaxValue(), 0, -getHeight()));
			break;
		}
	}
}