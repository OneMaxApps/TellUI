package microui.util;

import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;

/**
 * SpatialState the class of states for SpatialAnimator
 * @see SpatialView
 */
public final class SpatialState {
	private final float x, y, width, height;

	/**
	 * Constructs SpatialState with initialization
	 * 
	 * @param x the coordinate state
	 * @param y the coordinate state
	 * @param width the dimension state
	 * @param height the dimension state
	 */
	public SpatialState(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Constructs SpatialState with initialization of bounds from SpatialView object
	 * 
	 * @param source is current source for copy state
	 */
	public SpatialState(SpatialView source) {
		this(requireNonNull(source, "source").getX(), source.getY(), source.getWidth(), source.getHeight());
	}
	
	/**
	 * Constructs SpatialState with initialization of bounds from other SpatialState object
	 * 
	 * @param source is current source for copy state
	 */
	public SpatialState(SpatialState source) {
		this(requireNonNull(source, "source").getX(), source.getY(), source.getWidth(), source.getHeight());
	}

	/**
	 * Returns variable x
	 * 
	 * @return the current variable x
	 */
	public float getX() {
		return x;
	}

	/**
	 * Returns variable y
	 * 
	 * @return the current variable y
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns variable width
	 * 
	 * @return the current variable width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Returns variable height
	 * 
	 * @return the current variable height
	 */
	public float getHeight() {
		return height;
	}
	
}