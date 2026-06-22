package tellui.core;

import static java.util.Objects.requireNonNull;

import processing.core.PGraphics;
import tellui.core.interfaces.Visible;

/**
 * Abstract base class for views that render to an off-screen graphics buffer.
 * BufferedView provides a foundation for components that need to render to a
 * PGraphics buffer rather than directly to the main drawing surface.
 * 
 * <p>
 * This class implements the Visible interface to control rendering visibility
 * and provides a template method pattern for subclasses to implement their
 * specific rendering logic.
 * </p>
 * 
 * @see Visible
 */
public abstract class BufferedView implements Visible {
	private boolean visible;

	/**
	 * Renders the view to the specified graphics buffer if visible. This method
	 * checks visibility and delegates to the abstract render method.
	 *
	 * @param pGraphics the graphics buffer to render to
	 * @throws NullPointerException if pGraphics is null
	 */
	public void draw(PGraphics pGraphics) {
		if (isVisible()) {
			render(requireNonNull(pGraphics, "pGraphics"));
		}
	}

	/**
	 * Checks if the view is currently visible and should be rendered.
	 *
	 * @return true if the view is visible, false otherwise
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the visibility of the view. When invisible, the view will not be
	 * rendered even if draw() is called.
	 *
	 * @param visible true to make the view visible, false to hide it
	 */
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Abstract method for subclasses to implement their specific rendering logic.
	 * This method is only called when the view is visible.
	 *
	 * @param pGraphics the graphics buffer to render to
	 */
	protected abstract void render(PGraphics pGraphics);
}