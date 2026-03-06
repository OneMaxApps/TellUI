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

/**
 * Abstract base class for all interactive UI components.
 * Component provides comprehensive interaction handling, event
 * management, tooltip support, and visual styling for UI elements.
 * 
 * <p>
 * This class serves as the foundation for all interactive UI elements and
 * should be extended by specific component implementations.
 * </p>
 * 
 * @see ContentView
 * @see Event
 * @see InteractionHandler
 * @see Tooltip
 */
public abstract class Component extends ContentView {
	private final Event event;
	private final InteractionHandler interactionHandler;
	private final Tooltip tooltip;
	private AbstractColor backgroundColor;

	/**
	 * Constructs a Component with specified position and dimensions. Initializes
	 * with default theme background color and creates event, interaction handler,
	 * and tooltip systems.
	 *
	 * @param x      the x-coordinate of the component's top-left corner
	 * @param y      the y-coordinate of the component's top-left corner
	 * @param width  the width of the component
	 * @param height the height of the component
	 */
	public Component(float x, float y, float width, float height) {
		super(x, y, width, height);

		setBackgroundColor(getTheme().getBackgroundColor());
		event = new Event(this);
		interactionHandler = new InteractionHandler(this);
		tooltip = new Tooltip(this);
		
	}

	/**
	 * Constructs a Component at position (0,0) with zero dimensions. Useful when
	 * bounds will be set later.
	 */
	public Component() {
		this(0, 0, 0, 0);
	}

	/**
	 * Draws the component and processes interactions. This method orchestrates
	 * rendering, event detection, interaction handling, and tooltip management. It
	 * should be called every frame for active components.
	 */
	@Override
	public void draw() {
		if (!isVisible()) {
			return;
		}

		super.draw(); // Render the component's visual representation

		event.listen(); // Detect and process events
		interactionHandler.listen(); // Handle interaction callbacks
		tooltip.listen(); // Manage tooltip display
		
	}

	/**
	 * Gets the background color of the component.
	 *
	 * @return the current background color
	 */
	public final AbstractColor getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Sets the background color of the component.
	 *
	 * @param backgroundColor the background color to set
	 * @return this Component instance for method chaining
	 * @throws NullPointerException if backgroundColor is null
	 */
	public final Component setBackgroundColor(AbstractColor backgroundColor) {
		this.backgroundColor = requireNonNull(backgroundColor, "backgroundColor");
		return this;
	}

	// == STATE QUERY METHODS ==

	/**
	 * Checks if the component is in a pressed state (mouse down over component).
	 *
	 * @return true if component is pressed, false otherwise
	 */
	public boolean isPressed() {
		return event.isPressed();
	}
	
	/**
	 * Checks if the component is in a released state (not pressed).
	 *
	 * @return true if component is released, false otherwise
	 */
	public boolean isReleased() {
		return event.isReleased();
	}
	
	public boolean isHovered() {
		return event.isHover();
	}
	
	public boolean isDragging() {
		return event.isDragging();
	}
	
	// == EVENT QUERY METHODS ==
	
	public boolean isPressEvent() {
		return event.isPress();
	}

	public boolean isReleaseEvent() {
		return event.isRelease();
	}
	
	public boolean isLongPressedEvent() {
		return event.isLongPressed();
	}

	public boolean isMouseEnteredEvent() {
		return event.isEnter();
	}

	public boolean isMouseLeftEvent() {
		return event.isLeave();
	}

	public boolean isMouseEnteredLongEvent() {
		return event.isEnterLong();
	}

	public boolean isMouseLeftLongEvent() {
		return event.isLeaveLong();
	}

	public boolean isClickedEvent() {
		return event.isClicked();
	}

	public boolean isDoubleClickedEvent() {
		return event.isDoubleClicked();
	}

	public boolean isDragStartedEvent() {
		return event.isDragStarted();
	}

	public boolean isDragEndedEvent() {
		return event.isDragEnd();
	}
	
	/**
	 * Checks if the interaction handler is enabled for this component.
	 *
	 * @return true if interaction handling is enabled, false otherwise
	 */
	public final boolean isInteractionHandlerEnabled() {
		return interactionHandler.isEnabled();
	}

	/**
	 * Enables or disables the interaction handler for this component. When
	 * disabled, no interaction events will be processed or triggered.
	 *
	 * @param enabled true to enable interaction handling, false to disable
	 * @return this Component instance for method chaining
	 */
	public final Component setInteractionHandlerEnabled(boolean enabled) {
		interactionHandler.setEnabled(enabled);
		return this;
	}

	// == EVENT LISTENER REGISTRATION METHODS ==

	/**
	 * Registers a listener for hover events (mouse over component).
	 *
	 * @param listener the listener to call when hover occurs
	 * @return this Component instance for method chaining
	 */
	public final Component onHover(Listener listener) {
		interactionHandler.addListener(EventType.HOVER, listener);
		return this;
	}

	/**
	 * Registers a listener for press events (mouse button down over component).
	 *
	 * @param listener the listener to call when pressed
	 * @return this Component instance for method chaining
	 */
	public final Component onPress(Listener listener) {
		interactionHandler.addListener(EventType.PRESS, listener);
		return this;
	}

	/**
	 * Registers a listener for pressed state events (continuous while pressed).
	 *
	 * @param listener the listener to call while pressed
	 * @return this Component instance for method chaining
	 */
	public final Component onPressed(Listener listener) {
		interactionHandler.addListener(EventType.PRESSED, listener);
		return this;
	}

