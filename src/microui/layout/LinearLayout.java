package microui.layout;

import microui.core.base.Container.Entry;

/**
 * Linear layout manager that arranges components in a single row or column.
 * 
 * <p>
 * This layout extends LinearAxisLayout and provides concrete implementation for
 * linear arrangements. Components are positioned sequentially along either the
 * vertical or horizontal axis based on their weights.
 * </p>
 * 
 * <p>
 * The layout enforces weight constraints to ensure the total weight of all
 * components does not exceed the available space.
 * </p>
 */
public class LinearLayout extends LinearAxisLayout {

	/**
	 * Called when a new entry is added to the container.
	 * 
	 * <p>
	 * Validates that adding the new component does not cause the total weight to
	 * exceed available space. Throws an exception if the weight limit would be
	 * exceeded.
	 * </p>
	 * 
	 * @param contentViewEntry the entry being added to the container
	 * 
	 * @throws IllegalStateException if adding the entry would exceed the weight
	 *                               limit
	 */
	@Override
	public void onAdd(Entry contentViewEntry) {
		super.onAdd(contentViewEntry);
		if (isOutOfSpace()) {
			throw new IllegalStateException("weight limit out of bounds in LinearLayout");
		}
	}

	/**
	 * Constructs a LinearLayout with specified orientation.
	 * 
	 * @param isVerticalMode true for vertical layout (components stacked), false
	 *                       for horizontal layout (components side by side)
	 */
	public LinearLayout(boolean isVerticalMode) {
		super();
		setVerticalMode(isVerticalMode);
	}

	/**
	 * Constructs a LinearLayout with default vertical orientation.
	 */
	public LinearLayout() {
		this(true);
	}

	/**
	 * Returns whether the layout is in vertical mode.
	 * 
	 * @return true if the layout is vertical, false if horizontal
	 */
	@Override
	public boolean isVerticalMode() {
		return super.isVerticalMode();
	}

	/**
	 * Sets the orientation mode of the layout.
	 * 
	 * @param isVerticalMode true for vertical layout, false for horizontal layout
	 */
	@Override
	public void setVerticalMode(boolean isVerticalMode) {
		super.setVerticalMode(isVerticalMode);
	}

	/**
	 * Validates that the provided layout parameters are of the correct type.
	 * 
	 * @param layoutParams the layout parameters to validate
	 * @throws IllegalArgumentException if the parameters are not an instance of
	 *                                  LinearLayoutParams
	 */
	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof LinearLayoutParams)) {
			throw new IllegalArgumentException("using not correct layout params for LinearLayoutParams");
		}
	}
}