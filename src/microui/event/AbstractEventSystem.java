package microui.event;

import static java.util.Objects.requireNonNull;

import microui.MicroUI;
import microui.core.base.ContentView;
import microui.core.base.SpatialView;
import microui.util.Metrics;

/**
 * Abstract base class for event detection systems in MicroUI. Provides
 * comprehensive mouse event detection for SpatialView components, including
 * hover, press, click, drag, and timing-based events.
 * <p>
 * This class implements a sophisticated event detection system that can track
 * various mouse interactions with configurable thresholds. It supports both
 * ContentView (with padding/margin) and basic SpatialView components.
 * </p>
 * 
 * @see SpatialView
 * @see ContentView
 * @see EventDetector
 */
public abstract class AbstractEventSystem {
	private SpatialView spatialView;
	private final EventDetector detector;

	/**
	 * Constructs an AbstractEventSystem for the specified SpatialView.
	 * 
	 * @param spatialView the SpatialView to monitor for events (cannot be null)
	 * @throws NullPointerException if spatialView is null
	 */
	public AbstractEventSystem(SpatialView spatialView) {
		this.spatialView = requireNonNull(spatialView, "spatialView");

		detector = new EventDetector();

		Metrics.register(this);
	}

	/**
	 * Updates event detection state. Should be called every frame to ensure
	 * accurate event detection.
	 */
	public void listen() {
		detector.update();
	}

	/**
	 * Returns the main event detector instance.
	 * 
	 * @return the EventDetector for this event system
	 */
	public final EventDetector getDetector() {
		return detector;
	}

	/**
	 * Returns the threshold for enter-long events (hover duration before trigger).
	 * 
	 * @return the enter-long threshold in milliseconds
	 */
	public final long getEnterLongThreshold() {
		return detector.enterLongDetector.getEnterLongThreshold();
	}

	/**
	 * Sets the threshold for enter-long events.
	 * 
	 * @param enterLongThreshold the threshold in milliseconds (must be ≥ 0)
	 * @throws IllegalArgumentException if threshold is less than 0
	 */
	public final void setEnterLongThreshold(long enterLongThreshold) {
		detector.enterLongDetector.setEnterLongThreshold(enterLongThreshold);
	}

	/**
	 * Returns the threshold for long-press events.
	 * 
	 * @return the long-press threshold in milliseconds
	 */
	public final long getLongPressThreshold() {
		return detector.longPressDetector.getLongPressThreshold();
	}

	/**
	 * Sets the threshold for long-press events.
	 * 
	 * @param longPressThreshold the threshold in milliseconds (must be ≥ 0)
	 * @throws IllegalArgumentException if threshold is less than 0
	 */
	public final void setLongPressThreshold(long longPressThreshold) {
		detector.longPressDetector.setLongPressThreshold(longPressThreshold);
	}

	/**
	 * Returns the threshold for leave-long events (time after leaving before
	 * trigger).
	 * 
	 * @return the leave-long threshold in milliseconds
	 */
	public final long getLeaveLongThreshold() {
		return detector.leaveLongDetector.getLeaveLongThreshold();
	}

	/**
	 * Sets the threshold for leave-long events.
	 * 
	 * @param leaveLongThreshold the threshold in milliseconds (must be ≥ 0)
	 * @throws IllegalArgumentException if threshold is less than 0
	 */
	public final void setLeaveLongThreshold(long leaveLongThreshold) {
		detector.leaveLongDetector.setLeaveLongThreshold(leaveLongThreshold);
	}

	/**
	 * Returns the threshold for double-click detection.
	 * 
	 * @return the double-click threshold in milliseconds
	 */
	public final long getDoubleClickThreshold() {
		return detector.doubleClickDetector.getThreshold();
	}

	/**
	 * Sets the threshold for double-click detection.
	 * 
	 * @param threshold the threshold in milliseconds (must be ≥ 0)
	 * @throws IllegalArgumentException if threshold is less than 0
	 */
	public final void setDoubleClickThreshold(long threshold) {
		detector.doubleClickDetector.setThreshold(threshold);
	}

	/**
	 * Internal class containing all event detection logic and sub-detectors.
	 */
	protected final class EventDetector {
		private static final long DEFAULT_THRESHOLD = 1000;
		private static final long DEFAULT_DOUBLE_CLICK_THRESHOLD = 200;

