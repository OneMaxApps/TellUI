package microui.core.interfaces;

import processing.event.KeyEvent;

/**
 * Interface for components that can respond to keyboard input events.
 * <p>
 * Implementing this interface allows components to receive and handle keyboard
 * events from the Processing framework. Provides both a default no-operation
 * method for simple key presses and a method for detailed key events with
 * KeyEvent information.
 * </p>
 */
public interface KeyPressable {

	/**
	 * Called when a key is pressed (simple version).
	 * <p>
	 * This default implementation does nothing. Override to provide custom behavior
	 * for simple key press events without needing detailed event information.
	 * </p>
	 */
	default void keyPressed() {
	}

	/**
	 * Called when a key is pressed with detailed event information.
	 * <p>
	 * Implementing classes must provide an implementation to handle key press
	 * events. The KeyEvent provides detailed information about the key press
	 * including key code, modifiers, and timing.
	 * </p>
	 * 
	 * @param keyEvent the KeyEvent containing detailed information about the key press
	 */
	void keyPressed(KeyEvent keyEvent);
}