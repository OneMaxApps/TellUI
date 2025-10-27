package microui.feedback;

import static java.util.Objects.requireNonNull;

import microui.core.base.Component;
import microui.core.base.View;
import microui.service.TooltipManager;

//Status: STABLE - Do not modify
//Last Reviewed: 21.10.2025
public final class Tooltip extends View {
	private TooltipContent content;
	private boolean isMustBeClosed;

	public Tooltip(Component component) {
		super();
		setVisible(false);

		requireNonNull(component,"component");

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
		this.content = requireNonNull(content,"content");
		content.setTooltip(this);
	}

	@Override
	protected void render() {
		if (content != null) {
			content.draw();
		}
	}

}