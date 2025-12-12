package microui.feedback;

import static java.util.Objects.requireNonNull;

import microui.core.base.Component;
import microui.core.base.View;
import microui.service.TooltipManager;

/**
 * Tooltip manager that displays contextual information when users hover over components.
 * Manages tooltip visibility, content, and lifecycle in coordination with TooltipManager service.
 * <p>
 * The Tooltip class handles the display of tooltip content when users hover over associated
 * components. It automatically shows on enter-long events and hides on leave or press events.
 * Tooltip visibility is managed through the centralized TooltipManager service.
 * </p>
 * <p>
 * Status: STABLE - Do not modify
 * Last Reviewed: 21.10.2025
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see Component
 * @see TooltipContent
 * @see TooltipManager
 */
public final class Tooltip extends View {
	/** The content to display in this tooltip. */
	private TooltipContent content;
	/** Flag indicating the tooltip should be closed. */
	private boolean isMustBeClosed;

	/**
	 * Constructs a Tooltip associated with the specified component.
	 * Sets up event listeners to automatically show/hide the tooltip.
	 * 
	 * @param component the component this tooltip is associated with (cannot be null)
	 * @throws NullPointerException if component is null
	 */
	public Tooltip(Component component) {
		super();
		setVisible(false);

		requireNonNull(component,"component");

		// Show tooltip on enter-long (hover for extended time)
		component.onEnterLong(() -> {
			if (content != null && content.isPreparedShow()) {
				setVisible(true);
			}
		});

		// Schedule closure on mouse leave
		component.onLeave(() -> {
			isMustBeClosed = true;
		});

		// Schedule closure on mouse press
		component.onPress(() -> {
			isMustBeClosed = true;
		});

	}

	/**
	 * Updates tooltip state and manages visibility.
	 * Should be called every frame to handle tooltip lifecycle.
	 */
	public void listen() {
		// Close tooltip if scheduled and content allows closure
		if (isMustBeClosed && content != null && content.isPreparedClose()) {
			setVisible(false);
			isMustBeClosed = false;
		}

		// Register with TooltipManager if visible
		if (isVisible()) {
			TooltipManager.setTooltip(this);
		}
	}

	/**
	 * Returns the current tooltip content.
	 * 
	 * @return the tooltip content, or null if no content is set
	 */
	public final TooltipContent getContent() {
		return content;
	}

	/**
	 * Sets the content to display in this tooltip.
	 * 
	 * @param content the tooltip content to display (cannot be null)
	 * @throws NullPointerException if content is null
	 */
	public void setContent(TooltipContent content) {
		this.content = requireNonNull(content,"content");
		content.setTooltip(this);
	}

	/**
	 * Renders the tooltip by delegating to the content's draw method.
	 */
	@Override
	protected void render() {
		if (content != null) {
			content.draw();
		}
	}

}