package microui.layout;

import microui.core.base.Container.Entry;
import microui.core.interfaces.LayoutParams;

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
			throw new IllegalStateException("Weight limit out of bounds in LinearLayout");
		}
	}

	/**
	 * Constructs a LinearLayout with specified orientation.
	 * 
	 * @param verticalModeEnabled true for vertical layout (components stacked), false
	 *                       for horizontal layout (components side by side)
	 */
	public LinearLayout(boolean verticalModeEnabled) {
		super();
		setVerticalMode(verticalModeEnabled);
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
	 * @throws ClassCastException if the parameters are not an instance of
	 *                                  LinearLayoutParams
	 */
	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof LinearLayoutParams)) {
			throw new ClassCastException("Incorrect layout params: expected LinearLayoutParams but got " + layoutParams.getClass().getSimpleName());
		}
	}
}