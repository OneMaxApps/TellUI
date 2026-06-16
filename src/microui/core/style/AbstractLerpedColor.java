package microui.core.style;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import microui.core.exception.ValueOutOfRangeException;
import microui.util.MathUtils;

/**
 * Abstract class for all classes which releases interpolation of colors
 */
public abstract class AbstractLerpedColor extends AbstractColor {
	private final Supplier<AbstractColor> start, end;
	private final Animator animator;

	/**
	 * Constructs a lerped color with fixed start and end colors.
	 *
	 * @param start the start color (must not be {@code null})
	 * @param end   the end color (must not be {@code null})
	 * @throws NullPointerException if either argument is {@code null}
	 */
	public AbstractLerpedColor(AbstractColor start, AbstractColor end) {
		super();
		requireNonNull(start, "start");
		requireNonNull(end, "end");
		
		this.start = () -> start;
		this.end = () -> end;

		animator = new Animator();
	}
	
	/**
	 * Constructs a lerped color with dynamic start and end colors supplied by
	 * the given suppliers. The suppliers are called each time the color components
	 * are needed, allowing the start/end colors to change over time.
	 *
	 * @param start supplier for the start color (must not be {@code null})
	 * @param end   supplier for the end color (must not be {@code null})
	 * @throws NullPointerException if either supplier is {@code null}
	 */
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

		/**
		 * Updates the animation progress based on the current direction (enabled/disabled)
		 * and speed. Called internally before each color component is accessed.
		 */
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

		/**
		 * Returns whether the animation is currently progressing towards the end color.
		 *
		 * @return {@code true} if animating towards end, {@code false} if towards start
		 */
		public final boolean isStartEnabled() {
			return isStartEnabled;
		}

		/**
		 * Sets the direction of the animation.
		 *
		 * @param isStartEnabled {@code true} to animate towards the end color,
		 *                       {@code false} to animate towards the start color
		 */
		public final void setStartEnabled(boolean isStartEnabled) {
			this.isStartEnabled = isStartEnabled;
		}

		/**
		 * Returns the current animation progress.
		 *
		 * @return progress value between {@link #START_PROGRESS} and {@link #END_PROGRESS}
		 */
		public final float getProgress() {
			return progress;
		}

		/**
		 * Sets the animation progress directly.
		 *
		 * @param progress the new progress value (must be between 0 and 1)
		 * @throws ValueOutOfRangeException if progress is outside [0,1]
		 */
		public final void setProgress(float progress) {
			if (progress < START_PROGRESS || progress > END_PROGRESS) {
				throw new ValueOutOfRangeException("progress", progress, START_PROGRESS, END_PROGRESS);
			}
			this.progress = progress;
		}

		/**
		 * Returns whether the animation loops (ping-pong) between start and end.
		 *
		 * @return {@code true} if looping is enabled, {@code false} otherwise
		 */
		public final boolean isLoopEnabled() {
			return isLoopEnabled;
		}

		/**
		 * Enables or disables loop mode. When enabled, the animation reverses direction
		 * automatically after reaching either end.
		 *
		 * @param enabled {@code true} to enable looping, {@code false} to disable
		 */
		public final void setLoopEnabled(boolean enabled) {
			this.isLoopEnabled = enabled;
		}

		/**
		 * Returns the current animation speed.
		 *
		 * @return speed value between 0.0 and 1.0
		 */
		public final float getSpeed() {
			return speed;
		}

		/**
		 * Sets the animation speed. The speed determines how much the progress changes
		 * per update.
		 *
		 * @param speed the new speed (must be between 0.0 and 1.0)
		 * @throws ValueOutOfRangeException if speed is outside [0,1]
		 */
		public final void setSpeed(float speed) {
			if (speed < 0 || speed > 1) {
				throw new ValueOutOfRangeException("speed", speed, 0, 1);
			}
			this.speed = speed;
		}

	}
}