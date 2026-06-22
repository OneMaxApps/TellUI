package tellui.layout;

import tellui.core.base.Container.Entry;
import tellui.core.exception.IllegalLayoutParamsException;
import tellui.core.exception.LayoutException;
import tellui.core.interfaces.LayoutParams;

/**
 * Row layout manager that arranges components horizontally.
 * 
 * <p>
 * A specialized linear layout that always arranges components in a horizontal
 * row. Components are positioned side by side from left to right, with widths
 * proportional to their weights.
 * </p>
 * 
 * <p>
 * This layout enforces weight constraints to ensure the total weight of all
 * components does not exceed the available horizontal space.
 * </p>
 */
public final class RowLayout extends LinearAxisLayout {

	/**
	 * Called when a new entry is added to the container.
	 * 
	 * <p>
	 * Validates that adding the new component does not cause the total weight to
	 * exceed available horizontal space. Throws an exception if the weight limit
	 * would be exceeded.
	 * </p>
	 * 
	 * @param contentViewEntry the entry being added to the container
	 * 
	 * @throws LayoutException if adding the entry would exceed the weight
	 *                               limit
	 */
	@Override
	public void onAdd(Entry contentViewEntry) {
		super.onAdd(contentViewEntry);
		if (isOutOfSpace()) {
			throw new LayoutException("Weight limit out of bounds in RowLayout");
		}
	}

	/**
	 * Constructs a RowLayout with horizontal orientation.
	 * 
	 * <p>
	 * Initializes the layout in horizontal mode by default.
	 * </p>
	 */
	public RowLayout() {
		super();
		setVerticalMode(false); // RowLayout is always horizontal
	}

	/**
	 * Validates that the provided layout parameters are of the correct type.
	 * 
	 * @param layoutParams the layout parameters to validate
	 * @throws IllegalLayoutParamsException if the parameters are not an instance of
	 *                                  RowLayoutParams
	 */
	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof RowLayoutParams)) {
			throw new IllegalLayoutParamsException(RowLayoutParams.class, layoutParams);
		}
	}
}