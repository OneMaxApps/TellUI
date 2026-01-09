package microui.core.style;

import static java.util.Objects.requireNonNull;

import microui.util.MathUtils;

/**
 * An abstract base class for colors that interpolate between two other colors over time.
 * Provides animation control for the interpolation progress and implements color
 * component calculation through linear interpolation.
 * 
 * <p>This class manages an internal animation progress that determines the blend ratio
 * between the start and end colors. The animation can be configured to progress
 * forward, backward, or loop continuously between the two colors.
 * 
 * <p>Subclasses must implement the abstract methods from {@link AbstractColor}
 * and can use the protected methods provided here to access the animation state
 * and perform interpolation calculations.
 * 
 * <p><b>Animation Behavior:</b>
 * <ul>
 *   <li>When the animation is enabled ({@code isStartEnabled = true}), the progress
 *       increases from 0.0 to 1.0 at the configured speed.</li>
 *   <li>When disabled, the progress decreases from the current value back to 0.0.</li>
 *   <li>When looping is enabled, the animation automatically reverses direction
 *       when it reaches either endpoint.</li>
 * </ul>
 * 
 * @see AbstractColor
 */
public abstract class AbstractLerpedColor extends AbstractColor {
	private final AbstractColor start;
	private final AbstractColor end;
	private final Animator animator;

	/**
	 * Constructs a new interpolated color with the specified start and end colors.
	 * The initial animation speed is set to 0.1 (10% progress per update).
	 * 
	 * @param start the starting color of the interpolation sequence, cannot be null
	 * @param end the ending color of the interpolation sequence, cannot be null
	 * @throws NullPointerException if either start or end is null
	 */
	public AbstractLerpedColor(AbstractColor start, AbstractColor end) {
		super();
		
		this.start = requireNonNull(start,"start");
		this.end = requireNonNull(end,"end");

		animator = new Animator();
	}

	/**
	 * Returns the red component of the interpolated color.
	 * The value is calculated based on the current animation progress.
	 * 
	 * @return the interpolated red component value (0-255)
	 */
	@Override
	public int getRed() {
		return lerp(getStart().getRed(), getEnd().getRed());
	}

	/**
	 * Returns the green component of the interpolated color.
	 * The value is calculated based on the current animation progress.
	 * 
	 * @return the interpolated green component value (0-255)
	 */
	@Override
	public int getGreen() {
		return lerp(getStart().getGreen(), getEnd().getGreen());
	}

	/**
	 * Returns the blue component of the interpolated color.
	 * The value is calculated based on the current animation progress.
	 * 
	 * @return the interpolated blue component value (0-255)
	 */
	@Override
	public int getBlue() {
		return lerp(getStart().getBlue(), getEnd().getBlue());
	}

	/**
	 * Returns the alpha component of the interpolated color.
	 * The value is calculated based on the current animation progress.
	 * 
	 * @return the interpolated alpha component value (0-255)
	 */
	@Override
	public int getAlpha() {
		return lerp(getStart().getAlpha(), getEnd().getAlpha());
	}
	
	/**
	 * Returns the current animation speed.
	 * The speed determines how quickly the interpolation progresses between
	 * the start and end colors during each update.
	 * 
	 * @return the current animation speed, a value between 0.0 and 1.0
	 */
	public final float getSpeed() {
		return getAnimator().getSpeed();
	}
	
	/**
	 * Sets the animation speed and returns this instance for method chaining.
	 * The speed determines how quickly the interpolation progresses between
	 * the start and end colors during each update.
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
		return start;
	}

	protected final AbstractColor getEnd() {
		return end;
	}

	@Override
	protected void preApply() {
		super.preApply();
		animator.update();

		if (getStart() instanceof AbstractLerpedColor gradient) {
			gradient.preApply();
		}

		if (getEnd() instanceof AbstractLerpedColor gradient) {
			gradient.preApply();
		}

	}

	protected final Animator getAnimator() {
		return animator;
	}

	protected final int lerp(int start, int end) {
		return (int) MathUtils.convert(animator.getProgress(), Animator.START_PROGRESS, Animator.END_PROGRESS, start,
				end);
	}

	/**
	 * A controller for managing the interpolation animation progress.
	 * This inner class handles the timing and direction of color interpolation.
	 * 
	 * <p>The animator maintains a progress value between {@link #START_PROGRESS} (0.0)
	 * and {@link #END_PROGRESS} (1.0) that represents the current blend ratio
	 * between the start and end colors.
	 * 
	 * <p><b>Usage:</b>
	 * <ul>
	 *   <li>Call {@link #update()} periodically to advance/reverse the animation</li>
	 *   <li>Use {@link #setStartEnabled(boolean)} to start/stop forward progression</li>
	 *   <li>Use {@link #setLoopEnabled(boolean)} to enable automatic direction reversal</li>
	 *   <li>Use {@link #setProgress(float)} to manually set the interpolation point</li>
	 * </ul>
	 */
	protected final class Animator {
		public static final int START_PROGRESS = 0;
		public static final int END_PROGRESS = 1;
		
