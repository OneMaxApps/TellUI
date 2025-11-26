package microui.core;

import static java.util.Objects.requireNonNull;

import microui.core.interfaces.Visible;
import processing.core.PGraphics;

public abstract class BufferedView implements Visible {
	private boolean visible;
	
	public void draw(PGraphics pGraphics) {
		if (isVisible()) {
			render(requireNonNull(pGraphics,"pGraphics"));
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	protected abstract void render(PGraphics pGraphics);
	
}