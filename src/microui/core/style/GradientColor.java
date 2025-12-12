package microui.core.style;

import static java.util.Objects.requireNonNull;

import java.util.function.BooleanSupplier;

/**
 * A gradient color that dynamically transitions between two colors based on a boolean condition.
 * Extends AbstractGradientColor to provide conditional animation control where the gradient
 * progresses based on an external condition rather than automatic timing.
 * <p>
 * The GradientColor animates between start and end colors when a specified condition is true,
 * and reverses the animation when the condition is false. This is useful for creating
 * interactive color effects that respond to user input or application state.
 * </p>
 * <p>
 * Status: STABLE - Do not modify
 * Last Reviewed: 11.10.2025
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see AbstractGradientColor
 * @see BooleanSupplier
 */
public final class GradientColor extends AbstractGradientColor {

	/** Condition supplier that controls gradient animation direction. */
	private final BooleanSupplier condition;

	/**
	 * Constructs a GradientColor with specified start and end colors and a condition.
	 * 
	 * @param start the starting color (cannot be null)
	 * @param end the ending color (cannot be null)
	 * @param condition the condition supplier that controls animation direction (cannot be null)
	 * @throws NullPointerException if any parameter is null
	 */
	public GradientColor(AbstractColor start, AbstractColor end, BooleanSupplier condition) {
		super(start, end);

		this.condition = requireNonNull(condition,"condition");

		// Set a smooth default animation speed
		getAnimator().setSpeed(.025f);
	}

	/**
	 * Resets the animation progress to the beginning (0%).
	 * Useful for restarting the gradient animation from the start.
	 */
	public void resetAnimationProgress() {
		getAnimator().setProgress(0);
	}

	/**
	 * Updates the animation direction based on the current condition state.
	 * Called automatically before color application.
	 */
	@Override
	protected void preApply() {
		super.preApply();
		// Set animation direction based on condition: true = forward, false = reverse
		getAnimator().setStartEnabled(condition.getAsBoolean());
	}
}