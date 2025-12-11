package microui.core.interfaces;

/**
 * Interface for components that have visibility states.
 * <p>
 * Implementing this interface allows components to be shown or hidden
 * dynamically. This is a fundamental interface for all visual elements
 * in the MicroUI framework that need visibility control.
 * </p>
 */
public interface Visible {

	/**
	 * Checks whether the component is currently visible.
	 * 
	 * @return true if the component is visible, false if hidden
	 */
	public boolean isVisible();

	/**
	 * Sets the visibility state of the component.
	 * 
	 * @param visible true to make the component visible, false to hide it
	 */
	public void setVisible(boolean visible);

}