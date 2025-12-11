package microui.core.style;

import static java.util.Objects.requireNonNull;

import microui.util.MathUtils;

/**
 * Abstract base class for gradient color effects that interpolate between two colors.
 * Provides animated transitions between start and end colors with configurable
 * speed, direction, and looping behavior.
 * <p>
 * This class manages smooth color transitions by interpolating RGBA components
 * between a start color and an end color. It includes an internal animator
 * for controlling the transition progress and supports both one-shot and
 * looping animations.
 * </p>
 * <p>
 * Status: STABLE - Do not modify
 * Last Reviewed: 16.10.2025
 * </p>
 * 
 * @see AbstractColor
 * @see Animator
 */
public abstract class AbstractGradientColor extends AbstractColor {
	/** The starting color of the gradient. */
	private final AbstractColor start;
	/** The ending color of the gradient. */
	private final AbstractColor end;
	/** Internal animator for controlling gradient progress. */
	private final Animator animator;

	/**
	 * Constructs an AbstractGradientColor with specified start and end colors.
	 * 
	 * @param start the starting color (cannot be null)
	 * @param end the ending color (cannot be null)
	 * @throws NullPointerException if start or end is null
	 */
	public AbstractGradientColor(AbstractColor start, AbstractColor end) {
		super();
		
		this.start = requireNonNull(start,"start");
		this.end = requireNonNull(end,"end");

		animator = new Animator();
	}

	/**
	 * Returns the starting color of the gradient.
	 * 
	 * @return the starting color
	 */
	protected final AbstractColor getStart() {
		return start;
	}

	/**
	 * Returns the ending color of the gradient.
	 * 
	 * @return the ending color
	 */
	protected final AbstractColor getEnd() {
		return end;
	}

	/**
	 * Updates the gradient animation before color application.
	 * Recursively updates nested gradient colors if present.
	 */
	@Override
	protected void preApply() {
		super.preApply();
		animator.update();

		// Recursively update nested gradient colors
		if (getStart() instanceof AbstractGradientColor gradient) {
			gradient.preApply();
		}

		if (getEnd() instanceof AbstractGradientColor gradient) {
			gradient.preApply();
		}

	}

	/**
	 * Returns the interpolated red component.
	 * 
	 * @return the interpolated red value (0-255)
	 */
	@Override
	public int getRed() {
		return lerp(getStart().getRed(), getEnd().getRed());
	}

	/**
	 * Returns the interpolated green component.
	 * 
	 * @return the interpolated green value (0-255)
	 */
	@Override
	public int getGreen() {
		return lerp(getStart().getGreen(), getEnd().getGreen());
	}

	/**
	 * Returns the interpolated blue component.
	 * 
	 * @return the interpolated blue value (0-255)
	 */
	@Override
	public int getBlue() {
		return lerp(getStart().getBlue(), getEnd().getBlue());
	}

	/**
	 * Returns the interpolated alpha component.
	 * 
	 * @return the interpolated alpha value (0-255)
	 */
	@Override
	public int getAlpha() {
		return lerp(getStart().getAlpha(), getEnd().getAlpha());
	}
	
	/**
	 * Returns the current animation speed.
	 * 
	 * @return the animation speed (0-1)
	 */
	public final float getSpeed() {
		return getAnimator().getSpeed();
	}
	
	/**
	 * Sets the animation speed.
	 * 
	 * @param speed the animation speed to set (0-1)
	 * @return this AbstractGradientColor for method chaining
	 * @throws IllegalArgumentException if speed is not between 0 and 1
	 */
	public final AbstractGradientColor setSpeed(float speed) {
		getAnimator().setSpeed(speed);
		
		return this;
	}

	/**
	 * Returns the internal animator instance.
	 * 
	 * @return the gradient animator
	 */
	protected final Animator getAnimator() {
		return animator;
	}

	/**
	 * Linearly interpolates between two values based on current animation progress.
	 * 
	 * @param start the starting value
	 * @param end the ending value
	 * @return the interpolated value between start and end
	 */
	protected final int lerp(int start, int end) {
		return (int) MathUtils.convert(animator.getProgress(), Animator.START_PROGRESS, Animator.END_PROGRESS, start,
				end);
	}

	/**
	 * Internal class for managing gradient animation progress and behavior.
	 */
	protected final class Animator {
		/** Starting progress value (0%). */
		public static final int START_PROGRESS = 0;
		/** Ending progress value (100%). */
		public static final int END_PROGRESS = 1;
		
		/** Current animation progress (0-1). */
		private float progress, speed;
		/** Whether animation is moving from start to end. */
		private boolean isStartEnabled, isLoopEnabled;

		/**
		 * Constructs an Animator with default speed of 0.1.
		 */
		public Animator() {
			super();
			speed = .1f;
		}

		/**
		 * Updates the animation progress based on current state and speed.
		 * Handles forward, reverse, and looping animations.
		 */
		public void update() {
			if (isStartEnabled()) {
				// Moving from start to end
				if (progress < END_PROGRESS) {
					progress = Math.min(progress + speed, END_PROGRESS);
				} else {
					// Reached end - handle looping if enabled
					if (isLoopEnabled()) {
						setStartEnabled(false);
					}

				}
			} else {
				// Moving from end to start
				if (progress > START_PROGRESS) {
					progress = Math.max(progress - speed, START_PROGRESS);
				} else {
					// Reached start - handle looping if enabled
					if (isLoopEnabled()) {
						setStartEnabled(true);
					}
				}
			}
		}

		/**
		 * Checks if animation is moving from start to end.
		 * 
		 * @return true if moving forward, false if moving backward
		 */
		public final boolean isStartEnabled() {
			return isStartEnabled;
		}

		/**
		 * Sets the animation direction.
		 * 
		 * @param isStartEnabled true to move from start to end, false to move from end to start
		 */
		public final void setStartEnabled(boolean isStartEnabled) {
			this.isStartEnabled = isStartEnabled;
		}

		/**
		 * Returns the current animation progress.
		 * 
		 * @return the progress value (0-1)
		 */
		public final float getProgress() {
			return progress;
		}

		/**
		 * Sets the animation progress directly.
		 * 
		 * @param progress the progress value to set (0-1)
		 * @throws IllegalArgumentException if progress is not between 0 and 1
		 */
		public final void setProgress(float progress) {
			if (progress < START_PROGRESS || progress > END_PROGRESS) {
				throw new IllegalArgumentException("progress for gradient must be between 0 and 1");
			}
			this.progress = progress;
		}

		/**
		 * Checks if looping is enabled.
		 * 
		 * @return true if animation loops continuously, false for one-shot
		 */
		public final boolean isLoopEnabled() {
			return isLoopEnabled;
		}

		/**
		 * Enables or disables animation looping.
		 * 
		 * @param isLoopEnabled true to enable continuous looping, false for one-shot
		 */
		public final void setLoopEnabled(boolean isLoopEnabled) {
			this.isLoopEnabled = isLoopEnabled;
		}

		/**
		 * Returns the current animation speed.
		 * 
		 * @return the speed value (0-1)
		 */
		public final float getSpeed() {
			return speed;
		}

		/**
		 * Sets the animation speed.
		 * 
		 * @param speed the speed value to set (0-1)
		 * @throws IllegalArgumentException if speed is not between 0 and 1
		 */
		public final void setSpeed(float speed) {
			if (speed < 0 || speed > 1) {
				throw new IllegalArgumentException("speed must be between 0 and 1");
			}
			this.speed = speed;
		}

	}
}