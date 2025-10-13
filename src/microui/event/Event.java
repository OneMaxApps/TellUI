package microui.event;

import microui.core.base.SpatialView;

public final class Event extends AbstractEventSystem {

	public Event(SpatialView spatialView) {
		super(spatialView);

	}

	public boolean isPress() {
		return getDetector().isPress();
	}

	public boolean isRelease() {
		return getDetector().isRelease();
	}

	public boolean isLongPress() {
		return getDetector().isLongPress();
	}

	public boolean isEnter() {
		return getDetector().isEnter();
	}

	public boolean isLeave() {
		return getDetector().isLeave();
	}

	public boolean isEnterLong() {
		return getDetector().isEnterLong();
	}

	public boolean isLeaveLong() {
		return getDetector().isLeaveLong();
	}

	public boolean isClick() {
		return getDetector().isClick();
	}

	public boolean isDoubleClick() {
		return getDetector().isDoubleClick();
	}

	public boolean isDragStart() {
		return getDetector().isDragStart();
	}

	public boolean isDragging() {
		return getDetector().isDragging();
	}

	public boolean isDragEnd() {
		return getDetector().isDragEnd();
	}

	public boolean isPressed() {
		return getDetector().isPressed();
	}

	public boolean isReleased() {
		return getDetector().isReleased();
	}

	public boolean isHover() {
		return getDetector().isHover();
	}
}