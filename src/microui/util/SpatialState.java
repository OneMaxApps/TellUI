package microui.util;

import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;

/**
 * Immutable record representing the spatial state of a view.
 * 
 * <p>
 * Contains position (x, y) and dimensions (width, height) information. Useful
 * for capturing and comparing spatial states at different points in time, or
 * for passing spatial data without exposing mutable objects.
 * </p>
 * 
 * @param x      the x-coordinate position
 * @param y      the y-coordinate position
 * @param width  the width dimension
 * @param height the height dimension
 */
public record SpatialState(float x, float y, float width, float height) {

	/**
	 * Constructs a SpatialState from a SpatialView.
	 * 
	 * <p>
	 * Extracts the spatial properties (position and dimensions) from the provided
	 * SpatialView object.
	 * </p>
	 * 
	 * @param source the SpatialView to copy spatial properties from, cannot be null
	 * @throws NullPointerException if source is null
	 */
	public SpatialState(SpatialView source) {
		this(requireNonNull(source, "source").getX(), source.getY(), source.getWidth(), source.getHeight());
	}

	/**
	 * Copy constructor for SpatialState.
	 * 
	 * <p>
	 * Creates a new SpatialState with the same values as the provided source.
	 * </p>
	 * 
	 * @param source the SpatialState to copy, cannot be null
	 * @throws NullPointerException if source is null
	 */
	public SpatialState(SpatialState source) {
		this(requireNonNull(source, "source").x(), source.y(), source.width(), source.height());
	}
}