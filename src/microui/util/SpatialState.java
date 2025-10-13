package microui.util;

import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;

public record SpatialState(float x, float y, float width, float height) {

	public SpatialState(SpatialView source) {
		this(requireNonNull(source, "the source object for SpatialState cannot be null").getX(), source.getY(),
				source.getWidth(), source.getHeight());
	}

	public SpatialState(SpatialState source) {
		this(requireNonNull(source, "the source object for SpatialState cannot be null").x(), source.y(),
				source.width(), source.height());
	}
}