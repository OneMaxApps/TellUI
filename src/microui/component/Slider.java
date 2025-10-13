package microui.component;

import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.LinearRangeControl;
import microui.core.base.SpatialView;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;
import microui.util.MathUtils;

public class Slider extends LinearRangeControl {

	private final Rect progress;

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

	public Slider() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	@Override
	protected void render() {
		super.render();
		progress.draw();

	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		updateProgressBounds();
	}

	@Override
	public void swapOrientation() {
		super.swapOrientation();
		onChangeBounds();
	}

	public final float getProgressStrokeWeight() {
		return progress.stroke.getWeight();
	}

	public final void setProgressStrokeWeight(int weight) {
		progress.stroke.setWeight(weight);
	}

	public final AbstractColor getProgressStrokeColor() {
		return progress.stroke.getColor();
	}

	public final void setProgressStrokeColor(AbstractColor color) {
		progress.stroke.setColor(color);
	}

	public final AbstractColor getProgressColor() {
		return progress.color;
	}

	public final void setProgressColor(AbstractColor color) {
		progress.color = color;
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