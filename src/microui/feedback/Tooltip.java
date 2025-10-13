package microui.feedback;

import microui.core.base.Component;
import microui.core.base.View;
import microui.service.TooltipManager;
import microui.util.Metrics;

public final class Tooltip extends View {
	private TooltipContent content;
	private boolean isMustBeClosed;

	public Tooltip(Component component) {
		super();
		setVisible(false);

		if (component == null) {
			throw new NullPointerException("the component for Tooltip cannot be null");
		}

		component.onEnterLong(() -> {
			if (content != null && content.isPreparedShow()) {
				setVisible(true);
			}
		});

		component.onLeave(() -> {
			isMustBeClosed = true;
		});

		component.onPress(() -> {
			isMustBeClosed = true;
		});

		Metrics.register(this);

	}

	public void listen() {
		if (isMustBeClosed && content != null && content.isPreparedClose()) {
			setVisible(false);
			isMustBeClosed = false;
		}

		if (isVisible()) {
			TooltipManager.setTooltip(this);
		}
	}

	public final TooltipContent getContent() {
		return content;
	}

	public void setContent(TooltipContent content) {
		if (content == null) {
			throw new NullPointerException("the content for tooltip cannot be null");
		}
		this.content = content;
		content.setTooltip(this);
	}

	@Override
	protected void render() {
		if (content != null) {
			content.draw();
		}
	}

}