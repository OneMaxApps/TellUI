package microui.util;

import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;

/**
 * Utility class providing mathematical helper functions.
 * 
 * <p>
 * Contains static methods for common mathematical operations such as value
 * constraining, range conversion, collision detection, and distance
 * calculation.
 * </p>
 */
public final class MathUtils {

	private MathUtils() {
	}

	/**
	 * Constrains a value to a specified range.
	 * 
	 * <p>
	 * If the value is less than the minimum, returns the minimum. If the value is
	 * greater than the maximum, returns the maximum. Otherwise, returns the
	 * original value.
	 * </p>
	 * 
	 * @param value the value to constrain
	 * @param min   the minimum allowed value (inclusive)
	 * @param max   the maximum allowed value (inclusive)
	 * @return the constrained value within [min, max]
	 */
	public static final float constrain(float value, float min, float max) {
		return value < min ? min : value > max ? max : value;
	}

	/**
	 * Converts a value from one range to another.
	 * 
	 * <p>
	 * Linearly maps a value from the source range [min, max] to the target range
	 * [newMin, newMax].
	 * </p>
	 * 
	 * <p>
	 * Example: convert(50, 0, 100, 0, 1) returns 0.5
	 * </p>
	 * 
	 * @param value  the value to convert
	 * @param min    the minimum of the source range
	 * @param max    the maximum of the source range
	 * @param newMin the minimum of the target range
	 * @param newMax the maximum of the target range
	 * @return the converted value in the target range
	 */
	public static final float convert(float value, float min, float max, float newMin, float newMax) {
		return newMin + (newMax - newMin) * ((value - min) / (max - min));
	}

	/**
	 * Checks if two spatial views are colliding (overlapping).
	 * 
	 * <p>
	 * Performs axis-aligned bounding box (AABB) collision detection between two
	 * SpatialView objects based on their position and dimensions.
	 * </p>
	 * 
	 * @param spatialViewFirst  the first spatial view, cannot be null
	 * @param spatialViewSecond the second spatial view, cannot be null
	 * @return true if the views are overlapping, false otherwise
	 * 
	 * @throws NullPointerException if either spatial view is null
	 */
	public static final boolean isColliding(SpatialView spatialViewFirst, SpatialView spatialViewSecond) {
		SpatialView f = requireNonNull(spatialViewFirst, "spatialViewFirst");
		SpatialView s = requireNonNull(spatialViewSecond, "spatialViewSecond");

		return f.getX() + f.getWidth() > s.getX() && f.getX() < s.getX() + s.getWidth()
				&& f.getY() + f.getHeight() > s.getY() && f.getY() < s.getY() + s.getHeight();
	}

	/**
	 * Calculates the Euclidean distance between two points.
	 * 
	 * @param x  the x-coordinate of the first point
	 * @param y  the y-coordinate of the first point
	 * @param x1 the x-coordinate of the second point
	 * @param y1 the y-coordinate of the second point
	 * @return the distance between the two points
	 */
	public static final float dist(float x, float y, float x1, float y1) {
		final float dx = x1 - x;
		final float dy = y1 - y;

		return (float) Math.sqrt(dx * dx + dy * dy);
	}
}