package tellui.core.effect;

import java.util.Objects;
import java.util.function.Supplier;

import tellui.TellUI;
import telluii.util.SpatialState;

/**
 * Represents a transition animation between two UI containers.
 * <p>
 * A transition defines the start and end spatial states (position and size)
 * for both the previous (outgoing) and current (incoming) containers.
 * The library provides four predefined slide transitions:
 * {@link #SLIDE_LEFT}, {@link #SLIDE_RIGHT}, {@link #SLIDE_UP}, {@link #SLIDE_DOWN}.
 * Custom transitions can be created by instantiating this class and setting
 * the four spatial states appropriately.
 * </p>
 * 
 * @see SpatialState
 */
public final class Transition {
	/**
	 * Slide transition where the new content enters from the left,
	 * pushing the old content out to the right.
	 */
	public static final Transition SLIDE_LEFT;
	
	/**
	 * Slide transition where the new content enters from the right,
	 * pushing the old content out to the left.
	 */
	public static final Transition SLIDE_RIGHT;
	
	/**
	 * Slide transition where the new content enters from the top,
	 * pushing the old content out to the bottom.
	 */
	public static final Transition SLIDE_UP;
	
	/**
	 * Slide transition where the new content enters from the bottom,
	 * pushing the old content out to the top.
	 */
	public static final Transition SLIDE_DOWN;
	
	private SpatialState currentStart, currentEnd, previousStart, previousEnd;

	static {
		final var c = TellUI.getContext();
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
	
	/**
	 * Returns the starting spatial state of the incoming (current) container.
	 *
	 * @return the current start state (never {@code null})
	 */
	public final SpatialState getCurrentStart() {
		return currentStart;
	}

	/**
	 * Sets the starting spatial state of the incoming (current) container.
	 *
	 * @param currentStart the start state (must not be {@code null})
	 * @throws NullPointerException if {@code currentStart} is {@code null}
	 */
	public final void setCurrentStart(SpatialState currentStart) {
		this.currentStart = Objects.requireNonNull(currentStart,"currentStart");
	}

	/**
	 * Returns the ending spatial state of the incoming (current) container.
	 *
	 * @return the current end state (never {@code null})
	 */
	public final SpatialState getCurrentEnd() {
		return currentEnd;
	}

	/**
	 * Sets the ending spatial state of the incoming (current) container.
	 *
	 * @param currentEnd the end state (must not be {@code null})
	 * @throws NullPointerException if {@code currentEnd} is {@code null}
	 */
	public final void setCurrentEnd(SpatialState currentEnd) {
		this.currentEnd = Objects.requireNonNull(currentEnd,"currentEnd");
	}

	/**
	 * Returns the starting spatial state of the outgoing (previous) container.
	 *
	 * @return the previous start state (never {@code null})
	 */
	public final SpatialState getPreviousStart() {
		return previousStart;
	}

	/**
	 * Sets the starting spatial state of the outgoing (previous) container.
	 *
	 * @param previousStart the start state (must not be {@code null})
	 * @throws NullPointerException if {@code previousStart} is {@code null}
	 */
	public final void setPreviousStart(SpatialState previousStart) {
		this.previousStart = Objects.requireNonNull(previousStart,"previousStart");
	}

	/**
	 * Returns the ending spatial state of the outgoing (previous) container.
	 *
	 * @return the previous end state (never {@code null})
	 */
	public final SpatialState getPreviousEnd() {
		return previousEnd;
	}

	/**
	 * Sets the ending spatial state of the outgoing (previous) container.
	 *
	 * @param previousEnd the end state (must not be {@code null})
	 * @throws NullPointerException if {@code previousEnd} is {@code null}
	 */
	public final void setPreviousEnd(SpatialState previousEnd) {
		this.previousEnd = Objects.requireNonNull(previousEnd,"previousEnd");
	}
	
}