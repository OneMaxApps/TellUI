package tellui.event;

import tellui.core.base.SpatialView;

/**
 * Concrete implementation of AbstractEventSystem providing direct access to all
 * mouse event states. This class serves as a convenient wrapper for detecting
 * various mouse interactions with SpatialView components.
 * <p>
 * The Event class provides simple boolean methods to check for different mouse
 * events without needing to access the internal detector directly. It's
 * designed for easy integration into component update loops.
 * </p>
 * 
 * @see AbstractEventSystem
 * @see SpatialView
 */
public final class Event extends AbstractEventSystem {

	/**
	 * Constructs an Event detector for the specified SpatialView.
	 * 
	 * @param spatialView the SpatialView to monitor for events (cannot be null)
	 */
	public Event(SpatialView spatialView) {
		super(spatialView);

	}

	/**
	 * Checks if a mouse press event occurred in the current frame.
	 * 
	 * @return true if mouse was pressed while hovering over the component
	 */
	public boolean isPress() {
		return getDetector().isPress();
	}

	/**
	 * Checks if a mouse release event occurred in the current frame.
	 * 
	 * @return true if mouse was released after being pressed on the component
	 */
	public boolean isRelease() {
		return getDetector().isRelease();
	}

	/**
	 * Checks if a long-press event occurred (mouse held for extended duration).
	 * 
	 * @return true if mouse was pressed and held beyond the long-press threshold
	 */
	public boolean isLongPressed() {
		return getDetector().isLongPressed();
	}

	/**
	 * Checks if a mouse enter event occurred (mouse moved into component bounds).
	 * 
	 * @return true if mouse entered the component's bounds in the current frame
	 */
	public boolean isEnter() {
		return getDetector().isEnter();
	}

	/**
	 * Checks if a mouse leave event occurred (mouse moved out of component bounds).
	 * 
	 * @return true if mouse left the component's bounds in the current frame
	 */
	public boolean isLeave() {
		return getDetector().isLeave();
	}

	/**
	 * Checks if an enter-long event occurred (mouse hovered for extended duration).
	 * 
	 * @return true if mouse hovered over component beyond the enter-long threshold
	 */
	public boolean isEnterLong() {
		return getDetector().isEnterLong();
	}

	/**
	 * Checks if a leave-long event occurred (mouse stayed out for extended
	 * duration).
	 * 
	 * @return true if mouse stayed out of component bounds beyond the leave-long
	 *         threshold
	 */
	public boolean isLeaveLong() {
		return getDetector().isLeaveLong();
	}

	/**
	 * Checks if a click event occurred (press and release while hovering).
	 * 
	 * @return true if a complete click action was performed on the component
	 */
	public boolean isClicked() {
		return getDetector().isClicked();
	}

	/**
	 * Checks if a double-click event occurred (two clicks within time threshold).
	 * 
	 * @return true if two clicks were detected within the double-click threshold
	 */
	public boolean isDoubleClicked() {
		return getDetector().isDoubleClicked();
	}

	/**
	 * Checks if a drag start event occurred (mouse moved while pressed).
	 * 
	 * @return true if mouse started dragging from this component
	 */
	public boolean isDragStarted() {
		return getDetector().isDragStarted();
	}

	/**
	 * Checks if the mouse is currently dragging this component.
	 * 
	 * @return true if mouse is currently dragging the component
	 */
	public boolean isDragging() {
		return getDetector().isDragging();
	}

	/**
	 * Checks if a drag end event occurred (mouse released after dragging).
	 * 
	 * @return true if mouse stopped dragging this component
	 */
	public boolean isDragEnd() {
		return getDetector().isDragEnd();
	}

	/**
	 * Checks if the mouse is currently pressed on this component.
	 * 
	 * @return true if mouse is currently pressed while hovering over the component
	 */
	public boolean isPressed() {
		return getDetector().isPressed();
	}

	/**
	 * Checks if the mouse is currently released (not pressed) on this component.
	 * 
	 * @return true if mouse is not pressed or not hovering over the component
	 */
	public boolean isReleased() {
		return getDetector().isReleased();
	}

	/**
	 * Checks if the mouse is currently hovering over this component.
	 * 
	 * @return true if mouse cursor is within the component's bounds
	 */
	public boolean isHover() {
		return getDetector().isHover();
	}
}