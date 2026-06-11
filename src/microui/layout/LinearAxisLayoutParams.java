package microui.layout;

import microui.core.interfaces.LayoutParams;

/**
 * Abstract base class for layout parameters used in linear axis layouts.
 * 
 * <p>
 * Defines parameters that control how components are positioned and sized in
 * linear axis layouts (both vertical and horizontal). Parameters include weight
 * distribution and alignment values.
 * </p>
 */
public abstract class LinearAxisLayoutParams implements LayoutParams {
	private final float weight;
	private final int alignX;
	private final int alignY;

	/**
	 * Constructs a new LinearAxisLayoutParams with specified weight and alignment.
	 * 
	 * <p>
	 * Validates all parameters to ensure they are within acceptable ranges.
	 * </p>
	 * 
	 * @param weight the weight of the component (0.0 to 1.0)
	 * @param alignX horizontal alignment (-1 for left, 0 for center, 1 for right)
	 * @param alignY vertical alignment (-1 for top, 0 for center, 1 for bottom)
	 * 
	 * @throws IllegalArgumentException if any parameter is outside its valid range
	 */
	public LinearAxisLayoutParams(float weight, int alignX, int alignY) {
		super();

		if (weight < 0) {
			throw new IllegalArgumentException("weight cannot be lower than zero");
		}

		if (weight > 1) {
			throw new IllegalArgumentException("weight cannot be greater than 1");
		}

		if (alignX < -1) {
			throw new IllegalArgumentException("alignX cannot be lower than -1");
		}

		if (alignX > 1) {
			throw new IllegalArgumentException("alignX cannot be greater than 1");
		}

		if (alignY < -1) {
			throw new IllegalArgumentException("alignY cannot be lower than -1");
		}

		if (alignY > 1) {
			throw new IllegalArgumentException("alignY cannot be greater than 1");
		}

		this.weight = weight;
		this.alignX = alignX;
		this.alignY = alignY;
	}

	/**
	 * Returns the weight of the component.
	 * 
	 * <p>
	 * The weight determines how much space the component occupies relative to other
	 * components in the layout. The total weight of all components should typically
	 * sum to 1.0.
	 * </p>
	 * 
	 * @return the weight value (0.0 to 1.0)
	 */
	public final float getWeight() {
		return weight;
	}

	/**
	 * Returns the horizontal alignment value.
	 * 
	 * <p>
	 * Interpretation of alignment values:
	 * <ul>
	 * <li>-1: Left alignment</li>
	 * <li>0: Center alignment</li>
	 * <li>1: Right alignment</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the horizontal alignment value (-1, 0, or 1)
	 */
	protected int getAlignX() {
		return alignX;
	}

	/**
	 * Returns the vertical alignment value.
	 * 
	 * <p>
	 * Interpretation of alignment values:
	 * <ul>
	 * <li>-1: Top alignment</li>
	 * <li>0: Center alignment</li>
	 * <li>1: Bottom alignment</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the vertical alignment value (-1, 0, or 1)
	 */
	protected int getAlignY() {
		return alignY;
	}
}