	/**
	 * Registers a listener for release events (mouse button up after press).
	 *
	 * @param listener the listener to call when released
	 * @return this Component instance for method chaining
	 */
	public final Component onRelease(Listener listener) {
		interactionHandler.addListener(EventType.RELEASE, listener);
		return this;
	}

	/**
	 * Registers a listener for released state events (continuous while released).
	 *
	 * @param listener the listener to call while released
	 * @return this Component instance for method chaining
	 */
	public final Component onReleased(Listener listener) {
		interactionHandler.addListener(EventType.RELEASED, listener);
		return this;
	}

	/**
	 * Registers a listener for long press events (extended mouse button hold).
	 *
	 * @param listener the listener to call on long press
	 * @return this Component instance for method chaining
	 */
	public final Component onLongPress(Listener listener) {
		interactionHandler.addListener(EventType.LONG_PRESS, listener);
		return this;
	}

	/**
	 * Registers a listener for mouse enter events (mouse enters component bounds).
	 *
	 * @param listener the listener to call when mouse enters
	 * @return this Component instance for method chaining
	 */
	public final Component onEnter(Listener listener) {
		interactionHandler.addListener(EventType.ENTER, listener);
		return this;
	}

	/**
	 * Registers a listener for mouse leave events (mouse leaves component bounds).
	 *
	 * @param listener the listener to call when mouse leaves
	 * @return this Component instance for method chaining
	 */
	public final Component onLeave(Listener listener) {
		interactionHandler.addListener(EventType.LEAVE, listener);
		return this;
	}

	/**
	 * Registers a listener for long enter events (extended hover).
	 *
	 * @param listener the listener to call on long hover
	 * @return this Component instance for method chaining
	 */
	public final Component onEnterLong(Listener listener) {
		interactionHandler.addListener(EventType.ENTER_LONG, listener);
		return this;
	}

	/**
	 * Registers a listener for long leave events (mouse leaves after long hover).
	 *
	 * @param listener the listener to call when leaving after long hover
	 * @return this Component instance for method chaining
	 */
	public final Component onLeaveLong(Listener listener) {
		interactionHandler.addListener(EventType.LEAVE_LONG, listener);
		return this;
	}

	/**
	 * Registers a listener for click events (press + release combination).
	 *
	 * @param listener the listener to call when clicked
	 * @return this Component instance for method chaining
	 */
	public final Component onClick(Listener listener) {
		interactionHandler.addListener(EventType.CLICK, listener);
		return this;
	}

	/**
	 * Registers a listener for double-click events (two rapid clicks).
	 *
	 * @param listener the listener to call on double-click
	 * @return this Component instance for method chaining
	 */
	public final Component onDoubleClick(Listener listener) {
		interactionHandler.addListener(EventType.DOUBLE_CLICK, listener);
		return this;
	}

	/**
	 * Registers a listener for drag start events (beginning of drag operation).
	 *
	 * @param listener the listener to call when drag starts
	 * @return this Component instance for method chaining
	 */
	public final Component onDragStart(Listener listener) {
		interactionHandler.addListener(EventType.DRAG_START, listener);
		return this;
	}

	/**
	 * Registers a listener for dragging events (continuous during drag).
	 *
	 * @param listener the listener to call while dragging
	 * @return this Component instance for method chaining
	 */
	public final Component onDragging(Listener listener) {
		interactionHandler.addListener(EventType.DRAGGING, listener);
		return this;
	}

	/**
	 * Registers a listener for drag end events (end of drag operation).
	 *
	 * @param listener the listener to call when drag ends
	 * @return this Component instance for method chaining
	 */
	public final Component onDragEnd(Listener listener) {
		interactionHandler.addListener(EventType.DRAG_END, listener);
		return this;
	}

	// == TOOLTIP API ==

	/**
	 * Sets the tooltip content for this component.
	 *
	 * @param tooltipContent the tooltip content to display
	 * @return this Component instance for method chaining
	 */
	public final Component setTooltip(TooltipContent tooltipContent) {
		tooltip.setContent(tooltipContent);
		return this;
	}

	/**
	 * Gets the current tooltip content.
	 *
	 * @return the current tooltip content, or null if no tooltip is set
	 */
	public final TooltipContent getTooltipContent() {
		return tooltip.getContent();
	}

	// == CONVENIENCE METHODS FOR TEXT TOOLTIPS ==

	/**
	 * Sets a text tooltip for this component (convenience method). Creates a
	 * TextTooltipContent with the specified text.
	 *
	 * @param text the text to display in the tooltip
	 * @return this Component instance for method chaining
	 */
	public final Component setTooltip(String text) {
		if (tooltip.getContent() instanceof TextTooltipContent content) {
			content.setText(text);
		} else {
			tooltip.setContent(new TextTooltipContent(text));
		}
		return this;
	}

	/**
	 * Gets the text from the tooltip if it is a TextTooltipContent.
	 *
	 * @return the tooltip text
	 * @throws ClassCastException if the current tooltip content is not a
	 *                            TextTooltipContent
	 */
	public final String getTooltipText() {
		if (tooltip.getContent() instanceof TextTooltipContent content) {
			return content.getText();
		}

		throw new ClassCastException("Tooltip not instance of TextTooltipContent");
	}
}