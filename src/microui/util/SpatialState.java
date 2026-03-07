package microui.util;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import microui.core.base.SpatialView;

/**
 * SpatialState the class of states for SpatialAnimator
 * @see SpatialView
 */
public final class SpatialState {
	private final float x, y, width, height;
	private Supplier<Float> supplierX, supplierY, supplierWidth, supplierHeight;
	
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
	
	public SpatialState(float x, float y, Supplier<Float> width, Supplier<Float> height) {
		this(x,y,0,0);
		supplierWidth = requireNonNull(width,"width");
		supplierHeight = requireNonNull(height,"height");
	}
	
	public SpatialState(Supplier<Float>  x, Supplier<Float> y, Supplier<Float> width, Supplier<Float> height) {
		this(0,0,0,0);
		supplierX = requireNonNull(x,"x");
		supplierY = requireNonNull(y,"y");
		supplierWidth = requireNonNull(width,"width");
		supplierHeight = requireNonNull(height,"height");
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
	 * Constructs a SpatialState with zero bounds.
	 */
	public SpatialState() {
		this(0,0,0,0);
	}

	/**
	 * Sets a supplier for the X coordinate. When present, {@link #getX()} will use this supplier.
	 *
	 * @param supplierX the supplier for X, cannot be null.
	 * @throws NullPointerException if supplierX is null.
	 */
	public void setSupplierX(Supplier<Float> supplierX) {
		this.supplierX = requireNonNull(supplierX);
	}

	/**
	 * Sets a supplier for the Y coordinate. When present, {@link #getY()} will use this supplier.
	 *
	 * @param supplierY the supplier for Y, cannot be null.
	 * @throws NullPointerException if supplierY is null.
	 */
	public void setSupplierY(Supplier<Float> supplierY) {
		this.supplierY = requireNonNull(supplierY);
	}

	/**
	 * Sets a supplier for the width. When present, {@link #getWidth()} will use this supplier.
	 *
	 * @param supplierWidth the supplier for width, cannot be null.
	 * @throws NullPointerException if supplierWidth is null.
	 */
	public void setSupplierWidth(Supplier<Float> supplierWidth) {
		this.supplierWidth = requireNonNull(supplierWidth);
	}

	/**
	 * Sets a supplier for the height. When present, {@link #getHeight()} will use this supplier.
	 *
	 * @param supplierHeight the supplier for height, cannot be null.
	 * @throws NullPointerException if supplierHeight is null.
	 */
	public void setSupplierHeight(Supplier<Float> supplierHeight) {
		this.supplierHeight = requireNonNull(supplierHeight);
	}

	/**
	 * Returns variable x
	 * 
	 * @return the current variable x
	 */
	public float getX() {
		return supplierX == null ? x : supplierX.get();
	}

	/**
	 * Returns variable y
	 * 
	 * @return the current variable y
	 */
	public float getY() {
		return supplierY == null ? y : supplierY.get();
	}

	/**
	 * Returns variable width
	 * 
	 * @return the current variable width
	 */
	public float getWidth() {
		return supplierWidth == null ? width : supplierWidth.get();
	}

	/**
	 * Returns variable height
	 * 
	 * @return the current variable height
	 */
	public float getHeight() {
		return supplierHeight == null ? height : supplierHeight.get();
	}
	
}