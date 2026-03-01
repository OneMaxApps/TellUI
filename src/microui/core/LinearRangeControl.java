package microui.core;

import microui.constants.Orientation;
import microui.event.PointerManager;
import microui.util.Environment;
import processing.event.MouseEvent;

/**
 * Abstract base class for linear range control components (sliders,
 * scrollbars). Provides common functionality for components that allow
 * selecting a value along a linear axis, with support for orientation, mouse
 * wheel scrolling, and value change events.
 * 
 * @see RangeControl
 * @see Orientation
 */
public abstract class LinearRangeControl extends RangeControl {
	private Orientation orientation;

	/**
	 * Constructs a LinearRangeControl with specified position and dimensions.
	 * Initializes with horizontal orientation and default size constraints.
	 *
	 * @param x      the x-coordinate of the control's top-left corner
	 * @param y      the y-coordinate of the control's top-left corner
	 * @param width  the width of the control
	 * @param height the height of the control
	 */
	public LinearRangeControl(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinMaxSize(10, 20, 200, 20);

		getInternalValue().setOnChangeValueListener(() -> requestUpdate());

		onPress(() -> setEndedChangeValue(true));

		orientation = Orientation.HORIZONTAL;
	}

	/**
	 * Handles mouse wheel events for value adjustment. When the control is hovered,
	 * mouse wheel scrolling adjusts the value.
	 *
	 * @param event the mouse wheel event
	 */
	@Override
	public void mouseWheel(MouseEvent event) {
		if (isHover()) {
			getInternalScrolling().init(event);
			getInternalValue().append(getInternalScrolling().get());
		}

		onChangeValue();
	}

	@Override
	public boolean isContentPrepared() {
		if (Environment.isAndroid() && isHover() && !isPressed()) {
			return false;
		}
		
		if (!PointerManager.isOwner(this) && PointerManager.hasOwner()) {
			return false;
		}
		
		return isHover() || getInternalScrolling().isScrolling() || isDragging();
	}

	/**
	 * Handles mouse wheel events with an additional condition for value adjustment.
	 * Useful for nested controls or special interaction cases.
	 *
	 * @param event               the mouse wheel event
	 * @param additionalCondition additional condition that must be true for wheel
	 *                            to affect value
	 */
	public void mouseWheel(MouseEvent event, boolean additionalCondition) {
		if (isEnter() || additionalCondition) {
			getInternalScrolling().init(event);
			getInternalValue().append(getInternalScrolling().get());
		}
		onChangeValue();
	}

	/**
	 * Gets the current orientation of the control.
	 *
	 * @return the current orientation (HORIZONTAL or VERTICAL)
	 */
	public final Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Sets the orientation of the control and swaps dimensions accordingly. When
	 * changing from horizontal to vertical, width and height are swapped.
	 *
	 * @param orientation the new orientation to set
	 */
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

	/**
	 * Swaps the current orientation (horizontal ↔ vertical). Automatically swaps
	 * width and height dimensions.
	 */
	public void swapOrientation() {
		if (orientation == Orientation.HORIZONTAL) {
			orientation = Orientation.VERTICAL;
		} else {
			orientation = Orientation.HORIZONTAL;
		}
	}

	/**
	 * Renders the linear range control track and handles value change events. Draws
	 * the background track and manages the lifecycle of value change events (start,
	 * change, end) based on user interaction.
	 */
	@Override
	protected void render() {
		super.render();
		ctx.pushStyle();
		getInternalStroke().apply();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();

		if (getInternalScrolling().isScrolling()) {
			getInternalValue().append(getInternalScrolling().get());
			onChangeValue();
			setEndedChangeValue(true);
			if (!ctx.mousePressed && !isStartedChangeValue()) {
				onStartChangeValue();
			}
		} else {
			if (!ctx.mousePressed) {
				setStartedChangeValue(false);
			}
		}

		if (!ctx.mousePressed && isEndedChangeValue() && !getInternalScrolling().isScrolling()) {
			onEndChangeValue();
			setEndedChangeValue(false);
		}
	}

	/**
	 * Notifies the change value listener. Called internally when the value changes.
	 */
	protected void onChangeValue() {
		updateOnChangeValueListeners();
	}

	/**
	 * Notifies the start change value listener. Called internally when a value
	 * change begins. Ensures the start event is only fired once per interaction.
	 */
	protected void onStartChangeValue() {
		if (!isStartedChangeValue()) {
			updateOnStartChangeValueListeners();
			setStartedChangeValue(true);
		}
	}

	/**
	 * Notifies the end change value listener. Called internally when a value change
	 * ends.
	 */
	protected void onEndChangeValue() {
		updateOnEndChangeValueListeners();
	}

	/**
	 * Performs automatic scrolling by appending the current scrolling delta to the
	 * value. Also triggers the change value notification.
	 */
	protected final void autoScroll() {
		getInternalValue().append(getInternalScrolling().get());
		onChangeValue();
	}
}