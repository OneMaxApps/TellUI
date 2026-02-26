package microui.core.style;

import static java.util.Objects.requireNonNull;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A concrete implementation of {@link AbstractLerpedColor} that uses a
 * condition to control the direction of color interpolation.
 * 
 * <p>
 * This class automatically enables or disables forward animation progression
 * based on the result of a boolean condition evaluated during each update. When
 * the condition returns true, the animation progresses from start to end color.
 * When false, it progresses back from end to start color.
 * 
 * <p>
 * This is useful for creating color transitions that respond to application
 * state, such as hover effects, selection states, or enabled/disabled states.
 * 
 * @see AbstractLerpedColor
 */
public final class LerpedColor extends AbstractLerpedColor {
	private static final float DEFAULT_SPEED_OF_PROGRESS = .025f;
	
	private BooleanSupplier condition;
	private Supplier<Float> progressSupplier;
	
	/**
	 * Constructs a new LerpedColor with specified start and end colors, and a
	 * condition that controls animation direction. The animation speed is set to
	 * 0.025 (2.5% progress per update).
	 * 
	 * @param start     the starting color of the interpolation, cannot be null
	 * @param end       the ending color of the interpolation, cannot be null
	 * @param condition a boolean supplier that controls animation direction: when
	 *                  true, animation progresses from start to end; when false,
	 *                  animation progresses from end to start. Cannot be null.
	 * @throws NullPointerException if start, end, or condition is null
	 */
	public LerpedColor(AbstractColor start, AbstractColor end, BooleanSupplier condition) {
		super(start, end);

		this.condition = requireNonNull(condition, "condition");

		getAnimator().setSpeed(DEFAULT_SPEED_OF_PROGRESS);
	}
	
	/**
	 * Constructs a new LerpedColor with specified start and end colors,
	 * supplier controlling progress of interpolation
	 * 
	 * @param start 	the starting color of the interpolation, cannot be null
	 * @param end		the ending color of the interpolation, cannot be null
	 * @param supplier the supplier witch must have result between 0 and 1
	 */
	public LerpedColor(AbstractColor start, AbstractColor end, Supplier<Float> progressSupplier) {
		super(start, end);

		this.progressSupplier = requireNonNull(progressSupplier, "progressSupplier");
	}

	/**
	 * Resets the animation progress to the beginning (start color). This
	 * immediately sets the interpolation progress to 0, regardless of the current
	 * condition or animation state.
	 */
	public void resetAnimationProgress() {
		getAnimator().setProgress(0);
	}

	/**
	 * Updates the animation state before applying color operations. This method
	 * evaluates the condition and sets the animation direction accordingly. It
	 * should be called periodically (e.g., each frame) to ensure the color
	 * interpolation responds to condition changes.
	 * 
	 * <p>
	 * If the condition returns true, forward progression is enabled. If false,
	 * reverse progression is enabled.
	 * 
	 * <p>
	 * This method also calls the parent class's preApply method to update the
	 * animation progress.
	 */
	@Override
	protected void preApply() {
		super.preApply();
		if (condition != null) {
			getAnimator().setStartEnabled(condition.getAsBoolean());
		}
		
		if (progressSupplier != null) {
			getAnimator().setProgress(progressSupplier.get());
		}
	}
}