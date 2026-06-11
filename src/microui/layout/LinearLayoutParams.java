package microui.layout;

/**
 * Layout parameters for LinearLayout.
 * 
 * <p>
 * Concrete implementation of LinearAxisLayoutParams specifically for use with
 * LinearLayout. Provides convenient constructors for common use cases.
 * </p>
 */
public class LinearLayoutParams extends LinearAxisLayoutParams {

	/**
	 * Constructs LinearLayoutParams with specified weight and alignment values.
	 * 
	 * @param weight the weight of the component (0.0 to 1.0)
	 * @param alignX horizontal alignment (-1 for left, 0 for center, 1 for right)
	 * @param alignY vertical alignment (-1 for top, 0 for center, 1 for bottom)
	 * 
	 */
	public LinearLayoutParams(float weight, int alignX, int alignY) {
		super(weight, alignX, alignY);
	}

	/**
	 * Constructs LinearLayoutParams with specified weight and uniform alignment.
	 * 
	 * <p>
	 * Uses the same alignment value for both horizontal and vertical axes.
	 * </p>
	 * 
	 * @param weight the weight of the component (0.0 to 1.0)
	 * @param align  alignment value used for both axes (-1 for left/top, 0 for
	 *               center, 1 for right/bottom)
	 * 
	 */
	public LinearLayoutParams(float weight, int align) {
		this(weight, align, align);
	}

	/**
	 * Constructs LinearLayoutParams with specified weight and center alignment.
	 * 
	 * <p>
	 * Uses center alignment (0) for both horizontal and vertical axes by default.
	 * </p>
	 * 
	 * @param weight the weight of the component (0.0 to 1.0)
	 * 
	 * @throws IllegalArgumentException if weight is outside the valid range (0.0 to
	 *                                  1.0)
	 */
	public LinearLayoutParams(float weight) {
		this(weight, 0);
	}

	/**
	 * Returns the horizontal alignment value.
	 * 
	 * @return the horizontal alignment value (-1 for left, 0 for center, 1 for
	 *         right)
	 */
	@Override
	public int getAlignX() {
		return super.getAlignX();
	}

	/**
	 * Returns the vertical alignment value.
	 * 
	 * @return the vertical alignment value (-1 for top, 0 for center, 1 for bottom)
	 */
	@Override
	public int getAlignY() {
		return super.getAlignY();
	}
}