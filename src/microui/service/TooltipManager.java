package microui.service;

import static java.util.Objects.requireNonNull;

import microui.core.base.View;
import microui.feedback.Tooltip;
import microui.util.MathUtils;

/**
 * Manages tooltip display in the application.
 * 
 * <p>This is a singleton service that handles the positioning and rendering of tooltips.
 * Tooltips follow the mouse cursor and are constrained to stay within the application window.</p>
 * 
 * <p>Status: STABLE - Do not modify</p>
 * <p>Last Reviewed: 21.10.2025</p>
 */
public final class TooltipManager extends View {
    /** Singleton instance of the TooltipManager. */
    private static final TooltipManager INSTANCE = new TooltipManager();
    
    /** Current tooltip to display, or null if no tooltip is active. */
    private static Tooltip tooltip;

    /**
     * Private constructor for singleton pattern.
     * 
     * <p>Initializes the manager and sets it to be visible by default.</p>
     */
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
     * <p>Positions the tooltip relative to the mouse cursor, constrained within
     * the application window boundaries, then draws it.</p>
     */
    @Override
    protected void render() {
        if (tooltip != null) {
            tooltip.getContent().setAbsolutePosition(getCorrectPositionX(), getCorrectPositionY());
            tooltip.draw();
        }
    }
    
    /**
     * Calculates the correct X position for the tooltip.
     * 
     * <p>Constrains the tooltip's X position to keep it within the application window,
     * ensuring it doesn't extend beyond the right edge.</p>
     * 
     * @return the constrained X coordinate for tooltip placement
     */
    private float getCorrectPositionX() {
        return MathUtils.constrain(ctx.mouseX, 0, ctx.width - tooltip.getContent().getAbsoluteWidth());
    }

    /**
     * Calculates the correct Y position for the tooltip.
     * 
     * <p>Constrains the tooltip's Y position to keep it within the application window,
     * ensuring it doesn't extend beyond the bottom edge.</p>
     * 
     * @return the constrained Y coordinate for tooltip placement
     */
    private float getCorrectPositionY() {
        return MathUtils.constrain(ctx.mouseY, 0, ctx.height - tooltip.getContent().getAbsoluteHeight());
    }
}