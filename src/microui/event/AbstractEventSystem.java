package microui.event;

import static java.util.Objects.requireNonNull;

import microui.MicroUI;
import microui.core.base.Component;
import microui.core.base.SpatialView;
import microui.util.Metrics;

public abstract class AbstractEventSystem {
	private SpatialView spatialView;
	private final EventDetector detector;

	public AbstractEventSystem(SpatialView spatialView) {
		this.spatialView = requireNonNull(spatialView,"spatialView");

		detector = new EventDetector();

		Metrics.register(this);
	}

	public void listen() {
		detector.update();
	}

	public final EventDetector getDetector() {
		return detector;
	}

	public final long getEnterLongThreshold() {
		return detector.enterLongDetector.getEnterLongThreshold();
	}

	public final void setEnterLongThreshold(long enterLongThreshold) {
		detector.enterLongDetector.setEnterLongThreshold(enterLongThreshold);
	}

	public final long getLongPressThreshold() {
		return detector.longPressDetector.getLongPressThreshold();
	}

	public final void setLongPressThreshold(long longPressThreshold) {
		detector.longPressDetector.setLongPressThreshold(longPressThreshold);
	}

	public final long getLeaveLongThreshold() {
		return detector.leaveLongDetector.getLeaveLongThreshold();
	}

	public final void setLeaveLongThreshold(long leaveLongThreshold) {
		detector.leaveLongDetector.setLeaveLongThreshold(leaveLongThreshold);
	}

	public final long getDoubleClickThreshold() {
		return detector.doubleClickDetector.getThreshold();
	}

	public final void setDoubleClickThreshold(long threshold) {
		detector.doubleClickDetector.setThreshold(threshold);
	}

	protected final class EventDetector {
		private static final long DEFAULT_THRESHOLD = 1000;
		private static final long DEFAULT_DOUBLE_CLICK_THRESHOLD = 200;
		private boolean isHover, isPressed;

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

		public void update() {
			isHover = isHover();
			isPressed = isPressed();
		}

		public boolean isPress() {
			return pressDetector.isDetected();
		}

		public boolean isRelease() {
			return releaseDetector.isDetected();
		}

		public boolean isLongPress() {
			return longPressDetector.isDetected();
		}

		public boolean isEnter() {
			return enterDetector.isDetected();
		}

		public boolean isLeave() {
			return leaveDetector.isDetected();
		}

		public boolean isEnterLong() {
			return enterLongDetector.isDetected();
		}

		public boolean isLeaveLong() {
			return leaveLongDetector.isDetected();
		}

		public boolean isClick() {
			return clickDetector.isDetected();
		}

		public boolean isDoubleClick() {
			return doubleClickDetector.isDetected();
		}

		public boolean isDragStart() {
			return dragStartDetector.isDetected();
		}

		public boolean isDragging() {
			return draggingDetector.isDetected();
		}

		public boolean isDragEnd() {
			return dragEndDetector.isDetected();
		}

		public boolean isPressed() {
			return isHover && MicroUI.getContext().mousePressed;
		}

		public boolean isReleased() {
			return !isPressed;
		}

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

			isHover = (mx > x && mx < x + w && my > y && my < y + h);

			return isHover;
		}

		private final class PressDetector {
			private boolean isPressHookCalled;
			private long pressTime;

			public PressDetector() {
				super();
				pressTime = System.currentTimeMillis();
			}

			public boolean isDetected() {
				if (!MicroUI.getContext().mousePressed) {
					isPressHookCalled = false;
				}

				if (!isPressHookCalled && isHover && isPressed) {
					isPressHookCalled = true;
					pressTime = System.currentTimeMillis();
					return true;
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
				if (isPressed) {
					isReleaseHookCalled = false;
				}

				if (!MicroUI.getContext().mousePressed && !isReleaseHookCalled) {
					isReleaseHookCalled = true;
					releaseTime = System.currentTimeMillis();
					return true;
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

				if (!isLongPressHookCalled && isPressed
						&& System.currentTimeMillis() - pressDetectorInternal.getPressTime() >= longPressThreshold) {
					isLongPressHookCalled = true;
					return true;
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
				if (!isHover) {
					isEnterHookCalled = false;
				}

				if (!isEnterHookCalled) {
					if (isHover) {
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
				if (isHover) {
					isLeaveHookCalled = false;
				}

				if (!isLeaveHookCalled) {
					if (!isHover) {
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

				if (!isHover) {
					isEnterLongHookCalled = false;
				}

				if (!isEnterLongHookCalled) {
					if (isHover && System.currentTimeMillis()
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

				if (isHover) {
					isLeaveLongHookCalled = false;
				}

				if (!isLeaveLongHookCalled) {
					if (!isHover && System.currentTimeMillis()
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
			private boolean isCanCallHook;

			public boolean isDetected() {

				if (isCanCallHook && !isPressed && isHover) {
					isCanCallHook = false;
					return true;
				}

				if (isPressed) {
					isCanCallHook = true;
				}

				if (!isHover) {
					isCanCallHook = false;
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
				// background update state for local using
				releaseDetectorInternal.isDetected();

				if (System.currentTimeMillis() - releaseDetectorInternal.getReleaseTime() > threshold) {
					clickCount = 0;
				}

				if (clickDetectorInternal.isDetected()) {
					clickCount++;
				}

				if (clickCount == 2) {
					clickCount = 0;
					return true;
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
			private boolean isHookCalled;

			public boolean isDetected() {
				if (!MicroUI.getContext().mousePressed) {
					isHookCalled = false;
				}

				if (!isHookCalled && isPressed && isMouseMoved()) {
					isHookCalled = true;
					return true;
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

				return isDragging;
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
					return true;
				}

				return false;
			}

		}
	}

}