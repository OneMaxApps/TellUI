package microui.util;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import microui.core.base.SpatialView;

/**
 * Represents a spatial state consisting of position (x, y) and dimensions (width, height).
 * The state can be either fixed values or supplied dynamically via {@link Supplier}s.
 * This class is primarily used by {@link microui.core.effect.SpatialAnimator} to animate
 * transitions between different spatial configurations.
 *
 * @see SpatialView
 * @see microui.core.effect.SpatialAnimator
 */
public final class SpatialState {
	private final float x, y, width, height;
	private Supplier<Float> supplierX, supplierY, supplierWidth, supplierHeight;

	/**
	 * Constructs a SpatialState with fixed position and dimensions.
	 *
	 * @param x      the x-coordinate
	 * @param y      the y-coordinate
	 * @param width  the width
	 * @param height the height
	 */
	public SpatialState(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a SpatialState with fixed position and supplied dimensions.
	 *
	 * @param x      the fixed x-coordinate
	 * @param y      the fixed y-coordinate
	 * @param width  the supplier for width (must not be {@code null})
	 * @param height the supplier for height (must not be {@code null})
	 * @throws NullPointerException if {@code width} or {@code height} is {@code null}
	 */
	public SpatialState(float x, float y, Supplier<Float> width, Supplier<Float> height) {
		this(x, y, 0, 0);
		supplierWidth = requireNonNull(width, "width");
		supplierHeight = requireNonNull(height, "height");
	}

	/**
	 * Constructs a SpatialState with all four dimensions supplied.
	 *
	 * @param x      the supplier for x-coordinate (must not be {@code null})
	 * @param y      the supplier for y-coordinate (must not be {@code null})
	 * @param width  the supplier for width (must not be {@code null})
	 * @param height the supplier for height (must not be {@code null})
	 * @throws NullPointerException if any supplier is {@code null}
	 */
	public SpatialState(Supplier<Float> x, Supplier<Float> y, Supplier<Float> width, Supplier<Float> height) {
		this(0, 0, 0, 0);
		supplierX = requireNonNull(x, "x");
		supplierY = requireNonNull(y, "y");
		supplierWidth = requireNonNull(width, "width");
		supplierHeight = requireNonNull(height, "height");
	}

	/**
	 * Constructs a SpatialState by copying the bounds from a given {@link SpatialView}.
	 * The resulting state uses fixed values equal to the view's current position and size.
	 *
	 * @param source the SpatialView to copy from (must not be {@code null})
	 * @throws NullPointerException if {@code source} is {@code null}
	 */
	public SpatialState(SpatialView source) {
		this(requireNonNull(source, "source").getX(), source.getY(), source.getWidth(), source.getHeight());
	}

	/**
	 * Constructs a SpatialState by copying another SpatialState.
	 * The resulting state uses fixed values equal to the source's current values
	 * (any suppliers in the source are ignored, only the actual values are copied).
	 *
	 * @param source the SpatialState to copy from (must not be {@code null})
	 * @throws NullPointerException if {@code source} is {@code null}
	 */
	public SpatialState(SpatialState source) {
		this(requireNonNull(source, "source").getX(), source.getY(), source.getWidth(), source.getHeight());
	}

	/**
	 * Constructs a SpatialState with all dimensions set to zero.
	 */
	public SpatialState() {
		this(0, 0, 0, 0);
	}

	/**
	 * Sets a supplier for the X coordinate. When present, {@link #getX()} will use this supplier.
	 *
	 * @param supplierX the supplier for X, must not be {@code null}
	 * @throws NullPointerException if {@code supplierX} is {@code null}
	 */
	public void setSupplierX(Supplier<Float> supplierX) {
		this.supplierX = requireNonNull(supplierX);
	}

	/**
	 * Sets a supplier for the Y coordinate. When present, {@link #getY()} will use this supplier.
	 *
	 * @param supplierY the supplier for Y, must not be {@code null}
	 * @throws NullPointerException if {@code supplierY} is {@code null}
	 */
	public void setSupplierY(Supplier<Float> supplierY) {
		this.supplierY = requireNonNull(supplierY);
	}

	/**
	 * Sets a supplier for the width. When present, {@link #getWidth()} will use this supplier.
	 *
	 * @param supplierWidth the supplier for width, must not be {@code null}
	 * @throws NullPointerException if {@code supplierWidth} is {@code null}
	 */
	public void setSupplierWidth(Supplier<Float> supplierWidth) {
		this.supplierWidth = requireNonNull(supplierWidth);
	}

	/**
	 * Sets a supplier for the height. When present, {@link #getHeight()} will use this supplier.
	 *
	 * @param supplierHeight the supplier for height, must not be {@code null}
	 * @throws NullPointerException if {@code supplierHeight} is {@code null}
	 */
	public void setSupplierHeight(Supplier<Float> supplierHeight) {
		this.supplierHeight = requireNonNull(supplierHeight);
	}

	/**
	 * Returns the current x-coordinate. If a supplier is set, its value is returned;
	 * otherwise the fixed value given at construction is returned.
	 *
	 * @return the current x-coordinate
	 */
	public float getX() {
		return supplierX == null ? x : supplierX.get();
	}

	/**
	 * Returns the current y-coordinate. If a supplier is set, its value is returned;
	 * otherwise the fixed value given at construction is returned.
	 *
	 * @return the current y-coordinate
	 */
	public float getY() {
		return supplierY == null ? y : supplierY.get();
	}

	/**
	 * Returns the current width. If a supplier is set, its value is returned;
	 * otherwise the fixed value given at construction is returned.
	 *
	 * @return the current width
	 */
	public float getWidth() {
		return supplierWidth == null ? width : supplierWidth.get();
	}

	/**
	 * Returns the current height. If a supplier is set, its value is returned;
	 * otherwise the fixed value given at construction is returned.
	 *
	 * @return the current height
	 */
	public float getHeight() {
		return supplierHeight == null ? height : supplierHeight.get();
	}
}