package microui.service;

import static java.util.Objects.requireNonNull;

import microui.core.base.View;
import microui.feedback.Tooltip;
import microui.util.Environment;
import microui.util.MathUtils;

/**
 * Manages tooltip display in the application.
 * 
 * <p>
 * This is a singleton service that handles the positioning and rendering of
 * tooltips. Tooltips follow the mouse cursor and are constrained to stay within
 * the application window.
 * </p>
 * 
 * <p>
 * Status: STABLE - Do not modify
 * </p>
 * <p>
 * Last Reviewed: 24.02.2026
 * </p>
 */
public final class TooltipManager extends View {
	private static final TooltipManager INSTANCE = new TooltipManager();
	private static Tooltip tooltip;

	private TooltipManager() {
		super();
		setVisible(true);
	}

	/**
	 * Sets the current tooltip to display.
	 * 
	 * @param tooltip the tooltip to display, cannot be null
	 * 
	 * @throws NullPointerException if the tooltip is null
	 */
	public static void setTooltip(Tooltip tooltip) {
		TooltipManager.tooltip = requireNonNull(tooltip, "tooltip");
	}

	/**
	 * Returns the singleton instance of the TooltipManager.
	 * 
	 * @return the singleton TooltipManager instance
	 */
	public static TooltipManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Renders the current tooltip if one is set.
	 * 
	 * <p>
	 * Positions the tooltip relative to the mouse cursor, constrained within the
	 * application window boundaries, then draws it.
	 * </p>
	 */
	@Override
	protected void render() {
		if (Environment.isAndroid()) {
			return;
		}
			
		if (tooltip != null) {
			tooltip.getContent().setAbsolutePosition(getCorrectPositionX(), getCorrectPositionY());
			tooltip.draw();
		}
	}

	private float getCorrectPositionX() {
		return MathUtils.constrain(ctx.mouseX, 0, ctx.width - tooltip.getContent().getAbsoluteWidth());
	}

	private float getCorrectPositionY() {
		return MathUtils.constrain(ctx.mouseY, 0, ctx.height - tooltip.getContent().getAbsoluteHeight());
	}
}