package microui.event;

/**
 * Enumeration defining all possible mouse event types detected by the MicroUI event system.
 * Each event type is categorized as either EDGE (triggered once per occurrence) or
 * LEVEL (continuously active while condition is true).
 * <p>
 * Event types are used to specify which events to listen for in component event handlers.
 * EDGE events fire once when the event occurs, while LEVEL events remain active as long
 * as the condition persists.
 * </p>
 */
public enum EventType {
	/**
	 * Mouse press event (EDGE).
	 * Triggered once when mouse button is pressed while hovering over component.
	 * [EDGE type - calls only once per occurrence]
	 */
	PRESS,
	
	/**
	 * Mouse pressed state (LEVEL).
	 * Continuously true while mouse button is pressed and hovering over component.
	 * [LEVEL type - calls always without hooks logic]
	 */
	PRESSED,
	
	/**
	 * Mouse release event (EDGE).
	 * Triggered once when mouse button is released after being pressed on component.
	 * [EDGE type - calls only once per occurrence]
	 */
	RELEASE,
	
	/**
	 * Mouse released state (LEVEL).
	 * Continuously true while mouse button is not pressed or not hovering.
	 * [LEVEL type - calls always without hooks logic]
	 */
	RELEASED,

	/**
	 * Long press event (EDGE).
	 * Triggered once when mouse is held down beyond the long-press threshold.
	 * [EDGE type - calls only once per occurrence]
	 */
	LONG_PRESS,

	/**
	 * Mouse enter event (EDGE).
	 * Triggered once when mouse cursor enters the component's bounds.
	 * [EDGE type - calls only once per occurrence]
	 */
	ENTER,
	
	/**
	 * Mouse leave event (EDGE).
	 * Triggered once when mouse cursor leaves the component's bounds.
	 * [EDGE type - calls only once per occurrence]
	 */
	LEAVE,
	
	/**
	 * Enter long event (EDGE).
	 * Triggered once when mouse hovers over component beyond enter-long threshold.
	 * [EDGE type - calls only once per occurrence]
	 */
	ENTER_LONG,
	
	/**
	 * Leave long event (EDGE).
	 * Triggered once when mouse stays out of component beyond leave-long threshold.
	 * [EDGE type - calls only once per occurrence]
	 */
	LEAVE_LONG,

	/**
	 * Click event (EDGE).
	 * Triggered once when mouse is pressed and released while hovering (complete click).
	 * [EDGE type - calls only once per occurrence]
	 */
	CLICK,
	
	/**
	 * Double click event (EDGE).
	 * Triggered once when two clicks occur within the double-click time threshold.
	 * [EDGE type - calls only once per occurrence]
	 */
	DOUBLE_CLICK,

	/**
	 * Drag start event (EDGE).
	 * Triggered once when mouse starts dragging (moves while pressed).
	 * [EDGE type - calls only once per occurrence]
	 */
	DRAG_START,
	
	/**
	 * Dragging state (LEVEL).
	 * Continuously true while mouse is dragging the component.
	 * [LEVEL type - calls always without hooks logic]
	 */
	DRAGGING,
	
	/**
	 * Drag end event (EDGE).
	 * Triggered once when mouse stops dragging (released after dragging).
	 * [EDGE type - calls only once per occurrence]
	 */
	DRAG_END,

	/**
	 * Hover state (LEVEL).
	 * Continuously true while mouse cursor is within the component's bounds.
	 * [LEVEL type - calls always without hooks logic]
	 */
	HOVER;
}