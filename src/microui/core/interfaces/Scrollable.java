package microui.core.interfaces;

import processing.event.MouseEvent;

/**
 * Interface for components that can respond to mouse wheel scroll events.
 * <p>
 * Implementing this interface allows components to receive and handle
 * mouse wheel scrolling events from the Processing framework. This is
 * typically used for scrollable containers, lists, or any component
 * that needs to respond to vertical scrolling input.
 * </p>
 */
public interface Scrollable {
	
	/**
	 * Called when a mouse wheel event occurs.
	 * <p>
	 * Implementing classes must provide an implementation to handle
	 * mouse wheel scroll events. The MouseEvent provides detailed
	 * information about the scroll direction and amount.
	 * </p>
	 * 
	 * @param mouseEvent the MouseEvent containing detailed information about the scroll action
	 */
	public void mouseWheel(MouseEvent mouseEvent);

}