package microui.layout;

/**
 * Layout parameters for RowLayout.
 * 
 * <p>Concrete implementation of LinearAxisLayoutParams specifically for use with
 * RowLayout. RowLayout always uses horizontal arrangement, so horizontal
 * alignment is fixed at center (0), while vertical alignment can be customized.</p>
 */
public final class RowLayoutParams extends LinearAxisLayoutParams {

    /**
     * Constructs RowLayoutParams with specified weight and vertical alignment.
     * 
     * <p>Horizontal alignment is always set to center (0) for RowLayout.</p>
     * 
     * @param weight the weight of the component (0.0 to 1.0)
     * @param alignY vertical alignment (-1 for top, 0 for center, 1 for bottom)
     * 
     * @throws IllegalArgumentException if any parameter is outside its valid range
     */
    public RowLayoutParams(float weight, int alignY) {
        super(weight, 0, alignY);
    }

    /**
     * Constructs RowLayoutParams with specified weight and center vertical alignment.
     * 
     * <p>Horizontal alignment is always center (0), vertical alignment defaults to center (0).</p>
     * 
     * @param weight the weight of the component (0.0 to 1.0)
     * 
     * @throws IllegalArgumentException if weight is outside the valid range (0.0 to 1.0)
     */
    public RowLayoutParams(float weight) {
        this(weight, 0);
    }

    /**
     * Returns the vertical alignment value.
     * 
     * <p>Note: Horizontal alignment is always center (0) for RowLayout and cannot be changed.</p>
     * 
     * @return the vertical alignment value (-1 for top, 0 for center, 1 for bottom)
     */
    @Override
    public int getAlignY() {
        return super.getAlignY();
    }
}