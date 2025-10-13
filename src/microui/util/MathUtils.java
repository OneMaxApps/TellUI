package microui.util;

import microui.core.base.SpatialView;

public final class MathUtils {

	private MathUtils() {
	}

	public static final float constrain(float value, float min, float max) {
		return value < min ? min : value > max ? max : value;
	}

	public static final float convert(float value, float min, float max, float newMin, float newMax) {
		return newMin + (newMax - newMin) * ((value - min) / (max - min));
	}

	public static final boolean isColliding(SpatialView spatialViewFirst, SpatialView spatialViewSecond) {
		if (spatialViewFirst == null) {
			throw new NullPointerException("the spatialViewFirst cannot be null");
		}

		if (spatialViewSecond == null) {
			throw new NullPointerException("the spatialViewSecond cannot be null");
		}

		SpatialView f = spatialViewFirst;
		SpatialView s = spatialViewSecond;

		return f.getX() + f.getWidth() > s.getX() && f.getX() < s.getX() + s.getWidth()
				&& f.getY() + f.getHeight() > s.getY() && f.getY() < s.getY() + s.getHeight();
	}

	public static final float dist(float x, float y, float x1, float y1) {
		final float dx = x1 - x;
		final float dy = y1 - y;

		return (float) Math.sqrt(dx * dx + dy * dy);
	}
}