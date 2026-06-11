package microui.core.interfaces;

import processing.event.KeyEvent;

/**
 * Interface for components that can respond to keyboard input events.
 * <p>
 * Implementing this interface allows components to receive and handle keyboard
 * events.
 * </p>
 */
public interface KeyPressable {

	/**
	 * Called when a key is pressed with detailed event information.
	 * <p>
	 * Implementing classes must provide an implementation to handle key press
	 * events.
	 * </p>
	 * 
	 * @param keyEvent the KeyEvent containing detailed information about the key press
	 */
	void keyPressed(KeyEvent keyEvent);
}