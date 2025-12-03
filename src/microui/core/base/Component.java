package microui.core.base;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.style.AbstractColor;
import microui.event.Event;
import microui.event.EventType;
import microui.event.InteractionHandler;
import microui.event.Listener;
import microui.feedback.TextTooltipContent;
import microui.feedback.Tooltip;
import microui.feedback.TooltipContent;

//Status: STABLE - Do not modify
//Last Reviewed: 04.12.2025

public abstract class Component extends ContentView {
	private final Event event;
	private final InteractionHandler interactionHandler;
	private final Tooltip tooltip;
	private AbstractColor backgroundColor;

	public Component(float x, float y, float width, float height) {
		super(x, y, width, height);

		setBackgroundColor(getTheme().getBackgroundColor());
		event = new Event(this);
		interactionHandler = new InteractionHandler(this);
		tooltip = new Tooltip(this);

	}

	public Component() {
		this(0, 0, 0, 0);
	}

	@Override
	public void draw() {
		if (!isVisible()) {
			return;
		}

		super.draw();

		event.listen();

		interactionHandler.listen();

		tooltip.listen();

	}

	public final AbstractColor getBackgroundColor() {
		return backgroundColor;
	}

	public final Component setBackgroundColor(AbstractColor backgroundColor) {
		this.backgroundColor = requireNonNull(backgroundColor,"backgroundColor");

		return this;
	}

	public boolean isPress() {
		return event.isPress();
	}

	public boolean isRelease() {
		return event.isRelease();
	}

	public boolean isLongPress() {
		return event.isLongPress();
	}

	public boolean isEnter() {
		return event.isEnter();
	}

	public boolean isLeave() {
		return event.isLeave();
	}

	public boolean isEnterLong() {
		return event.isEnterLong();
	}

	public boolean isLeaveLong() {
		return event.isLeaveLong();
	}

	public boolean isClick() {
		return event.isClick();
	}

	public boolean isDoubleClick() {
		return event.isDoubleClick();
	}

	public boolean isDragStart() {
		return event.isDragStart();
	}

	public boolean isDragging() {
		return event.isDragging();
	}

	public boolean isDragEnd() {
		return event.isDragEnd();
	}

	public boolean isPressed() {
		return event.isPressed();
	}

	public boolean isReleased() {
		return event.isReleased();
	}

	public boolean isHover() {
		return event.isHover();
	}
	
	
	public final boolean isInteractionHandlerEnabled() {
		return interactionHandler.isEnabled();
	}
	
	public final Component setInteractionHandlerEnabled(boolean enabled) {
		interactionHandler.setEnabled(enabled);
		
		return this;
	}

	public final Component onHover(Listener listener) {
		interactionHandler.addListener(EventType.HOVER, listener);

		return this;
	}

	public final Component onPress(Listener listener) {
		interactionHandler.addListener(EventType.PRESS, listener);

		return this;
	}

	public final Component onPressed(Listener listener) {
		interactionHandler.addListener(EventType.PRESSED, listener);

		return this;
	}

	public final Component onRelease(Listener listener) {
		interactionHandler.addListener(EventType.RELEASE, listener);

		return this;
	}

	public final Component onReleased(Listener listener) {
		interactionHandler.addListener(EventType.RELEASED, listener);

		return this;
	}

	public final Component onLongPress(Listener listener) {
		interactionHandler.addListener(EventType.LONG_PRESS, listener);

		return this;
	}

	public final Component onEnter(Listener listener) {
		interactionHandler.addListener(EventType.ENTER, listener);

		return this;
	}

	public final Component onLeave(Listener listener) {
		interactionHandler.addListener(EventType.LEAVE, listener);

		return this;
	}

	public final Component onEnterLong(Listener listener) {
		interactionHandler.addListener(EventType.ENTER_LONG, listener);

		return this;
	}

	public final Component onLeaveLong(Listener listener) {
		interactionHandler.addListener(EventType.LEAVE_LONG, listener);

		return this;
	}

	public final Component onClick(Listener listener) {
		interactionHandler.addListener(EventType.CLICK, listener);

		return this;
	}

	public final Component onDoubleClick(Listener listener) {
		interactionHandler.addListener(EventType.DOUBLE_CLICK, listener);

		return this;
	}

	public final Component onDragStart(Listener listener) {
		interactionHandler.addListener(EventType.DRAG_START, listener);

		return this;
	}

	public final Component onDragging(Listener listener) {
		interactionHandler.addListener(EventType.DRAGGING, listener);

		return this;
	}

	public final Component onDragEnd(Listener listener) {
		interactionHandler.addListener(EventType.DRAG_END, listener);

		return this;
	}

	// NEW TOOLTIP API //////////////////////////////////

	public final Component setTooltip(TooltipContent tooltipContent) {
		tooltip.setContent(tooltipContent);
		return this;
	}

	public final TooltipContent getTooltipContent() {
		return tooltip.getContent();
	}

	// Sugar API
	public final Component setTooltip(String text) {
		if (tooltip.getContent() instanceof TextTooltipContent content) {
			content.setText(text);
		} else {
			tooltip.setContent(new TextTooltipContent(text));
		}
		return this;
	}

	public final String getTooltipText() {
		if (tooltip.getContent() instanceof TextTooltipContent content) {
			return content.getText();
		}

		throw new ClassCastException("Tooltip not instance of TooltipTextViewContent");
	}

}