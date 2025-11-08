package microui.core.interfaces;

import processing.event.KeyEvent;

public interface KeyPressable {
	default void keyPressed() {
	}

	void keyPressed(KeyEvent e);
}