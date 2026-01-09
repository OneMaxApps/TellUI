package microui.core.effect;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.convert;
import static microui.util.Timer.END;
import static microui.util.Timer.START;

import java.util.function.BooleanSupplier;

import microui.core.base.ContentView;
import microui.core.base.SpatialView;
import microui.util.SpatialState;
import microui.util.Timer;

/**
 * Animator for smooth interpolation of spatial properties (position and dimensions) between states.
 * Provides flexible animation control with configurable reaction modes and conditional triggering.
 * <p>
 * The SpatialAnimator animates a target SpatialView between defined start and end states,
 * with support for both SpatialState objects and ContentView references. It includes
 * configurable reaction modes (Reactive and Trigger) and separate controls for
 * position and dimension animations.
 * </p>
 * @see SpatialView
 * @see ContentView
 * @see SpatialState
 * @see Timer
 */
public final class SpatialAnimator {
	private final SpatialState startSpatialState, endSpatialState;
	private ContentView startContentView, endContentView;
	private final Timer timer;
	private final BooleanSupplier condition;
	private SpatialView targetSpatialView;
	private ReactionMode reactionMode;
	private boolean isEnabled;
	private boolean isPositionEnabled, isDimensionsEnabled;

	/**
	 * Constructs a SpatialAnimator with SpatialState objects and a condition.
	 * 
	 * @param startSpatialState the starting spatial state (cannot be null)
	 * @param endSpatialState the ending spatial state (cannot be null)
	 * @param condition the condition supplier for animation control (cannot be null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(SpatialState startSpatialState, SpatialState endSpatialState, BooleanSupplier condition) {
		super();
		
		this.startSpatialState = requireNonNull(startSpatialState,"startSpatialState");
		this.endSpatialState = requireNonNull(endSpatialState,"endSpatialState");
		this.condition = requireNonNull(condition,"condition");

		timer = new Timer();
		timer.setSpeed(.2f);
		
		setReactionMode(ReactionMode.REACTIVE);
		setEnabled(true);
		setPositionEnabled(true);
		setDimensionsEnabled(true);
	}

	/**
	 * Constructs a SpatialAnimator with a starting ContentView and ending SpatialState.
	 * 
	 * @param start the starting ContentView (cannot be null)
	 * @param end the ending spatial state (cannot be null)
	 * @param condition the condition supplier for animation control (cannot be null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(ContentView start, SpatialState end, BooleanSupplier condition) {
		this(new SpatialState(requireNonNull(start, "start").getX(),
				start.getY(), start.getAbsoluteWidth(), start.getAbsoluteHeight()), end, condition);
		startContentView = start;
	}

	/**
	 * Constructs a SpatialAnimator with a starting SpatialState and ending ContentView.
	 * 
	 * @param start the starting spatial state (cannot be null)
	 * @param end the ending ContentView (cannot be null)
	 * @param condition the condition supplier for animation control (cannot be null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(SpatialState start, ContentView end, BooleanSupplier condition) {
		this(start, new SpatialState(requireNonNull(end, "end").getX(),
				end.getY(), end.getAbsoluteWidth(), end.getAbsoluteHeight()), condition);
		endContentView = end;
	}

	/**
	 * Constructs a SpatialAnimator with both starting and ending ContentViews.
	 * 
	 * @param start the starting ContentView (cannot be null)
	 * @param end the ending ContentView (cannot be null)
	 * @param condition the condition supplier for animation control (cannot be null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(ContentView start, ContentView end, BooleanSupplier condition) {
		this(start, new SpatialState(requireNonNull(end, "end").getX(),
				end.getY(), end.getAbsoluteWidth(), end.getAbsoluteHeight()), condition);
		startContentView = start;
		endContentView = end;
	}

	/**
	 * Returns the current reaction mode.
	 * 
	 * @return the current reaction mode
	 */
	public ReactionMode getReactionMode() {
		return reactionMode;
	}

	/**
	 * Sets the reaction mode for animation triggering.
	 * 
	 * @param reactionMode the reaction mode to set (cannot be null)
	 * @return this SpatialAnimator for method chaining
	 * @throws NullPointerException if reactionMode is null
	 */
	public SpatialAnimator setReactionMode(ReactionMode reactionMode) {
		this.reactionMode = requireNonNull(reactionMode,"reactionMode");

		return this;
	}

