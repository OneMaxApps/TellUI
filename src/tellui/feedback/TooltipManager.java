package tellui.feedback;

import static java.util.Objects.requireNonNull;

import tellui.core.base.View;
import tellui.util.Environment;
import tellui.util.MathUtils;

/**
 * Manages the display of tooltips. This is a singleton class responsible for
 * rendering a tooltip near the mouse cursor. On Android, tooltips are disabled.
 *
 * @see Tooltip
 */
public final class TooltipManager extends View {
	private static TooltipManager instance;
	private Tooltip tooltip;
	
	private TooltipManager() {
		setVisible(true);
	}
	
	/**
	 * Returns the singleton instance of the tooltip manager.
	 *
	 * @return the unique {@code TooltipManager} instance
	 */
	public static TooltipManager getInstance() {
		if (instance == null) {
			instance = new TooltipManager();
		}
		
		return instance;
	}
	
	void setTooltip(Tooltip tooltip) {
		this.tooltip = requireNonNull(tooltip, "tooltip");
	}
	
	@Override
	protected void render() {
		if (Environment.isAndroid()) {
			return;
		}
			
		if (tooltip != null) {
			tooltip.getContent().setAbsolutePosition(getConstrainedX(), getConstrainedY());
			tooltip.draw();
		}
	}
	
	private float getConstrainedX() {
		return MathUtils.constrain(ctx.mouseX, 0, ctx.width - tooltip.getContent().getAbsoluteWidth());
	}

	private float getConstrainedY() {
		return MathUtils.constrain(ctx.mouseY, 0, ctx.height - tooltip.getContent().getAbsoluteHeight());
	}
	
}