		private boolean hover, pressed;
		private final PressDetector pressDetector;
		private final ReleaseDetector releaseDetector;
		private final LongPressDetector longPressDetector;
		private final EnterDetector enterDetector;
		private final LeaveDetector leaveDetector;
		private final EnterLongDetector enterLongDetector;
		private final LeaveLongDetector leaveLongDetector;
		private final ClickDetector clickDetector;
		private final DoubleClickDetector doubleClickDetector;
		private final DragStartDetector dragStartDetector;
		private final DraggingDetector draggingDetector;
		private final DragEndDetector dragEndDetector;

		/**
		 * Constructs an EventDetector with all sub-detectors initialized.
		 */
		public EventDetector() {
			super();

			pressDetector = new PressDetector();
			releaseDetector = new ReleaseDetector();
			longPressDetector = new LongPressDetector();
			enterDetector = new EnterDetector();
			leaveDetector = new LeaveDetector();
			enterLongDetector = new EnterLongDetector();
			leaveLongDetector = new LeaveLongDetector();
			clickDetector = new ClickDetector();
			doubleClickDetector = new DoubleClickDetector();
			dragStartDetector = new DragStartDetector();
			draggingDetector = new DraggingDetector();
			dragEndDetector = new DragEndDetector();
		}

		/**
		 * Updates all detector states based on current input.
		 */
		public void update() {
			
			hover = isHover();
			pressed = isPressed();
			
		}

		/**
		 * Checks if a press event was detected in the current frame.
		 * 
		 * @return true if press detected, false otherwise
		 */
		public boolean isPress() {
			return pressDetector.isDetected();
		}

		/**
		 * Checks if a release event was detected in the current frame.
		 * 
		 * @return true if release detected, false otherwise
		 */
		public boolean isRelease() {
			return releaseDetector.isDetected();
		}

		/**
		 * Checks if a long-press event was detected.
		 * 
		 * @return true if long-press detected, false otherwise
		 */
		public boolean isLongPressed() {
			return longPressDetector.isDetected();
		}

		/**
		 * Checks if an enter (mouse enter) event was detected.
		 * 
		 * @return true if enter detected, false otherwise
		 */
		public boolean isEnter() {
			return enterDetector.isDetected();
		}

		/**
		 * Checks if a leave (mouse leave) event was detected.
		 * 
		 * @return true if leave detected, false otherwise
		 */
		public boolean isLeave() {
			return leaveDetector.isDetected();
		}

		/**
		 * Checks if an enter-long (hover for extended time) event was detected.
		 * 
		 * @return true if enter-long detected, false otherwise
		 */
		public boolean isEnterLong() {
			return enterLongDetector.isDetected();
		}

		/**
		 * Checks if a leave-long (left for extended time) event was detected.
		 * 
		 * @return true if leave-long detected, false otherwise
		 */
		public boolean isLeaveLong() {
			return leaveLongDetector.isDetected();
		}

		/**
		 * Checks if a click event was detected.
		 * 
		 * @return true if click detected, false otherwise
		 */
		public boolean isClicked() {
			return clickDetector.isDetected();
		}

		/**
		 * Checks if a double-click event was detected.
		 * 
		 * @return true if double-click detected, false otherwise
		 */
		public boolean isDoubleClicked() {
			return doubleClickDetector.isDetected();
		}

		/**
		 * Checks if a drag start event was detected.
		 * 
		 * @return true if drag start detected, false otherwise
		 */
		public boolean isDragStarted() {
			return dragStartDetector.isDetected();
		}

		/**
		 * Checks if a dragging event is currently active.
		 * 
		 * @return true if currently dragging, false otherwise
		 */
		public boolean isDragging() {
			return draggingDetector.isDetected();
		}

		/**
		 * Checks if a drag end event was detected.
		 * 
		 * @return true if drag end detected, false otherwise
		 */
		public boolean isDragEnd() {
			return dragEndDetector.isDetected();
		}

		/**
		 * Checks if the mouse is currently pressed on this component.
		 * 
		 * @return true if pressed, false otherwise
		 */
		public boolean isPressed() {
			return hover && MicroUI.getContext().mousePressed;
		}

		/**
		 * Checks if the mouse is currently released (not pressed) on this component.
		 * 
		 * @return true if released, false otherwise
		 */
		public boolean isReleased() {
			return !pressed;
		}

		/**
		 * Checks if the mouse is currently hovering over this component. Handles both
		 * ContentView (with padding) and basic SpatialView bounds.
		 * 
		 * @return true if hovering, false otherwise
		 */
		public boolean isHover() {

			int mx = MicroUI.getContext().mouseX;
			int my = MicroUI.getContext().mouseY;

			int x, y, w, h;

			if (spatialView instanceof ContentView c) {
				x = (int) c.getPadX();
				y = (int) c.getPadY();
				w = (int) c.getPadWidth();
				h = (int) c.getPadHeight();
			} else {
				x = (int) spatialView.getX();
				y = (int) spatialView.getY();
				w = (int) spatialView.getWidth();
				h = (int) spatialView.getHeight();
			}

			hover = (mx > x && mx < x + w && my > y && my < y + h);

			return hover;
		}

