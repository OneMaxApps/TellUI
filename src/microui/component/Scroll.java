package microui.component;

import static microui.core.style.theme.ThemeManager.getTheme;
import static microui.util.Value.constrain;

import microui.constants.Orientation;
import microui.core.LinearRangeControl;
import microui.core.style.AbstractColor;
import microui.util.MathUtils;

public class Scroll extends LinearRangeControl {
	private final Button thumb;
	private float distToThumb, thumbSizeRatio;
	private boolean needRecalculateDistToThumb;

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

		setThumbSizeRatio(.1f);

		updateThumbTransforms();
		setValue(0, 100, 50);
	}

	public Scroll() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);

	}

	@Override
	protected void render() {
		super.render();
		thumb.draw();

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

	@Override
	public void swapOrientation() {
		super.swapOrientation();
		onChangeBounds();
	}

	public final boolean isThumbClicked() {
		return thumb.isClick();
	}

	public final AbstractColor getThumbRipplesColor() {
		return thumb.getRipplesColor();
	}

	public final void setThumbRipplesColor(AbstractColor color) {
		thumb.setRipplesColor(color);
	}

	public final boolean isThumbRipplesEnabled() {
		return thumb.isRipplesEnabled();
	}

	public final void setThumbRipplesEnabled(boolean isEnabled) {
		thumb.setRipplesEnabled(isEnabled);
	}

	public final boolean isThumbHoverEnabled() {
		return thumb.isHoverEnabled();
	}

	public final void setThumbHoverEnabled(boolean isEnabled) {
		thumb.setHoverEnabled(isEnabled);
	}

	public AbstractColor getThumbHoverColor() {
		return thumb.getHoverColor();
	}

	public void setThumbHoverColor(AbstractColor color) {
		thumb.setHoverColor(color);
	}

	public final float getThumbHoverSpeed() {
		return thumb.getHoverSpeed();
	}

	public final void setThumbHoverSpeed(float speed) {
		thumb.setHoverSpeed(speed);
	}

	public final float getThumbStrokeWeight() {
		return thumb.getStrokeWeight();
	}

	public final void setThumbStrokeWeight(int weight) {
		thumb.setStrokeWeight(weight);
	}

	public final AbstractColor getThumbStrokeColor() {
		return thumb.getStrokeColor();
	}

	public final void setThumbStrokeColor(AbstractColor color) {
		thumb.setStrokeColor(color);
	}

	public final float getCalculatedThumbSize() {
		return getOrientation() == Orientation.HORIZONTAL ? getWidth() * thumbSizeRatio : getHeight() * thumbSizeRatio;

	}

	public final void setThumbSizeRatio(float thumbSizeRatio) {
		if (thumbSizeRatio < 0 || thumbSizeRatio > 1) {
			throw new IllegalStateException("thumb ratio in scroll cannot be lower than zero or be greater than 1");
		}
		this.thumbSizeRatio = thumbSizeRatio;
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (thumb == null) {
			return;
		}
		updateThumbTransforms();

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