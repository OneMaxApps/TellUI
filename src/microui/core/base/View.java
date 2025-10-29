package microui.core.base;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;
import static microui.Render.getMode;
import static microui.Render.Mode.STRICT;
import static microui.core.base.ContainerManager.canDraw;
import static microui.core.base.ContainerManager.isInitialized;

import microui.core.exception.RenderException;
import microui.core.interfaces.Visible;
import microui.util.Metrics;
import processing.core.PApplet;

//Status: STABLE - Do not modify
//Last Reviewed: 29.10.2025

/**
 * Abstract base class for all visual elements in MicroUI. Provides basic
 * functionality for managing visibility, rendering priority, and element
 * identification.
 * 
 * <p>
 * All visual components must be inherited from this class and implement the
 * {@link #render()} method to determine the rendering logic.
 * </p>
 */
public abstract class View implements Visible {
	public static final String DEFAULT_TEXT_ID = "";
	public static final int DEFAULT_ID = 0;
	public static final int MIN_PRIORITY = 0;
	public static final int MIN_ID = 0;
	protected static final PApplet ctx = getContext();
	private String textId;
	private int priority, id;
	private boolean visible;

	public View() {
		Metrics.register(this);
		id = DEFAULT_ID;
		textId = DEFAULT_TEXT_ID;
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
	public final void setVisible(final boolean visible) {
		this.visible = visible;
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
	public final View setPriority(int priority) {
		if (priority < MIN_PRIORITY) {
			throw new IllegalArgumentException("Priority cannot be less than zero");
		}
		this.priority = priority;

		return this;
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
	 * @param id new identifier (must be between MIN_ID (0) and MAX_ID
	 *           (Integer.MAX_VALUE))
	 * @throws IllegalArgumentException if the identifier is less than 0
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
	 * @param textId new text identifier for this View object (cannot be null and
	 *               (or) empty)
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
	 * 
	 * @return true if id is not equal to DEFAULT_ID (0)
	 */
	public final boolean hasId() {
		return id != DEFAULT_ID;
	}

	/**
	 * 
	 * @return true if text id is not equal to DEFAULT_TEXT_ID ("")
	 */
	public final boolean hasTextId() {
		return !textId.equals(DEFAULT_TEXT_ID);
	}

	/**
	 * Renders the element if it is visible. Automatically manages Processing styles
	 * (push/pop style).
	 * 
	 * <p>
	 * Calls the {@link #render() method} only if the element is visible.
	 */
	public void draw() {
		if (getMode() == STRICT) {
			if (!isInitialized()) {
				throw new RenderException("ContainerManager is not initialized");
			}

			if (!canDraw()) {
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