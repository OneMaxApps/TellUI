package microui.feedback;

import microui.core.base.ContentView;

public abstract class TooltipContent extends ContentView {
	private Tooltip tooltip;

	public TooltipContent() {
		super();
		setVisible(false);
		setConstrainDimensionsEnabled(false);
		setNegativeDimensionsEnabled(false);
	}

	public abstract boolean isPreparedShow();

	public abstract boolean isPreparedClose();

	protected final Tooltip getTooltip() {
		if (tooltip == null) {
			throw new IllegalStateException("tooltip is not initialized");
		}
		return tooltip;
	}

	public final void setTooltip(Tooltip tooltip) {
		if (tooltip == null) {
			throw new NullPointerException("the tooltip cannot be null");
		}
		this.tooltip = tooltip;
	}

}