package microui.core.style;

/**
 * A continuously looping gradient color that automatically animates between two colors.
 * Extends AbstractGradientColor to provide automatic, continuous animation that
 * cycles between start and end colors without external triggering.
 * <p>
 * The GradientLoopColor creates a smooth, endless color transition effect that
 * can be used for animations, loading indicators, or decorative background effects.
 * The looping behavior can be enabled or disabled as needed.
 * </p>
 * <p>
 * Status: STABLE - Do not modify
 * Last Reviewed: 11.10.2025
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see AbstractGradientColor
 */
public class GradientLoopColor extends AbstractGradientColor {
	/** Flag tracking whether looping is enabled (mirrors animator state). */
	private boolean isLoopEnabled;

	/**
	 * Constructs a GradientLoopColor with specified start and end colors.
	 * Initialized with looping enabled and a slow, smooth animation speed.
	 * 
	 * @param start the starting color (cannot be null)
	 * @param end the ending color (cannot be null)
	 */
	public GradientLoopColor(AbstractColor start, AbstractColor end) {
		super(start, end);
		setLoopEnabled(true);
		getAnimator().setSpeed(.01f);
	}

	/**
	 * Checks if looping animation is currently enabled.
	 * 
	 * @return true if looping is enabled, false if disabled
	 */
	public final boolean isLoopEnabled() {
		return getAnimator().isLoopEnabled();
	}

	/**
	 * Enables or disables the looping animation.
	 * 
	 * @param isLoopEnabled true to enable continuous looping, false to stop at current state
	 */
	public final void setLoopEnabled(boolean isLoopEnabled) {
		getAnimator().setLoopEnabled(isLoopEnabled);
		this.isLoopEnabled = isLoopEnabled;
	}
	
	/**
	 * Updates the animation if looping is enabled.
	 * Called automatically before color application.
	 */
	@Override
	protected void preApply() {
		super.preApply();
		// Only update animation if looping is enabled
		if (isLoopEnabled) {
			getAnimator().update();
		}
	}
}