package microui.event;

/**
 * Functional interface for event listeners in the MicroUI framework.
 * <p>
 * Listeners are callback objects that can be registered with event handlers
 * to respond to specific events. When an event occurs, the {@link #action()}
 * method is called on all registered listeners for that event type.
 * </p>
 * <p>
 * This interface is designed to be implemented using lambda expressions,
 * method references, or anonymous classes for concise event handling.
 * </p>
 * @see InteractionHandler
 */
public interface Listener {
	
	/**
	 * Called when the event this listener is registered for occurs.
	 * <p>
	 * Implementing classes should provide the action to perform when
	 * the corresponding event is triggered.
	 * </p>
	 */
	void action();
}