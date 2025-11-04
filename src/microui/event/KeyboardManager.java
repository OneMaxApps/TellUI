package microui.event;

import microui.MicroUI;

public class KeyboardManager {
	private static final boolean[] keys = new boolean[Character.MAX_VALUE + 1];

	private KeyboardManager() {

	}

	public static final void keyPressed() {
		keys[MicroUI.getContext().key] = true;
		keys[MicroUI.getContext().keyCode] = true;
	}

	public static final boolean checkKeyPressed(int ch) {
		return keys[ch];
	}

	public static final void keyReleased() {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}
}