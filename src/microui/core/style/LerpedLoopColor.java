package microui.core.style;

/**
 * A continuously looping interpolated color that automatically cycles between
 * start and end colors.
 * 
 * <p>This class extends {@link AbstractLerpedColor} with built-in looping behavior.
 * When loop mode is enabled, the color automatically interpolates back and forth
 * between the start and end colors, creating a continuous pulsing or breathing effect.
 * 
 * <p>The default animation speed is set to 0.01 (1% progress per update), which
 * creates a smooth, gradual transition suitable for visual effects.
 * 
 * <p>Looping can be temporarily disabled while keeping the current progress state,
 * allowing for controlled pauses in the animation.
 * 
 * @see AbstractLerpedColor
 */
public class LerpedLoopColor extends AbstractLerpedColor {
	
	private boolean loopEnabled;

	/**
	 * Constructs a new looping interpolated color with the specified start and end colors.
	 * Loop mode is enabled by default with an animation speed of 0.01.
	 * 
	 * @param start the starting color of the loop sequence, cannot be null
	 * @param end the ending color of the loop sequence, cannot be null
	 */
	public LerpedLoopColor(AbstractColor start, AbstractColor end) {
		super(start, end);
		setLoopEnabled(true);
		getAnimator().setSpeed(.01f);
	}

	/**
	 * Checks whether loop mode is currently enabled.
	 * 
	 * @return true if the color is automatically cycling between start and end colors,
	 *         false if the animation is paused at its current progress
	 */
	public final boolean isLoopEnabled() {
		return getAnimator().isLoopEnabled();
	}

	/**
	 * Enables or disables loop mode.
	 * When enabled, the animation automatically reverses direction when reaching
	 * either endpoint, creating a continuous back-and-forth transition.
	 * When disabled, the animation stops at the current progress until loop mode
	 * is re-enabled.
	 * 
	 * @param isLoopEnabled true to enable continuous looping, false to pause at current progress
	 */
	public final void setLoopEnabled(boolean loopEnabled) {
		getAnimator().setLoopEnabled(loopEnabled);
		this.loopEnabled = loopEnabled;
	}
	
	/**
	 * Updates the animation state before applying color operations.
	 * When loop mode is enabled, this method triggers an animation update
	 * to progress the interpolation. When loop mode is disabled, the animation
	 * remains at its current progress.
	 * 
	 * <p>This method should be called periodically (e.g., each frame) to
	 * maintain the animation when loop mode is active.
	 */
	@Override
	protected void preApply() {
		super.preApply();
		if (loopEnabled) {
			getAnimator().update();
		}
	}
}