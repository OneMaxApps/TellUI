package tellui.core.effect;

import static java.util.Objects.requireNonNull;
import static telluii.util.MathUtils.convert;
import static telluii.util.Timer.END;
import static telluii.util.Timer.START;

import java.util.function.BooleanSupplier;

import tellui.core.base.ContentView;
import tellui.core.base.SpatialView;
import telluii.util.SpatialState;
import telluii.util.Timer;

/**
 * Animator for smooth interpolation of spatial properties (position and
 * dimensions) between states. Provides flexible animation control with
 * configurable reaction modes and conditional triggering.
 * <p>
 * The SpatialAnimator animates a target SpatialView between defined start and
 * end states, with support for both SpatialState objects and ContentView
 * references. It includes configurable reaction modes (Reactive and Trigger)
 * and separate controls for position and dimension animations.
 * </p>
 * 
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
	private boolean enabled;
	private boolean positionEnabled, dimensionsEnabled;

	/**
	 * Constructs a SpatialAnimator with SpatialState objects and a condition.
	 * 
	 * @param startSpatialState the starting spatial state (cannot be null)
	 * @param endSpatialState   the ending spatial state (cannot be null)
	 * @param condition         the condition supplier for animation control (cannot
	 *                          be null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(SpatialState startSpatialState, SpatialState endSpatialState, BooleanSupplier condition) {
		super();

		this.startSpatialState = requireNonNull(startSpatialState, "startSpatialState");
		this.endSpatialState = requireNonNull(endSpatialState, "endSpatialState");
		this.condition = requireNonNull(condition, "condition");

		timer = new Timer();
		timer.setSpeed(.2f);

		setReactionMode(ReactionMode.REACTIVE);
		setEnabled(true);
		setPositionEnabled(true);
		setDimensionsEnabled(true);
	}

	/**
	 * Constructs a SpatialAnimator with a starting ContentView and ending
	 * SpatialState.
	 * 
	 * @param start     the starting ContentView (cannot be null)
	 * @param end       the ending spatial state (cannot be null)
	 * @param condition the condition supplier for animation control (cannot be
	 *                  null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(ContentView start, SpatialState end, BooleanSupplier condition) {
		this(new SpatialState(requireNonNull(start, "start").getX(), start.getY(), start.getAbsoluteWidth(),
				start.getAbsoluteHeight()), end, condition);
		startContentView = start;
	}

	/**
	 * Constructs a SpatialAnimator with a starting SpatialState and ending
	 * ContentView.
	 * 
	 * @param start     the starting spatial state (cannot be null)
	 * @param end       the ending ContentView (cannot be null)
	 * @param condition the condition supplier for animation control (cannot be
	 *                  null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(SpatialState start, ContentView end, BooleanSupplier condition) {
		this(start, new SpatialState(requireNonNull(end, "end").getX(), end.getY(), end.getAbsoluteWidth(),
				end.getAbsoluteHeight()), condition);
		endContentView = end;
	}

	/**
	 * Constructs a SpatialAnimator with both starting and ending ContentViews.
	 * 
	 * @param start     the starting ContentView (cannot be null)
	 * @param end       the ending ContentView (cannot be null)
	 * @param condition the condition supplier for animation control (cannot be
	 *                  null)
	 * @throws NullPointerException if any parameter is null
	 */
	public SpatialAnimator(ContentView start, ContentView end, BooleanSupplier condition) {
		this(start, new SpatialState(requireNonNull(end, "end").getX(), end.getY(), end.getAbsoluteWidth(),
				end.getAbsoluteHeight()), condition);
		startContentView = start;
		endContentView = end;
	}
	
	/**
	 * Returns the current speed of the timer.
	 * 
	 * @return speed value between 0 and 1
	 */
	public float getSpeed() {
		return timer.getSpeed();
	}

	/**
	 * Sets the speed of the SpatialAnimator.
	 * 
	 * <p>
	 * The speed determines how much the timer advances or retreats per update call.
	 * Valid values are between 0 and 1 inclusive.
	 * </p>
	 * 
	 * @param speed the new speed value, must be between 0 and 1
	 * @throws IllegalArgumentException if speed is outside the range [0, 1]
	 */
	public void setSpeed(float speed) {
		timer.setSpeed(speed);
	}

	/**
	 * Checks if the SpatialAnimator has reached either end-point.
	 * 
	 * @return true if current time equals either START or END, false otherwise
	 */
	public boolean isComplete() {
		return timer.isComplete();
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
		this.reactionMode = requireNonNull(reactionMode, "reactionMode");

		return this;
	}

	/**
	 * Checks if position animation is enabled.
	 * 
	 * @return true if position animation is enabled, false otherwise
	 */
	public boolean isPositionEnabled() {
		return positionEnabled;
	}

	/**
	 * Enables or disables position animation.
	 * 
	 * @param positionEnabled true to enable position animation, false to disable
	 * @return this SpatialAnimator for method chaining
	 */
	public SpatialAnimator setPositionEnabled(boolean positionEnabled) {
		this.positionEnabled = positionEnabled;

		return this;
	}

	/**
	 * Checks if dimension animation is enabled.
	 * 
	 * @return true if dimension animation is enabled, false otherwise
	 */
	public boolean isDimensionsEnabled() {
		return dimensionsEnabled;
	}

	/**
	 * Enables or disables dimension animation.
	 * 
	 * @param dimensionsEnabled true to enable dimension animation, false to
	 *                            disable
	 * @return this SpatialAnimator for method chaining
	 */
	public SpatialAnimator setDimensionsEnabled(boolean dimensionsEnabled) {
		this.dimensionsEnabled = dimensionsEnabled;

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
		this.targetSpatialView = requireNonNull(targetSpatialView, "targetSpatialView");

		return this;
	}

	/**
	 * Checks if the animator is enabled.
	 * 
	 * @return true if enabled, false if disabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables the animator.
	 * 
	 * @param enabled true to enable the animator, false to disable
	 * @return this SpatialAnimator for method chaining
	 */
	public SpatialAnimator setEnabled(boolean enabled) {
		this.enabled = enabled;

		return this;
	}

	/**
	 * Updates the animation based on current conditions and timer state.
	 * Interpolates position and dimensions between start and end states. Should be
	 * called every frame for smooth animation.
	 */
	public void update() {
		if (!isEnabled() || targetSpatialView == null) {
			return;
		}

		switch (getReactionMode()) {
		case REACTIVE:
			timer.setIncrementingEnabled(condition.getAsBoolean());
			break;

		case TRIGGER:
			if (timer.isComplete()) {
				timer.setIncrementingEnabled(condition.getAsBoolean());
			}
			break;
		}

		timer.update();

		if (isPositionEnabled()) {
			targetSpatialView.setX(lerp(getStartX(), getEndX()));
			targetSpatialView.setY(lerp(getStartY(), getEndY()));
		}

		if (isDimensionsEnabled()) {
			targetSpatialView.setWidth(lerp(getStartWidth(), getEndWidth()));
			targetSpatialView.setHeight(lerp(getStartHeight(), getEndHeight()));
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
	
	private float getStartX() {
		return startContentView != null ? startContentView.getAbsoluteX() : startSpatialState.getX();
	}
	
	private float getStartY() {
		return startContentView != null ? startContentView.getAbsoluteY() : startSpatialState.getY();
	}
	
	private float getStartWidth() {
		return startContentView != null ? startContentView.getAbsoluteWidth(): startSpatialState.getWidth();
	}
	
	private float getStartHeight() {
		return startContentView != null ? startContentView.getAbsoluteHeight() : startSpatialState.getHeight();
	}
	
	private float getEndX() {
		return endContentView != null ? endContentView.getAbsoluteX() : endSpatialState.getX();
	}
	
	private float getEndY() {
		return endContentView != null ? endContentView.getAbsoluteY() : endSpatialState.getY();
	}
	
	private float getEndWidth() {
		return endContentView != null ? endContentView.getAbsoluteWidth(): endSpatialState.getWidth();
	}
	
	private float getEndHeight() {
		return endContentView != null ? endContentView.getAbsoluteHeight() : endSpatialState.getHeight();
	}

	private float lerp(float start, float end) {
		return convert(timer.getCurrent(), START, END, start, end);
	}
}