		private float progress, speed;
		private boolean isStartEnabled, isLoopEnabled;

		/**
		 * Creates a new Animator with default settings:
		 * <ul>
		 *   <li>Progress: 0.0 (full start color)</li>
		 *   <li>Speed: 0.1 (10% progress per update)</li>
		 *   <li>Start enabled: false</li>
		 *   <li>Loop enabled: false</li>
		 * </ul>
		 */
		public Animator() {
			super();
			speed = .1f;
		}

		/**
		 * Updates the animation progress based on current settings.
		 * This method should be called periodically (e.g., each frame) to advance
		 * the interpolation. The direction of progression depends on whether
		 * start is enabled and if looping is configured.
		 */
		public void update() {
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
		 * Checks whether forward animation progression is currently enabled.
		 * 
		 * @return true if the animation is progressing from start to end,
		 *         false if it's progressing from end to start or paused
		 */
		public final boolean isStartEnabled() {
			return isStartEnabled;
		}

		/**
		 * Enables or disables forward animation progression.
		 * When enabled, the progress increases toward {@link #END_PROGRESS}.
		 * When disabled, the progress decreases toward {@link #START_PROGRESS}.
		 * 
		 * @param isStartEnabled true to enable forward progression,
		 *                      false to enable reverse progression
		 */
		public final void setStartEnabled(boolean isStartEnabled) {
			this.isStartEnabled = isStartEnabled;
		}

		/**
		 * Returns the current interpolation progress.
		 * A value of 0.0 represents the start color, 1.0 represents the end color,
		 * and values in between represent interpolated colors.
		 * 
		 * @return the current progress value between 0.0 and 1.0
		 */
		public final float getProgress() {
			return progress;
		}

		/**
		 * Sets the interpolation progress to a specific value.
		 * This allows manual control over the interpolation point without
		 * waiting for the animation to progress naturally.
		 * 
		 * @param progress the new progress value, must be between 0.0 and 1.0
		 * @throws IllegalArgumentException if progress is outside the range [0.0, 1.0]
		 */
		public final void setProgress(float progress) {
			if (progress < START_PROGRESS || progress > END_PROGRESS) {
				throw new IllegalArgumentException("progress for gradient must be between 0 and 1");
			}
			this.progress = progress;
		}

		/**
		 * Checks whether looping animation is enabled.
		 * When looping is enabled, the animation automatically reverses direction
		 * when it reaches either endpoint ({@link #START_PROGRESS} or {@link #END_PROGRESS}).
		 * 
		 * @return true if looping is enabled, false otherwise
		 */
		public final boolean isLoopEnabled() {
			return isLoopEnabled;
		}

		/**
		 * Enables or disables looping animation.
		 * When enabled, the animation will automatically reverse direction when it
		 * reaches either endpoint, creating a continuous back-and-forth animation.
		 * 
		 * @param isLoopEnabled true to enable looping, false to disable it
		 */
		public final void setLoopEnabled(boolean isLoopEnabled) {
			this.isLoopEnabled = isLoopEnabled;
		}

		/**
		 * Returns the current animation speed.
		 * The speed determines how much the progress changes during each {@link #update()}.
		 * 
		 * @return the current speed value between 0.0 and 1.0
		 */
		public final float getSpeed() {
			return speed;
		}

		/**
		 * Sets the animation speed.
		 * The speed determines how much the progress changes during each {@link #update()}.
		 * Higher values result in faster interpolation between colors.
		 * 
		 * @param speed the new speed value, must be between 0.0 and 1.0
		 * @throws IllegalArgumentException if speed is outside the range [0.0, 1.0]
		 */
		public final void setSpeed(float speed) {
			if (speed < 0 || speed > 1) {
				throw new IllegalArgumentException("speed must be between 0 and 1");
			}
			this.speed = speed;
		}

	}
}