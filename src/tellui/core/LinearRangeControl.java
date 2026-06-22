package tellui.core;

import processing.event.MouseEvent;
import tellui.constants.Orientation;
import tellui.event.PointerManager;
import telluii.util.Environment;

/**
 * Abstract base class for linear range control components (sliders,
 * scroll-bars). Provides common functionality for components that allow
 * selecting a value along a linear axis, with support for orientation, mouse
 * wheel scrolling, and value change events.
 * 
 * @see RangeControl
 * @see Orientation
 */
public abstract class LinearRangeControl extends RangeControl {
	private final static int DEFAULT_MIN_WIDTH = 10;
	private final static int DEFAULT_MIN_HEIGHT = 20;
	private final static int DEFAULT_MAX_WIDTH = 200;
	private final static int DEFAULT_MAX_HEIGHT = 20;
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
		setMinMaxSize(DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);

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
		if (isHovered()) {
			getInternalScrolling().init(event);
			getInternalValue().append(getInternalScrolling().get());
		}

		notifyOnChangeValueListeners();
	}

	@Override
	public boolean isContentPrepared() {
		if (Environment.isAndroid() && isHovered() && !isPressed()) {
			return false;
		}
		
		if (!PointerManager.isOwner(this) && PointerManager.hasOwner()) {
			return false;
		}
		
		return isHovered() || getInternalScrolling().isScrolling() || isDragging();
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
		if (isMouseEnteredEvent() || additionalCondition) {
			getInternalScrolling().init(event);
			getInternalValue().append(getInternalScrolling().get());
		}
		notifyOnChangeValueListeners();
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
		
		setOrientation(orientation);
	}

	@Override
	protected void render() {
		super.render();
		ctx.pushStyle();
		getInternalStroke().apply();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();

		final boolean pressed = ctx.mousePressed;
		final var 	  scroll = getInternalScrolling();
		final boolean scrolling = scroll.isScrolling();
		final float   scrollSpeed = scroll.get();
		final var 	  value = getInternalValue();
		
		if (scrolling) {
			value.append(scrollSpeed);
			notifyOnChangeValueListeners();
			setEndedChangeValue(true);
			if (!pressed && !isStartedChangeValue()) {
				onStartChangeValue();
			}
		} else {
			if (!pressed) {
				setStartedChangeValue(false);
			}
		}

		if (!pressed && isEndedChangeValue() && !scrolling) {
			notifyOnEndChangeValueListeners();
			setEndedChangeValue(false);
		}
	}

	protected void onStartChangeValue() {
		if (!isStartedChangeValue()) {
			notifyOnStartChangeValueListeners();
			setStartedChangeValue(true);
		}
	}
}