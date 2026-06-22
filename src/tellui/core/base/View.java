package tellui.core.base;

import static java.util.Objects.requireNonNull;
import static tellui.TellUI.getContext;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import processing.core.PApplet;
import tellui.core.exception.DuplicateItemException;
import tellui.core.interfaces.Listener;
import tellui.core.interfaces.Visible;
import tellui.util.Metrics;

/**
 * Abstract base class for all visual elements in TellUI. Provides fundamental
 * functionality for managing visibility, rendering priority, and element
 * identification within the GUI framework.
 * 
 * <p>
 * The View class serves as the foundation for all visual components in TellUI.
 * Subclasses must implement the {@link #render()} method to define their
 * specific drawing logic. This class implements the {@link Visible} interface
 * and provides built-in support for metrics collection.
 * </p>
 * 
 * @see Visible
 * @see Metrics
 */
public abstract class View implements Visible {
	/** Default text id ("") for View objects */
	public static final String DEFAULT_TEXT_ID = "";
	/** Default id (0) for View objects */
	public static final int DEFAULT_ID = 0;
	/** Minimal priority (0) for Z-order in rendering */
	public static final int MIN_PRIORITY = 0;
	/** Minimal id (0) for View objects */
	public static final int MIN_ID = 0;
	/** Ignoring internal components in components with this id */
	public static final int IGNORE_INTERNAL_COMPONENT_ID = Integer.MAX_VALUE;
	/** Id for items of modal components */
	public static final int MODAL_ITEM_VIEW_ID = Integer.MAX_VALUE-1;
	
	/** Instance of context by Processing */
	protected static final PApplet ctx = getContext();

	private final List<Listener> onChangePriorityListenerList;
	
	private String textId;
	private int priority, id;
	private boolean visible;

	/**
	 * Constructs a new View with default values. Automatically registers the view
	 * with the metrics system.
	 */
	public View() {
		Metrics.register(this);
		
		onChangePriorityListenerList = new ArrayList<Listener>();
		
		id = DEFAULT_ID;
		textId = DEFAULT_TEXT_ID;
	}
	
	/**
	 * Add a listener that is called when the rendering priority of this view changes.
	 *  
	 * @param listener a new listener for priority (cannot be {@code null}).
	 * @throws NullPointerException if listener is null
	 * @throws DuplicateItemException if listener already added
	 */
	public final void addOnChangePriorityListener(Listener listener) {
		requireNonNull(listener,"listener");
		
		if (onChangePriorityListenerList.contains(listener)) {
			throw new DuplicateItemException("Listener already added");
		}
		
		onChangePriorityListenerList.add(listener);
	}
	
	/**
	 * Removes a previously added priority change listener.
	 * 
	 * @param listener a listener for remove (cannot be {@code null}).
	 * @throws NullPointerException if listener is null
	 * @throws NoSuchElementException if listener has not been added
	 */
	public final void removeOnChangePriorityListener(Listener listener) {
		requireNonNull(listener,"listener");
		
		if (!onChangePriorityListenerList.contains(listener)) {
			throw new NoSuchElementException("Listener not found");
		}
		
		onChangePriorityListenerList.remove(listener);
	}

	/**
	 * Checks whether the element is visible.
	 *
	 * @return true if the element is visible, false if hidden
	 */
	@Override
	public final boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the visibility of the element.
	 *
	 * @param visible true to show the element, false to hide
	 */
	@Override
	public final void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns the priority of rendering the element. Elements with a higher
	 * priority are drawn on top of others.
	 *
	 * @return current rendering priority (≥ {@value #MIN_PRIORITY})
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * Sets the priority of rendering the element.
	 *
	 * @param priority new rendering priority (must be ≥ {@value #MIN_PRIORITY})
	 * @return this View for method chaining
	 * @throws IllegalArgumentException if priority is less than {@value #MIN_PRIORITY}
	 */
	public final View setPriority(int priority) {
		if (this.priority == priority) {
			return this;
		}
		
		if (priority < MIN_PRIORITY) {
			throw new IllegalArgumentException("Priority cannot be less than " + MIN_PRIORITY);
		}
		
		this.priority = priority;

		notifyOnChangePriority();
		
		return this;
	}

	/**
	 * Returns the numeric identifier of the element.
	 *
	 * @return numeric element Id (≥ {@value #MIN_ID})
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Sets the numeric identifier of the element.
	 *
	 * @param id new identifier (must be greater than MIN_ID ({@value #MIN_ID})
	 * @return this View for method chaining
	 * @throws IllegalArgumentException if the identifier is less than {@value #MIN_ID}
	 */
	public final View setId(int id) {
		if (id < MIN_ID) {
			throw new IllegalArgumentException("Id cannot be negative");
		}
		this.id = id;

		return this;
	}

	/**
	 * Returns the text identifier of the element.
	 * 
	 * @return text id of the View object
	 */
	public final String getTextId() {
		return textId;
	}

	/**
	 * Sets text identifier for this View object.
	 * 
	 * @param textId new text identifier for this View object (cannot be null or empty)
	 * @return this View for method chaining
	 * @throws NullPointerException     if textId is null
	 * @throws IllegalArgumentException if textId is blank
	 */
	public final View setTextId(final String textId) {
		requireNonNull(textId, "textId");

		if (textId.isBlank()) {
			throw new IllegalArgumentException("Text id cannot be blank");
		}

		this.textId = textId;

		return this;
	}

	/**
	 * Checks if this View has a non-default numeric identifier.
	 * 
	 * @return true if id is not equal to DEFAULT_ID ({@value #DEFAULT_ID}), false otherwise
	 */
	public final boolean hasId() {
		return id != DEFAULT_ID;
	}

	/**
	 * Checks if this View has a non-default text identifier.
	 * 
	 * @return true if text id is not equal to DEFAULT_TEXT_ID ({@value #DEFAULT_TEXT_ID}), false otherwise
	 */
	public final boolean hasTextId() {
		return !textId.equals(DEFAULT_TEXT_ID);
	}

	protected void draw() {
		if (isVisible()) {
			ctx.pushStyle();
			render();
			ctx.popStyle();
		}

	}

	/**
	 * Abstract method for subclasses to implement their specific drawing logic.
	 * This method is called by {@link #draw()} when the View is visible.
	 * 
	 * <p>
	 * Subclasses should override this method to define how the View is rendered to
	 * the screen. The Processing context is automatically prepared with proper
	 * style management (push/pop).
	 * </p>
	 */
	protected abstract void render();

	private void notifyOnChangePriority() {
		for (int i = 0; i < onChangePriorityListenerList.size(); i++) {
			onChangePriorityListenerList.get(i).action();
		}
	}
}