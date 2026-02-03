package microui.event;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.EnumMap;

import microui.core.base.SpatialView;

/**
 * Advanced event handler that manages event listeners and dispatches events to
 * registered callbacks. Extends AbstractEventSystem to provide a comprehensive
 * event handling system with listener registration and event dispatching
 * capabilities.
 * <p>
 * The InteractionHandler not only detects mouse events but also manages
 * listener registration and dispatches events to appropriate callbacks when
 * events occur. It supports enabling/disabling the entire handler and provides
 * safe methods for adding/removing listeners.
 * </p>
 * 
 * @see AbstractEventSystem
 * @see EventType
 * @see Listener
 */
public final class InteractionHandler extends AbstractEventSystem {
	private boolean isEnabled;
	private final EventDispatcher dispatcher;

	/**
	 * Constructs an InteractionHandler for the specified SpatialView.
	 * 
	 * @param spatialView the SpatialView to monitor for events (cannot be null)
	 */
	public InteractionHandler(SpatialView spatialView) {
		super(spatialView);

		dispatcher = new EventDispatcher();

		setEnabled(true);
	}

	/**
	 * Updates event detection and dispatches events to registered listeners. This
	 * method should be called every frame for active event handling.
	 */
	@Override
	public void listen() {
		if (!isEnabled()) {
			return;
		}

		super.listen();

		if (getDetector().isPress()) {
			dispatcher.dispatch(EventType.PRESS);
		}

		if (getDetector().isPressed()) {
			dispatcher.dispatch(EventType.PRESSED);
		}

		if (getDetector().isRelease()) {
			dispatcher.dispatch(EventType.RELEASE);
		}

		if (getDetector().isReleased()) {
			dispatcher.dispatch(EventType.RELEASED);
		}

		if (getDetector().isLongPress()) {
			dispatcher.dispatch(EventType.LONG_PRESS);
		}

		if (getDetector().isEnter()) {
			dispatcher.dispatch(EventType.ENTER);
		}

		if (getDetector().isLeave()) {
			dispatcher.dispatch(EventType.LEAVE);
		}

		if (getDetector().isEnterLong()) {
			dispatcher.dispatch(EventType.ENTER_LONG);
		}

		if (getDetector().isLeaveLong()) {
			dispatcher.dispatch(EventType.LEAVE_LONG);
		}

		if (getDetector().isClick()) {
			dispatcher.dispatch(EventType.CLICK);
		}

		if (getDetector().isDoubleClick()) {
			dispatcher.dispatch(EventType.DOUBLE_CLICK);
		}

		if (getDetector().isDragStart()) {
			dispatcher.dispatch(EventType.DRAG_START);
		}

		if (getDetector().isDragging()) {
			dispatcher.dispatch(EventType.DRAGGING);
		}

		if (getDetector().isDragEnd()) {
			dispatcher.dispatch(EventType.DRAG_END);
		}

		if (getDetector().isHover()) {
			dispatcher.dispatch(EventType.HOVER);
		}

	}

	/**
	 * Checks if this interaction handler is enabled.
	 * 
	 * @return true if enabled, false if disabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Enables or disables this interaction handler. When disabled, no events will
	 * be detected or dispatched.
	 * 
	 * @param isEnabled true to enable, false to disable
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Adds a listener for a specific event type.
	 * 
	 * @param eventType the type of event to listen for (cannot be null)
	 * @param listener  the listener callback to call when event occurs (cannot be
	 *                  null)
	 * @throws NullPointerException if eventType or listener is null
	 */
	public void addListener(EventType eventType, Listener listener) {
		dispatcher.addListenerSafe(eventType, listener);
	}

	/**
	 * Removes a listener for a specific event type.
	 * 
	 * @param eventType the event type to remove listener from (cannot be null)
	 * @param listener  the listener to remove (cannot be null)
	 * @throws NullPointerException  if eventType or listener is null
	 * @throws IllegalStateException if the listener is not found for the given
	 *                               event type
	 */
	public void removeListener(EventType eventType, Listener listener) {
		dispatcher.removeListenerSafe(eventType, listener);
	}

	/**
	 * Internal class that manages event listeners and dispatches events to them.
	 */
	private final class EventDispatcher {
		/** Map of event types to their registered listeners. */
		private final EnumMap<EventType, ArrayList<Listener>> listeners;

		/**
		 * Constructs an EventDispatcher with empty listener maps.
		 */
		public EventDispatcher() {
			super();
			listeners = new EnumMap<>(EventType.class);
		}

		/**
		 * Dispatches an event to all registered listeners for that event type.
		 * 
		 * @param eventType the type of event to dispatch
		 */
		public void dispatch(EventType eventType) {
			if (listeners.get(eventType) != null) {
				listeners.get(eventType).forEach(Listener::action);
			}
		}

		/**
		 * Safely adds a listener for a specific event type. Creates the listener list
		 * if it doesn't exist.
		 * 
		 * @param eventType the event type to listen for (cannot be null)
		 * @param listener  the listener to add (cannot be null)
		 * @throws NullPointerException if eventType or listener is null
		 */
		public void addListenerSafe(EventType eventType, Listener listener) {
			requireNonNull(eventType, "eventType");
			requireNonNull(listener, "listener");

			listeners.putIfAbsent(eventType, new ArrayList<Listener>());
			listeners.get(eventType).add(listener);
		}

		/**
		 * Safely removes a listener for a specific event type.
		 * 
		 * @param eventType the event type to remove listener from (cannot be null)
		 * @param listener  the listener to remove (cannot be null)
		 * @throws NullPointerException  if eventType or listener is null
		 * @throws IllegalStateException if the listener is not found for the given
		 *                               event type
		 */
		public void removeListenerSafe(EventType eventType, Listener listener) {
			requireNonNull(eventType, "eventType");
			requireNonNull(listener, "listener");

			if (listeners.get(eventType) == null || !listeners.get(eventType).contains(listener)) {
				throw new IllegalStateException("listener is not found");
			}

			listeners.get(eventType).remove(listener);
		}
	}

}