package microui.util;

public final class Timer {
	public static final float DEFAULT_SPEED = .05f;
	public static final int START = 0;
	public static final int END = 1;
	private float currentTime, speed;
	private boolean incrementing;

	public Timer() {
		super();
		setSpeed(DEFAULT_SPEED);
	}

	public void update() {
		setCurrentTime(isIncrementing() ? getCurrent() + getSpeed() : getCurrent() - getSpeed());
	}

	public float getCurrent() {
		return currentTime;
	}

	public void reset() {
		setCurrentTime(START);
	}

	public float getSpeed() {
		return speed;
	}

	/**
	 * this method provides setting speed value
	 * 
	 * @param speed must be between 0 and 1
	 */
	public void setSpeed(float speed) {
		if (speed < 0 || speed > 1) {
			throw new IllegalArgumentException("speed for Timer must be between 0 and 1");
		}
		this.speed = speed;
	}

	public boolean isIncrementing() {
		return incrementing;
	}

	public void setIncrementing(boolean isIncrementing) {
		this.incrementing = isIncrementing;
	}

	public boolean isComplete() {
		return currentTime == START || currentTime == END;
	}

	private void setCurrentTime(float current) {
		this.currentTime = current < START ? START : current > END ? END : current;
	}
}