		private final class PressDetector {
			private boolean pressHookCalled;
			private long pressTime;

			public PressDetector() {
				super();
				pressTime = System.currentTimeMillis();
			}

			public boolean isDetected() {
				
				if (!MicroUI.getContext().mousePressed) {
					pressHookCalled = false;
				}

				if (!pressHookCalled && hover && pressed) {
					pressHookCalled = true;
					pressTime = System.currentTimeMillis();
					
					return PointerManager.request(spatialView);
				}
				return false;
			}

			public long getPressTime() {
				return pressTime;
			}

		}

		private final class ReleaseDetector {
			private boolean isReleaseHookCalled;
			private long releaseTime;

			public ReleaseDetector() {
				isReleaseHookCalled = true;
				releaseTime = System.currentTimeMillis();
			}

			public boolean isDetected() {
				if (pressed) {
					isReleaseHookCalled = false;
				}

				if (!MicroUI.getContext().mousePressed && !isReleaseHookCalled) {
					isReleaseHookCalled = true;
					releaseTime = System.currentTimeMillis();
					
					return PointerManager.request(spatialView);
				}

				return false;
			}

			public long getReleaseTime() {
				return releaseTime;
			}

		}

		private final class LongPressDetector {
			private final PressDetector pressDetectorInternal;
			private boolean isLongPressHookCalled;
			private long longPressThreshold;

			public LongPressDetector() {
				super();
				pressDetectorInternal = new PressDetector();
				longPressThreshold = DEFAULT_THRESHOLD;
			}

			public boolean isDetected() {
				pressDetectorInternal.isDetected();

				if (!MicroUI.getContext().mousePressed) {
					isLongPressHookCalled = false;
				}

				if (!isLongPressHookCalled && pressed
						&& System.currentTimeMillis() - pressDetectorInternal.getPressTime() >= longPressThreshold) {
					isLongPressHookCalled = true;
					
					return PointerManager.request(spatialView);
				}

				return false;
			}

			public long getLongPressThreshold() {
				return longPressThreshold;
			}

			public void setLongPressThreshold(long longPressThreshold) {
				if (longPressThreshold < 0) {
					throw new IllegalArgumentException("long press threshold cannot be less than 0");
				}

				this.longPressThreshold = longPressThreshold;
			}

		}

		private final class EnterDetector {
			private boolean isEnterHookCalled;
			private long enterTime;

			public boolean isDetected() {
				if (!hover) {
					isEnterHookCalled = false;
				}

				if (!isEnterHookCalled) {
					if (hover) {
						enterTime = System.currentTimeMillis();
						isEnterHookCalled = true;
						return true;
					}
				}

				return false;
			}

			public long getEnterTime() {
				return enterTime;
			}

		}

		private final class LeaveDetector {
			private boolean isLeaveHookCalled;
			private long leaveTime;

			public LeaveDetector() {
				super();
				isLeaveHookCalled = true;
			}

			public boolean isDetected() {
				if (hover) {
					isLeaveHookCalled = false;
				}

				if (!isLeaveHookCalled) {
					if (!hover) {
						isLeaveHookCalled = true;
						leaveTime = System.currentTimeMillis();
						return true;
					}
				}

				return false;
			}

			public long getLeaveTime() {
				return leaveTime;
			}

		}

		private final class EnterLongDetector {
			private final EnterDetector enterDetectorInternal;
			private boolean isEnterLongHookCalled;
			private long enterLongThreshold;

			public EnterLongDetector() {
				super();
				enterDetectorInternal = new EnterDetector();
				enterLongThreshold = DEFAULT_THRESHOLD;
				isEnterLongHookCalled = false;
			}

			public boolean isDetected() {
				enterDetectorInternal.isDetected();

				if (!hover) {
					isEnterLongHookCalled = false;
				}

				if (!isEnterLongHookCalled) {
					if (hover && System.currentTimeMillis()
							- enterDetectorInternal.getEnterTime() >= enterLongThreshold) {
						isEnterLongHookCalled = true;
						return true;
					}
				}

				return false;
			}

			public long getEnterLongThreshold() {
				return enterLongThreshold;
			}

