package tellui.layout;

import tellui.core.base.Container.Entry;
import tellui.core.exception.IllegalLayoutParamsException;
import tellui.core.exception.LayoutException;
import tellui.core.interfaces.LayoutParams;

/**
 * Layout manager that arranges child components in a vertical column. Extends
 * LinearAxisLayout to provide vertical stacking of components with weight-based
 * sizing and spacing control.
 * <p>
 * The ColumnLayout positions components from top to bottom, allocating vertical
 * space based on each component's weight parameter. Components are arranged
 * sequentially with no horizontal overlap.
 * </p>
 * 
 * @see LinearAxisLayout
 * @see ColumnLayoutParams
 * @see Entry
 */
public final class ColumnLayout extends LinearAxisLayout {

	/**
	 * Called when a new entry is added to the container. Validates that there is
	 * sufficient space for the new component.
	 * 
	 * @param contentViewEntry the entry being added
	 * @throws LayoutException if weight limit is exceeded (out of bounds)
	 */
	@Override
	public void onAdd(Entry contentViewEntry) {
		super.onAdd(contentViewEntry);
		if (isOutOfSpace()) {
			throw new LayoutException("Weight limit out of bounds in ColumnLayout");
		}
	}

	/**
	 * Constructs a ColumnLayout with vertical orientation enabled.
	 */
	public ColumnLayout() {
		super();
		setVerticalMode(true);
	}

	/**
	 * Validates that the provided LayoutParams are of the correct type for
	 * ColumnLayout.
	 * 
	 * @param layoutParams the layout parameters to validate
	 * @throws IllegalLayoutParamsException if layoutParams is not an instance of
	 *                                  ColumnLayoutParams
	 */
	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof ColumnLayoutParams)) {
			throw new IllegalLayoutParamsException(ColumnLayoutParams.class, layoutParams);
		}
	}

}