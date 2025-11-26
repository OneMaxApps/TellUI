package microui.core;

import microui.constants.Orientation;
import microui.event.Listener;
import processing.event.MouseEvent;

public abstract class LinearRangeControl extends RangeControl {
	private Orientation orientation;
	private boolean valueChangeStart, valueChangeEnd;
	private Listener onStartChangeValueListener, onChangeValueListener, onEndChangeValueListener;

	public LinearRangeControl(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinMaxSize(10, 20, 200, 20);

		getMutableValue().setOnChangeValueListener(() -> requestUpdate());

		onPress(() -> valueChangeEnd = true);

		orientation = Orientation.HORIZONTAL;

	}

	@Override
	protected void render() {

		ctx.pushStyle();
		getMutableStroke().apply();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();

		if (getMutableScrolling().isScrolling()) {
			getMutableValue().append(getMutableScrolling().get());
			onChangeValue();
			valueChangeEnd = true;
			if (!ctx.mousePressed && !valueChangeStart) {
				onStartChangeValue();
			}
		} else {
			if (!ctx.mousePressed) {
				valueChangeStart = false;
			}
		}

		if (!ctx.mousePressed && valueChangeEnd && !getMutableScrolling().isScrolling()) {
			onEndChangeValue();
			valueChangeEnd = false;
		}
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		if (isHover()) {
			getMutableScrolling().init(event);
			getMutableValue().append(getMutableScrolling().get());
		}

		onChangeValue();
	}

	public void mouseWheel(MouseEvent event, boolean additionalCondition) {
		if (isEnter() || additionalCondition) {
			getMutableScrolling().init(event);
			getMutableValue().append(getMutableScrolling().get());
		}
		onChangeValue();
	}

	public final Orientation getOrientation() {
		return orientation;
	}

	public final void setOrientation(final Orientation orientation) {
		if (this.orientation == orientation) {
			return;
		}
		final float w = getWidth(), h = getHeight();
		this.orientation = orientation;

		setWidth(h);
		setHeight(w);
		requestUpdate();
	}

	public void swapOrientation() {
		if (orientation == Orientation.HORIZONTAL) {
			orientation = Orientation.VERTICAL;
		} else {
			orientation = Orientation.HORIZONTAL;
		}

	}

	public final Listener getOnChangeValueListener() {
		return onChangeValueListener;
	}

	public final void setOnChangeValueListener(Listener onChangeValueListener) {
		this.onChangeValueListener = onChangeValueListener;
	}

	public final Listener getOnStartChangeValueListener() {
		return onStartChangeValueListener;
	}

	public final void setOnStartChangeValueListener(Listener onStartChangeValueListener) {
		this.onStartChangeValueListener = onStartChangeValueListener;
	}

	public final Listener getOnEndChangeValueListener() {
		return onEndChangeValueListener;
	}

	public final void setOnEndChangeValueListener(Listener onEndChangeValueListener) {
		this.onEndChangeValueListener = onEndChangeValueListener;
	}

	protected void onChangeValue() {
		if (onChangeValueListener != null) {
			onChangeValueListener.action();
		}
	}

	protected void onStartChangeValue() {
		if (!valueChangeStart) {
			if (onStartChangeValueListener != null) {
				onStartChangeValueListener.action();
			}
			valueChangeStart = true;
		}
	}

	protected void onEndChangeValue() {
		if (onEndChangeValueListener != null) {
			onEndChangeValueListener.action();
		}
	}

	protected final void autoScroll() {
		getMutableValue().append(getMutableScrolling().get());
		onChangeValue();
	}
}