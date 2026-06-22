package tellui.layout;

/**
 * Layout parameters specifically for ColumnLayout, defining how components are
 * positioned and sized within a vertical column arrangement.
 * <p>
 * ColumnLayoutParams extends LinearAxisLayoutParams to provide weight-based
 * vertical sizing and horizontal alignment control for components in a
 * ColumnLayout. The weight parameter determines the proportional vertical space
 * allocation, while alignX controls horizontal alignment within the column.
 * </p>
 * 
 * @see LinearAxisLayoutParams
 * @see ColumnLayout
 */
public final class ColumnLayoutParams extends LinearAxisLayoutParams {

	/**
	 * Constructs ColumnLayoutParams with specified weight and horizontal alignment.
	 * 
	 * @param weight the weight factor for vertical space allocation
	 * @param alignX the horizontal alignment within the column
	 */
	public ColumnLayoutParams(float weight, int alignX) {
		super(weight, alignX, 0);
	}

	/**
	 * Constructs ColumnLayoutParams with specified weight and default left
	 * alignment.
	 * 
	 * @param weight the weight factor for vertical space allocation
	 */
	public ColumnLayoutParams(float weight) {
		this(weight, 0);
	}

	/**
	 * Returns the horizontal alignment value. Overrides parent method to provide
	 * type-correct return value.
	 * 
	 * @return the horizontal alignment value
	 */
	@Override
	public int getAlignX() {
		return super.getAlignX();
	}

}