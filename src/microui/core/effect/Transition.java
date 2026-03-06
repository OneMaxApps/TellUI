package microui.core.effect;

import java.util.Objects;

import microui.MicroUI;
import microui.util.SpatialState;

public final class Transition {
	public static final Transition SLIDE_LEFT;
	public static final Transition SLIDE_RIGHT;
	public static final Transition SLIDE_UP;
	public static final Transition SLIDE_DOWN;
	
	private SpatialState currentStart, currentEnd, previousStart, previousEnd;

	static {
		final var c = MicroUI.getContext();
		final var w = c.width;
		final var h = c.height;
		
		SLIDE_LEFT = new Transition();
		SLIDE_LEFT.setPreviousStart(new SpatialState(0,0,w,h));
		SLIDE_LEFT.setPreviousEnd(new SpatialState(-w,0,w,h));
		SLIDE_LEFT.setCurrentStart(new SpatialState(w,0,w,h));
		SLIDE_LEFT.setCurrentEnd(new SpatialState(0,0,w,h));
		
		SLIDE_RIGHT = new Transition();
		SLIDE_RIGHT.setPreviousStart(new SpatialState(0,0,w,h));
		SLIDE_RIGHT.setPreviousEnd(new SpatialState(w,0,w,h));
		SLIDE_RIGHT.setCurrentStart(new SpatialState(-w,0,w,h));
		SLIDE_RIGHT.setCurrentEnd(new SpatialState(0,0,w,h));
		
		SLIDE_UP = new Transition();
		SLIDE_UP.setPreviousStart(new SpatialState(0,0,w,h));
		SLIDE_UP.setPreviousEnd(new SpatialState(0,-h,w,h));
		SLIDE_UP.setCurrentStart(new SpatialState(0,h,w,h));
		SLIDE_UP.setCurrentEnd(new SpatialState(0,0,w,h));
		
		SLIDE_DOWN = new Transition();
		SLIDE_DOWN.setPreviousStart(new SpatialState(0,0,w,h));
		SLIDE_DOWN.setPreviousEnd(new SpatialState(0,h,w,h));
		SLIDE_DOWN.setCurrentStart(new SpatialState(0,-h,w,h));
		SLIDE_DOWN.setCurrentEnd(new SpatialState(0,0,w,h));
	}
	
	public final SpatialState getCurrentStart() {
		return currentStart;
	}

	public final void setCurrentStart(SpatialState currentStart) {
		this.currentStart = Objects.requireNonNull(currentStart,"currentStart");
	}

	public final SpatialState getCurrentEnd() {
		return currentEnd;
	}

	public final void setCurrentEnd(SpatialState currentEnd) {
		this.currentEnd = Objects.requireNonNull(currentEnd,"currentEnd");
	}

	public final SpatialState getPreviousStart() {
		return previousStart;
	}

	public final void setPreviousStart(SpatialState previousStart) {
		this.previousStart = Objects.requireNonNull(previousStart,"previousStart");
	}

	public final SpatialState getPreviousEnd() {
		return previousEnd;
	}

	public final void setPreviousEnd(SpatialState previousEnd) {
		this.previousEnd = Objects.requireNonNull(previousEnd,"previousEnd");
	}
	
}