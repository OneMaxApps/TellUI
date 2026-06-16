package microui.constants;

/**
 * Enumeration defining the scaling factors for automatic text resizing in TextView.
 * The AutoResizeMode determines how aggressively text is scaled to
 * fit within its bounds, with lower values resulting in larger text and higher
 * values resulting in smaller text.
 * 
 * <p>
 * Available modes range from FULL (largest text) to TINY (smallest text).
 * </p>
 */
public enum AutoResizeMode {
	/**
	 * Largest text size - uses the full available space. Scaling factor: 1 (text
	 * size = min(width, height) / 1)
	 */
	FULL(1),

	/**
	 * Large text size - text occupies about half the available space. Scaling
	 * factor: 2 (text size = min(width, height) / 2)
	 */
	BIG(2),

	/**
	 * Medium text size - balanced size for general use. Scaling factor: 3 (text
	 * size = min(width, height) / 3)
	 */
	MIDDLE(3),

	/**
	 * Small text size - compact text for limited space. Scaling factor: 4 (text
	 * size = min(width, height) / 4)
	 */
	SMALL(4),

	/**
	 * Smallest text size - minimal text for maximum content. Scaling factor: 5
	 * (text size = min(width, height) / 5)
	 */
	TINY(5);

	/**
	 * Constructs an AutoResizeMode with the specified scaling factor.
	 *
	 * @param value the scaling factor divisor (must be positive)
	 */
	private AutoResizeMode(int value) {
		this.value = value;
	}

	/** The scaling factor divisor for automatic text size calculation. */
	private final int value;

	/**
	 * Gets the scaling factor divisor for this resize mode. The divisor is used to
	 * calculate automatic text size.
	 *
	 * @return the scaling factor divisor
	 */
	public final int getValue() {
		return value;
	}

}