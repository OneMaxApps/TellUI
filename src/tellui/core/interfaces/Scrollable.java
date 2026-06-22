package tellui.core.interfaces;

import processing.event.MouseEvent;

/**
 * Interface for components that can respond to mouse wheel scroll events.
 */
public interface Scrollable {

	/**
	 * Called when a mouse wheel event occurs.
	 * 
	 * @param mouseEvent the MouseEvent containing detailed information about the
	 *                   scroll action
	 */
	public void mouseWheel(MouseEvent mouseEvent);

}