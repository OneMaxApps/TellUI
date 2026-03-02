package microui.util;

/**
 * Timer utility for tracking progress between two endpoints over time.
 * 
 * <p>
 * Manages a timer value that progresses between START (0) and END (1) at a
 * configurable speed. Can increment or decrement the timer value and provides
 * methods to check completion status.
 * </p>
 */
public final class Timer {
	/** Default speed for timer */
	public static final float DEFAULT_SPEED = .05f;
	/** constant for setting in start */
	public static final int START = 0;
	/** constant for setting in end */
	public static final int END = 1;
	private float currentTime;
	private float speed;
	private boolean incrementing;

	/**
	 * Constructs a Timer with default settings.
	 * 
	 * <p>
	 * Initializes with current time at START, default speed, and incrementing
	 * direction.
	 * </p>
	 */
	public Timer() {
		super();
		setSpeed(DEFAULT_SPEED);
	}

	/**
	 * Updates the timer value based on current speed and direction.
	 * 
	 * <p>
	 * Increments or decrements the current time by the speed value, then constrains
	 * the result to the START-END range.
	 * </p>
	 */
	public void update() {
		setCurrentTime(isIncrementing() ? getCurrent() + getSpeed() : getCurrent() - getSpeed());
	}

	/**
	 * Returns the current timer value.
	 * 
	 * @return current time value between START and END (inclusive)
	 */
	public float getCurrent() {
		return currentTime;
	}

	/**
	 * Resets the timer to the START value.
	 */
	public void reset() {
		setCurrentTime(START);
	}

	/**
	 * Returns the current speed of the timer.
	 * 
	 * @return speed value between 0 and 1
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed of the timer.
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
		if (speed < 0 || speed > 1) {
			throw new IllegalArgumentException("speed for Timer must be between 0 and 1");
		}
		this.speed = speed;
	}

	/**
	 * Checks if the timer is currently incrementing.
	 * 
	 * @return true if incrementing (moving from START to END), false if
	 *         decrementing
	 */
	public boolean isIncrementing() {
		return incrementing;
	}

	/**
	 * Sets the direction of the timer.
	 * 
	 * @param enabled true to make the timer increment (START → END), false
	 *                       to make it decrement (END → START)
	 */
	public void setIncrementingEnabled(boolean enabled) {
		this.incrementing = enabled;
	}

	/**
	 * Checks if the timer has reached either endpoint.
	 * 
	 * @return true if current time equals either START or END, false otherwise
	 */
	public boolean isComplete() {
		return currentTime == START || currentTime == END;
	}

	private void setCurrentTime(float current) {
		this.currentTime = current < START ? START : current > END ? END : current;
	}
}