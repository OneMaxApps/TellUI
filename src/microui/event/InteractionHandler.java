package microui.event;

import java.util.ArrayList;
import java.util.EnumMap;

import microui.core.base.SpatialView;

public final class InteractionHandler extends AbstractEventSystem {
	private boolean isEnabled;
	private final EventDispatcher dispatcher;

	public InteractionHandler(SpatialView spatialView) {
		super(spatialView);

		dispatcher = new EventDispatcher();

		setEnabled(true);
	}

	@Override
	public void listen() {
		if(!isEnabled()) {
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
	
	

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public void addListener(EventType eventType, Listener listener) {
		dispatcher.addListenerSafe(eventType, listener);
	}

	public void removeListener(EventType eventType, Listener listener) {
		dispatcher.removeListenerSafe(eventType, listener);
	}

	private final class EventDispatcher {
		private final EnumMap<EventType, ArrayList<Listener>> listeners;

		public EventDispatcher() {
			super();
			listeners = new EnumMap<>(EventType.class);
		}

		public void dispatch(EventType eventType) {
			if (listeners.get(eventType) != null) {
				listeners.get(eventType).forEach(Listener::action);
			}
		}

		public void addListenerSafe(EventType eventType, Listener listener) {
			if (eventType == null) {
				throw new NullPointerException("eventType cannot be null");
			}

			if (listener == null) {
				throw new NullPointerException("listener cannot be null");
			}

			listeners.putIfAbsent(eventType, new ArrayList<Listener>());
			listeners.get(eventType).add(listener);
		}

		public void removeListenerSafe(EventType eventType, Listener listener) {
			if (eventType == null) {
				throw new NullPointerException("eventType cannot be null");
			}

			if (listener == null) {
				throw new NullPointerException("listener cannot be null");
			}

			if (listeners.get(eventType) == null || !listeners.get(eventType).contains(listener)) {
				throw new IllegalStateException("listener is not found");
			}

			listeners.get(eventType).remove(listener);
		}
	}

}