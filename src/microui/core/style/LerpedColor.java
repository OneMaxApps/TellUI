package microui.core.style;

import static java.util.Objects.requireNonNull;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A concrete implementation of {@link AbstractLerpedColor} that uses a
 * condition (or progress supplier) to control the direction of color interpolation.
 * 
 * <p>This is useful for creating color transitions that respond to application
 * state, such as hover effects, selection states, or enabled/disabled states.</p>
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
	 * Constructs a new LerpedColor with specified start and end colors, and a
	 * supplier that provides the animation progress directly. The supplied progress
	 * must be a value between 0.0 and 1.0, where 0.0 represents the start color
	 * and 1.0 represents the end color. The animation speed is set to 0.025.
	 * 
	 * @param start            the starting color of the interpolation, cannot be null
	 * @param end              the ending color of the interpolation, cannot be null
	 * @param progressSupplier a supplier that returns the current progress (0.0-1.0),
	 *                         cannot be null
	 * @throws NullPointerException if start, end, or progressSupplier is null
	 */
	public LerpedColor(AbstractColor start, AbstractColor end, Supplier<Float> progressSupplier) {
		super(start, end);
		
		getAnimator().setSpeed(DEFAULT_SPEED_OF_PROGRESS);
		
		this.progressSupplier = requireNonNull(progressSupplier, "progressSupplier");
	}
	
	/**
	 * Constructs a new LerpedColor with supplier-based start and end colors, and a
	 * condition that controls animation direction. The start and end colors are
	 * obtained from suppliers each time the color is evaluated, allowing dynamic
	 * color changes. The animation speed is set to 0.025.
	 * 
	 * @param start     supplier for the starting color, cannot be null
	 * @param end       supplier for the ending color, cannot be null
	 * @param condition a boolean supplier that controls animation direction: when
	 *                  true, animation progresses from start to end; when false,
	 *                  animation progresses from end to start. Cannot be null.
	 * @throws NullPointerException if start, end, or condition is null
	 */
	public LerpedColor(Supplier<AbstractColor> start, Supplier<AbstractColor> end, BooleanSupplier condition) {
		super(start, end);
		
		getAnimator().setSpeed(DEFAULT_SPEED_OF_PROGRESS);
		
		this.condition = requireNonNull(condition, "condition");
	}
	
	/**
	 * Constructs a new LerpedColor with supplier-based start and end colors, and a
	 * supplier that provides the animation progress directly. The start and end
	 * colors are obtained from suppliers each time the color is evaluated.
	 * The supplied progress must be a value between 0.0 and 1.0.
	 * The animation speed is set to 0.025.
	 * 
	 * @param start            supplier for the starting color, cannot be null
	 * @param end              supplier for the ending color, cannot be null
	 * @param progressSupplier a supplier that returns the current progress (0.0-1.0),
	 *                         cannot be null
	 * @throws NullPointerException if start, end, or progressSupplier is null
	 */
	public LerpedColor(Supplier<AbstractColor> start, Supplier<AbstractColor> end, Supplier<Float> progressSupplier) {
		super(start, end);
		
		getAnimator().setSpeed(DEFAULT_SPEED_OF_PROGRESS);
		
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
	 * evaluates the condition and sets the animation direction accordingly, or
	 * uses the progress supplier if provided. It should be called periodically
	 * (e.g., each frame) to ensure the color interpolation responds to changes.
	 * 
	 * <p>If a condition is present, {@link Animator#setStartEnabled(boolean)} is
	 * called with the condition's current value. If a progress supplier is present,
	 * the animation progress is set directly from the supplier's value.</p>
	 * 
	 * <p>This method also calls the parent class's {@code preApply()} to update the
	 * animation progress.</p>
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