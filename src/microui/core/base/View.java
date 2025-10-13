package microui.core.base;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;

import microui.MicroUI;
import microui.core.exception.RenderException;
import microui.core.interfaces.Visible;
import microui.util.Metrics;
import processing.core.PApplet;

/**
 * Abstract base class for all visual elements in MicroUI. Provides basic
 * functionality for managing visibility, rendering priority, and element
 * identification.
 * 
 * <p>
 * All visual components must be inherited from this class. and implement the
 * {@link #render()} method to determine the rendering logic.
 * </p>
 */
public abstract class View implements Visible {
	protected static final PApplet ctx = requireNonNull(getContext(),
			"Context (PApplet) for MicroUI is not initialized");
	private static final String DEFAULT_EMPTY_TEXT_ID = "";
	private String textId;
	private int priority, id;
	private boolean isVisible;

	public View() {
		Metrics.register(this);
	}

	/**
	 * Checks whether the element is visible.
	 *
	 * @return true if the element is visible, false if hidden
	 */
	@Override
	public final boolean isVisible() {
		return isVisible;
	}

	/**
	 * Sets the visibility of the element.
	 *
	 * @param isVisible true to show the element, false to hide
	 */
	@Override
	public final void setVisible(final boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Returns the priority of rendering the element. Elements with a higher
	 * priority are drawn on top of others.
	 *
	 * @return current rendering priority (≥ 0)
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * Sets the priority of rendering the element.
	 *
	 * @param priority new rendering priority (must be ≥ 0)
	 * @throws IllegalArgumentException if priority is less than 0
	 */
	public final void setPriority(final int priority) {
		if (priority < 0) {
			throw new IllegalArgumentException("priority cannot be less than zero");
		}
		this.priority = priority;
	}

	/**
	 * Returns the numeric identifier of the element.
	 *
	 * @return numeric element Id (≥ 0)
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Sets the numeric identifier of the element.
	 *
	 * @param id new identifier (must be ≥ 0)
	 * @throws IllegalArgumentException if the identifier is less than 0
	 */
	public final void setId(int id) {
		if (id < 0) {
			throw new IllegalArgumentException("id cannot be negative");
		}
		this.id = id;
	}

	/**
	 * Returns the text identifier of the element.
	 * 
	 * @return text id of the View object
	 */
	public final String getTextId() {
		if (textId == null) {
			return DEFAULT_EMPTY_TEXT_ID;
		}
		return textId;
	}

	/**
	 * Sets text identifier for this View object.
	 * 
	 * @param textId new text identifier for this View object (cannot be null and
	 *               (or) empty)
	 */
	public final void setTextId(final String textId) {
		if (textId == null) {
			throw new IllegalArgumentException("text id cannot be null");
		}
		if (textId.trim().isEmpty()) {
			throw new IllegalArgumentException("text id cannot be empty");
		}

		this.textId = textId;
	}

	/**
	 * Renders the element if it is visible. Automatically manages Processing styles
	 * (push/pop style).
	 * 
	 * <p>
	 * Calls the {@link #render() method} only if the element is visible.
	 */
	public void draw() {
		if (!MicroUI.isFlexibleRenderModeEnabled()) {
			if (!ContainerManager.isInitialized()) {
				throw new RenderException("ContainerManager is not initialized");
			}

			if (!ContainerManager.isCanDraw()) {
				throw new RenderException("Cannot draw outside from ContainerManager");
			}
		}

		if (isVisible()) {
			ctx.pushStyle();
			render();
			ctx.popStyle();
		}

	}

	/**
	 * Abstract method for sub-classes for implementation them drawing logic
	 */
	protected abstract void render();

}