package microui.service;

import microui.core.base.View;
import microui.feedback.Tooltip;
import microui.util.MathUtils;

//Status: STABLE - Do not modify
//Last Reviewed: 21.10.2025
public final class TooltipManager extends View {
	private static final TooltipManager INSTANCE = new TooltipManager();
	private static Tooltip tooltip;

	private TooltipManager() {
		super();
		setVisible(true);
	}

	public static void setTooltip(Tooltip tooltip) {
		if (tooltip == null) {
			throw new NullPointerException("the tooltip cannot be null");
		}
		TooltipManager.tooltip = tooltip;
	}

	public static TooltipManager getInstance() {
		return INSTANCE;
	}

	@Override
	protected void render() {
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