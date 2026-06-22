package tellui.layout;

import static java.util.Objects.requireNonNull;

import java.util.List;

import processing.core.PApplet;
import tellui.TellUI;
import tellui.core.base.Container;
import tellui.core.base.Container.Entry;
import tellui.core.interfaces.LayoutParams;

/**
 * Abstract base class for layout managers that handle positioning and sizing of
 * components within a container.
 * 
 * <p>
 * Layout managers are responsible for calculating the positions and dimensions
 * of container entries based on their layout parameters and the available
 * space.
 * </p>
 */
public abstract class LayoutManager {
	protected PApplet ctx = TellUI.getContext();

	private Container container;
	private List<Container.Entry> entryList;

	/**
	 * Recalculates the layout of all entries in the container.
	 * 
	 * <p>
	 * This method should be implemented by subclasses to define how entries are
	 * positioned and sized within the container.
	 * </p>
	 */
	public abstract void recalculate();

	/**
	 * Draws debug information for the layout.
	 * 
	 * <p>
	 * This method can be overridden by subclasses to visualize layout calculations or
	 * boundaries.
	 * </p>
	 */
	public abstract void debugOnDraw();

	/**
	 * Called when a new entry is added to the container.
	 * 
	 * <p>
	 * This method validates the entry's layout parameters and triggers a layout
	 * recalculation.
	 * </p>
	 * 
	 * @param entry the entry that was added to the container
	 */
	public void onAdd(Entry entry) {
		checkCorrectParams(entry.getLayoutParams());
		recalculate();
	}

	/**
	 * Called when an entry is removed from the container.
	 * 
	 * <p>
	 * This method triggers a layout recalculation to account for the removed entry.
	 * </p>
	 */
	public void onRemove() {
		recalculate();
	}

	/**
	 * Returns the container managed by this layout manager.
	 * 
	 * @return the container instance
	 */
	public final Container getContainer() {
		return container;
	}

	/**
	 * Sets the container to be managed by this layout manager.
	 * 
	 * <p>
	 * This method also initializes the entry list from the container.
	 * </p>
	 * 
	 * @param container the container to manage
	 * @throws NullPointerException if the container is null
	 */
	public final void setContainer(Container container) {
		this.container = requireNonNull(container, "container");
		entryList = container.getEntryList();
	}

	/**
	 * Returns the list of entries in the managed container.
	 * 
	 * @return an unmodifiable view of the entry list
	 */
	protected final List<Entry> getEntryList() {
		return entryList;
	}

	/**
	 * Validates the correctness of layout parameters.
	 * 
	 * <p>
	 * This method should be implemented by subclasses to check if the provided
	 * layout parameters are valid for the specific layout manager implementation.
	 * </p>
	 * 
	 * @param layoutParams the layout parameters to validate
	 */
	protected abstract void checkCorrectParams(LayoutParams layoutParams);
}