			public void setEnterLongThreshold(long enterLongThreshold) {
				if (enterLongThreshold < 0) {
					throw new IllegalArgumentException("enter long threshold cannot be less than 0");
				}
				this.enterLongThreshold = enterLongThreshold;
			}

		}

		private final class LeaveLongDetector {
			private final LeaveDetector leaveDetectorInternal;
			private boolean isLeaveLongHookCalled;
			private long leaveLongThreshold;

			public LeaveLongDetector() {
				super();
				leaveDetectorInternal = new LeaveDetector();
				leaveLongThreshold = DEFAULT_THRESHOLD;
				isLeaveLongHookCalled = true;
			}

			public boolean isDetected() {
				leaveDetectorInternal.isDetected();

				if (hover) {
					isLeaveLongHookCalled = false;
				}

				if (!isLeaveLongHookCalled) {
					if (!hover && System.currentTimeMillis()
							- leaveDetectorInternal.getLeaveTime() >= leaveLongThreshold) {
						isLeaveLongHookCalled = true;
						return true;
					}
				}

				return false;
			}

			public long getLeaveLongThreshold() {
				return leaveLongThreshold;
			}

			public void setLeaveLongThreshold(long leaveLongThreshold) {
				if (leaveLongThreshold < 0) {
					throw new IllegalArgumentException("leave long threshold cannot be less than 0");
				}

				this.leaveLongThreshold = leaveLongThreshold;
			}

		}

		private final class ClickDetector {
			private boolean canCallHook;

			public boolean isDetected() {

				if (canCallHook && !pressed && hover) {
					canCallHook = false;
					
					if (!PointerManager.isOwner(spatialView)) {
						return false;
					}
					
					return true;
				}

				if (pressed) {
					PointerManager.request(spatialView);
					canCallHook = true;
				}

				if (!hover) {
					canCallHook = false;
				}

				return false;
			}
		}

		private final class DoubleClickDetector {
			private final ClickDetector clickDetectorInternal;
			private final ReleaseDetector releaseDetectorInternal;
			private long threshold;
			private int clickCount;

			public DoubleClickDetector() {
				super();
				threshold = DEFAULT_DOUBLE_CLICK_THRESHOLD;
				clickDetectorInternal = new ClickDetector();
				releaseDetectorInternal = new ReleaseDetector();
			}

			public boolean isDetected() {
				releaseDetectorInternal.isDetected();

				if (System.currentTimeMillis() - releaseDetectorInternal.getReleaseTime() > threshold) {
					clickCount = 0;
				}

				if (clickDetectorInternal.isDetected()) {
					clickCount++;
				}

				if (clickCount == 2) {
					clickCount = 0;
					return PointerManager.request(spatialView);
				}

				return false;
			}

			public long getThreshold() {
				return threshold;
			}

			public void setThreshold(long threshold) {
				if (threshold < 0) {
					throw new IllegalArgumentException("double click threshold cannot be less than 0");
				}
				this.threshold = threshold;
			}

		}

		private final class DragStartDetector {
			private boolean hookCalled;
			
			public boolean isDetected() {
				if (!MicroUI.getContext().mousePressed) {
					 hookCalled = false;
				}

				if (!hookCalled && pressed && isMouseMoved()) {
					hookCalled = true;
					return PointerManager.request(spatialView);
				}

				return false;
			}

			private boolean isMouseMoved() {
				int mx = MicroUI.getContext().mouseX;
				int my = MicroUI.getContext().mouseY;
				int pmx = MicroUI.getContext().pmouseX;
				int pmy = MicroUI.getContext().pmouseY;

				return mx != pmx || my != pmy;
			}
		}

		private final class DraggingDetector {
			private final DragStartDetector dragStartDetector;

			private boolean isDragging;

			public DraggingDetector() {
				super();
				dragStartDetector = new DragStartDetector();
			}

			public boolean isDetected() {
				if (dragStartDetector.isDetected()) {
					isDragging = true;
				}

				if (!MicroUI.getContext().mousePressed) {
					isDragging = false;
				}

				return isDragging && PointerManager.request(spatialView);
			}

		}

		private final class DragEndDetector {
			private final DragStartDetector dragStartDetector;
			private boolean isDragEnd;

			public DragEndDetector() {
				super();
				this.dragStartDetector = new DragStartDetector();
			}

			public boolean isDetected() {

				if (dragStartDetector.isDetected()) {
					isDragEnd = true;
				}

				if (!dragStartDetector.isDetected() && isDragEnd && !MicroUI.getContext().mousePressed) {
					isDragEnd = false;
					return PointerManager.isOwner(spatialView);
				}

				return false;
			}

		}
	}

}