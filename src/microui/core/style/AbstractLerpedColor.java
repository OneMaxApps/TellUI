package microui.core.style;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import microui.util.MathUtils;

public abstract class AbstractLerpedColor extends AbstractColor {
	private final Supplier<AbstractColor> start, end;
	private final Animator animator;


	public AbstractLerpedColor(AbstractColor start, AbstractColor end) {
		super();
		requireNonNull(start, "start");
		requireNonNull(end, "end");
		
		this.start = () -> start;
		this.end = () -> end;

		animator = new Animator();
	}
	
	public AbstractLerpedColor(Supplier<AbstractColor> start, Supplier<AbstractColor> end) {
		super();
		requireNonNull(start, "start");
		requireNonNull(end, "end");
		
		this.start = start;
		this.end = end;

		animator = new Animator();
	}

	/**
	 * Returns the red component of the interpolated color. The value is calculated
	 * based on the current animation progress.
	 * 
	 * @return the interpolated red component value (0-255)
	 */
	@Override
	public int getRed() {
		return lerp(getStart().getRed(), getEnd().getRed());
	}

	/**
	 * Returns the green component of the interpolated color. The value is
	 * calculated based on the current animation progress.
	 * 
	 * @return the interpolated green component value (0-255)
	 */
	@Override
	public int getGreen() {
		return lerp(getStart().getGreen(), getEnd().getGreen());
	}

	/**
	 * Returns the blue component of the interpolated color. The value is calculated
	 * based on the current animation progress.
	 * 
	 * @return the interpolated blue component value (0-255)
	 */
	@Override
	public int getBlue() {
		return lerp(getStart().getBlue(), getEnd().getBlue());
	}

	/**
	 * Returns the alpha component of the interpolated color. The value is
	 * calculated based on the current animation progress.
	 * 
	 * @return the interpolated alpha component value (0-255)
	 */
	@Override
	public int getAlpha() {
		return lerp(getStart().getAlpha(), getEnd().getAlpha());
	}

	/**
	 * Returns the current animation speed. The speed determines how quickly the
	 * interpolation progresses between the start and end colors during each update.
	 * 
	 * @return the current animation speed, a value between 0.0 and 1.0
	 */
	public final float getSpeed() {
		return getAnimator().getSpeed();
	}

	/**
	 * Sets the animation speed and returns this instance for method chaining. The
	 * speed determines how quickly the interpolation progresses between the start
	 * and end colors during each update.
	 * 
	 * @param speed the new animation speed, must be between 0.0 and 1.0
	 * @return this instance for method chaining
	 * @throws IllegalArgumentException if speed is outside the range [0.0, 1.0]
	 */
	public final AbstractLerpedColor setSpeed(float speed) {
		getAnimator().setSpeed(speed);

		return this;
	}

	protected final AbstractColor getStart() {
		return start.get();
	}

	protected final AbstractColor getEnd() {
		return end.get();
	}

	@Override
	protected void preApply() {
		super.preApply();
		animator.update();

		if (getStart() instanceof AbstractLerpedColor lerp) {
			lerp.preApply();
		}

		if (getEnd() instanceof AbstractLerpedColor lerp) {
			lerp.preApply();
		}

	}

	protected final Animator getAnimator() {
		return animator;
	}

	protected final int lerp(int start, int end) {
		return (int) MathUtils.convert(animator.getProgress(), Animator.START_PROGRESS, Animator.END_PROGRESS, start, end);
	}

	/**
	 * A controller for managing the interpolation animation progress. This inner
	 * class handles the timing and direction of color interpolation.
	 */
	protected final class Animator {
		public static final int START_PROGRESS = 0;
		public static final int END_PROGRESS = 1;
		
		private static final int EPSILON = 32;
		
		private float progress, speed;
		private boolean isStartEnabled, isLoopEnabled;

		private long lastUpdateTime;
		
		public Animator() {
			super();
			speed = .1f;
		}

		public void update() {

			if (System.currentTimeMillis() - lastUpdateTime < EPSILON) {
				return;
			}

			lastUpdateTime = System.currentTimeMillis();

			if (isStartEnabled()) {
				if (progress < END_PROGRESS) {
					progress = Math.min(progress + speed, END_PROGRESS);
				} else {
					if (isLoopEnabled()) {
						setStartEnabled(false);
					}

				}
			} else {
				if (progress > START_PROGRESS) {
					progress = Math.max(progress - speed, START_PROGRESS);
				} else {
					if (isLoopEnabled()) {
						setStartEnabled(true);
					}
				}
			}
		}

		public final boolean isStartEnabled() {
			return isStartEnabled;
		}

		public final void setStartEnabled(boolean isStartEnabled) {
			this.isStartEnabled = isStartEnabled;
		}

		public final float getProgress() {
			return progress;
		}

		public final void setProgress(float progress) {
			if (progress < START_PROGRESS || progress > END_PROGRESS) {
				throw new IllegalArgumentException("Progress for lerp must be between 0 and 1");
			}
			this.progress = progress;
		}

		
		public final boolean isLoopEnabled() {
			return isLoopEnabled;
		}

		public final void setLoopEnabled(boolean enabled) {
			this.isLoopEnabled = enabled;
		}

		public final float getSpeed() {
			return speed;
		}

		public final void setSpeed(float speed) {
			if (speed < 0 || speed > 1) {
				throw new IllegalArgumentException("Speed must be between 0 and 1");
			}
			this.speed = speed;
		}

	}
}