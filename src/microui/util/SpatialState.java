package microui.util;

import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;

// TODO write JavaDocs
public final class SpatialState {
	private final float x, y, width, height;

	public SpatialState(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public SpatialState(SpatialView source) {
		this(requireNonNull(source, "source").getX(), source.getY(), source.getWidth(), source.getHeight());
	}
	
	public SpatialState(SpatialState source) {
		this(requireNonNull(source, "source").x(), source.y(), source.width(), source.height());
	}

	public float x() {
		return x;
	}

	public float y() {
		return y;
	}

	public float width() {
		return width;
	}

	public float height() {
		return height;
	}
	
	
}