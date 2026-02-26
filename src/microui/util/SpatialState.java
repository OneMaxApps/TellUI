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
	
	public SpatialState() {
		this(0,0,0,0);
	}

	public void setSupplierX(Supplier<Float> supplierX) {
		this.supplierX = requireNonNull(supplierX);
	}

	public void setSupplierY(Supplier<Float> supplierY) {
		this.supplierY = requireNonNull(supplierY);
	}

	public void setSupplierWidth(Supplier<Float> supplierWidth) {
		this.supplierWidth = requireNonNull(supplierWidth);
	}

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