	/**
	 * Checks if position animation is enabled.
	 * 
	 * @return true if position animation is enabled, false otherwise
	 */
	public boolean isPositionEnabled() {
		return isPositionEnabled;
	}

	/**
	 * Enables or disables position animation.
	 * 
	 * @param isPositionEnabled true to enable position animation, false to disable
	 * @return this SpatialAnimator for method chaining
	 */
	public SpatialAnimator setPositionEnabled(boolean isPositionEnabled) {
		this.isPositionEnabled = isPositionEnabled;

		return this;
	}

	/**
	 * Checks if dimension animation is enabled.
	 * 
	 * @return true if dimension animation is enabled, false otherwise
	 */
	public boolean isDimensionsEnabled() {
		return isDimensionsEnabled;
	}

	/**
	 * Enables or disables dimension animation.
	 * 
	 * @param isDimensionsEnabled true to enable dimension animation, false to disable
	 * @return this SpatialAnimator for method chaining
	 */
	public SpatialAnimator setDimensionsEnabled(boolean isDimensionsEnabled) {
		this.isDimensionsEnabled = isDimensionsEnabled;

		return this;
	}

	/**
	 * Returns the target SpatialView being animated.
	 * 
	 * @return the target SpatialView, or null if not set
	 */
	public SpatialView getTargetSpatialView() {
		return targetSpatialView;
	}

	/**
	 * Sets the target SpatialView to animate.
	 * 
	 * @param targetSpatialView the SpatialView to animate (cannot be null)
	 * @return this SpatialAnimator for method chaining
	 * @throws NullPointerException if targetSpatialView is null
	 */
	public SpatialAnimator setTargetSpatialView(SpatialView targetSpatialView) {
		this.targetSpatialView = requireNonNull(targetSpatialView,"targetSpatialView");

		return this;
	}

	/**
	 * Checks if the animator is enabled.
	 * 
	 * @return true if enabled, false if disabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Enables or disables the animator.
	 * 
	 * @param isEnabled true to enable the animator, false to disable
	 * @return this SpatialAnimator for method chaining
	 */
	public SpatialAnimator setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;

		return this;
	}

	/**
	 * Updates the animation based on current conditions and timer state.
	 * Interpolates position and dimensions between start and end states.
	 * Should be called every frame for smooth animation.
	 */
	public void update() {
		if (!isEnabled() || targetSpatialView == null) {
			return;
		}

		switch (getReactionMode()) {
		case REACTIVE:
			timer.setIncrementing(condition.getAsBoolean());
			break;

		case TRIGGER:
			if (timer.isComplete()) {
				timer.setIncrementing(condition.getAsBoolean());
			}
			break;
		}

		timer.update();

		if (isPositionEnabled()) {
			final float startX = startContentView != null ? startContentView.getAbsoluteX() : startSpatialState.x();
			final float endX = endContentView != null ? endContentView.getAbsoluteX() : endSpatialState.x();

			final float startY = startContentView != null ? startContentView.getAbsoluteY() : startSpatialState.y();
			final float endY = endContentView != null ? endContentView.getAbsoluteY() : endSpatialState.y();

			targetSpatialView.setX(lerp(startX, endX));
			targetSpatialView.setY(lerp(startY, endY));
		}

		if (isDimensionsEnabled()) {

			final float startWidth = startContentView != null ? startContentView.getAbsoluteWidth() : startSpatialState.width();
			final float endWidth = endContentView != null ? endContentView.getAbsoluteWidth() : endSpatialState.width();

			final float startHeight = startContentView != null ? startContentView.getAbsoluteHeight(): startSpatialState.height();
			final float endHeight = endContentView != null ? endContentView.getAbsoluteHeight() : endSpatialState.height();

			targetSpatialView.setWidth(lerp(startWidth, endWidth));
			targetSpatialView.setHeight(lerp(startHeight, endHeight));
		}

	}

	/**
	 * Reaction modes for controlling animation triggering behavior.
	 */
	public static enum ReactionMode {
		/** Animation direction changes immediately when condition changes. */
		REACTIVE, 
		/** Animation direction only changes when current animation completes. */
		TRIGGER;
	}

	private float lerp(float start, float end) {
		return convert(timer.getCurrent(), START, END, start, end);
	}
}