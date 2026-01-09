package microui.feedback;

import static java.util.Objects.requireNonNull;

import microui.core.base.ContentView;

/**
 * Abstract base class for tooltip content implementations.
 * Provides common functionality for all tooltip content types and defines
 * the interface for tooltip content lifecycle management.
 * <p>
 * TooltipContent extends ContentView to inherit padding, margin, and rendering
 * capabilities while adding tooltip-specific functionality. Subclasses must
 * implement methods to determine when the tooltip can be shown and closed.
 * </p>
 * @see ContentView
 * @see Tooltip
 */
public abstract class TooltipContent extends ContentView {
	private Tooltip tooltip;

	/**
	 * Constructs a TooltipContent with default settings.
	 * Initializes as invisible with constraints disabled for flexible sizing.
	 */
	public TooltipContent() {
		super();
		setVisible(false);
		setConstrainDimensionsEnabled(false);
		setNegativeDimensionsEnabled(false);
	}

	/**
	 * Determines if the tooltip is prepared to be shown.
	 * Subclasses should implement this to check if conditions are met for display.
	 * 
	 * @return true if the tooltip can be shown, false otherwise
	 */
	public abstract boolean isPreparedShow();

	/**
	 * Determines if the tooltip is prepared to be closed.
	 * Subclasses should implement this to check if conditions allow closure.
	 * 
	 * @return true if the tooltip can be closed, false otherwise
	 */
	public abstract boolean isPreparedClose();

	/**
	 * Returns the Tooltip that contains this content.
	 * 
	 * @return the parent Tooltip
	 * @throws IllegalStateException if tooltip has not been set
	 */
	protected final Tooltip getTooltip() {
		if (tooltip == null) {
			throw new IllegalStateException("tooltip is not initialized");
		}
		return tooltip;
	}

	/**
	 * Sets the Tooltip that contains this content.
	 * 
	 * @param tooltip the Tooltip to associate with this content (cannot be null)
	 * @throws NullPointerException if tooltip is null
	 */
	public final void setTooltip(Tooltip tooltip) {
		this.tooltip = requireNonNull(tooltip,"tooltip");
	}

}