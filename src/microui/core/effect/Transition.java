package microui.core.effect;

import java.util.Objects;
import java.util.function.Supplier;

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
		final Supplier<Float> w = () -> (float) c.width;
		final Supplier<Float> nw = () -> (float) -c.width; // nw == negative width 
		
		final Supplier<Float> h = () -> (float) c.height;
		final Supplier<Float> nh = () -> (float) -c.height; // nh == negative height
		
		final Supplier<Float> zero = () -> 0f;
		
		SLIDE_LEFT = new Transition();
		SLIDE_LEFT.setPreviousStart(new SpatialState(zero,zero,w,h));
		SLIDE_LEFT.setPreviousEnd(new SpatialState(nw,zero,w,h));
		SLIDE_LEFT.setCurrentStart(new SpatialState(w,zero,w,h));
		SLIDE_LEFT.setCurrentEnd(new SpatialState(zero,zero,w,h));
		
		SLIDE_RIGHT = new Transition();
		SLIDE_RIGHT.setPreviousStart(new SpatialState(zero,zero,w,h));
		SLIDE_RIGHT.setPreviousEnd(new SpatialState(w,zero,w,h));
		SLIDE_RIGHT.setCurrentStart(new SpatialState(nw,zero,w,h));
		SLIDE_RIGHT.setCurrentEnd(new SpatialState(zero,zero,w,h));
		
		SLIDE_UP = new Transition();
		SLIDE_UP.setPreviousStart(new SpatialState(zero,zero,w,h));
		SLIDE_UP.setPreviousEnd(new SpatialState(zero,nh,w,h));
		SLIDE_UP.setCurrentStart(new SpatialState(zero,h,w,h));
		SLIDE_UP.setCurrentEnd(new SpatialState(zero,zero,w,h));
		
		SLIDE_DOWN = new Transition();
		SLIDE_DOWN.setPreviousStart(new SpatialState(zero,zero,w,h));
		SLIDE_DOWN.setPreviousEnd(new SpatialState(zero,h,w,h));
		SLIDE_DOWN.setCurrentStart(new SpatialState(zero,nh,w,h));
		SLIDE_DOWN.setCurrentEnd(new SpatialState(zero,zero,w,h));
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