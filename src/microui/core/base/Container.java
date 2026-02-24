package microui.core.base;

import static java.util.Objects.requireNonNull;
import static microui.core.base.Container.Mode.RESPECT_CONSTRAINTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import microui.core.ImageBuffer;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.layout.LayoutManager;
import microui.layout.LayoutParams;
import microui.util.Debugger;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * Container component for organizing and managing GUI components. Provides
 * functionality for managing child elements (ContentView) through a layout
 * system (LayoutManager), event handling, and rendering priority management.
 * <p>
 * The container supports two operation modes: {@link Mode#RESPECT_CONSTRAINTS}
 * (respect constraints) and {@link Mode#IGNORE_CONSTRAINTS} (ignore
 * constraints). Default mode is {@code RESPECT_CONSTRAINTS}.
 * </p>
 * <p>
 * Implements {@link KeyPressable} and {@link Scrollable} interfaces to
 * propagate keyboard and scroll events to child components.
 * </p>
 * <p>
 * Status: STABLE - Do not modify Last Reviewed: 08.11.2025
 * </p>
 * 
 * @see ContentView
 * @see LayoutManager
 * @see LayoutParams
 */
public final class Container extends Component implements KeyPressable, Scrollable {
	private final List<Entry> entryList;
	private final LayoutManager layoutManager;
	private final PriorityManager priorityManager;
	private final ImageBuffer image;
	private Mode mode;

	/**
	 * Constructs a Container with specified layout manager and dimensions.
	 * 
	 * @param layoutManager the layout manager to use (cannot be null)
	 * @param x             the x-coordinate of the container
	 * @param y             the y-coordinate of the container
	 * @param width         the width of the container
	 * @param height        the height of the container
	 * @throws NullPointerException if layoutManager is null
	 */
	public Container(LayoutManager layoutManager, float x, float y, float width, float height) {
		super(x, y, width, height);
		setConstrainDimensionsEnabled(false);
		setMinMaxSize(1, 1, width, height);
		setBackgroundColor(Color.TRANSPARENT);

		entryList = new ArrayList<Entry>();

		this.layoutManager = requireNonNull(layoutManager, "layoutManager");
		layoutManager.setContainer(this);

		priorityManager = new PriorityManager(entryList);

		image = new ImageBuffer();

		setMode(RESPECT_CONSTRAINTS);
	}

	/**
	 * Constructs a Container with specified layout manager using default dimensions
	 * (full context width and height).
	 * 
	 * @param layoutManager the layout manager to use (cannot be null)
	 * @throws NullPointerException if layoutManager is null
	 */
	public Container(LayoutManager layoutManager) {
		this(layoutManager, 0, 0, ctx.width, ctx.height);
	}

	/**
	 * Handles mouse wheel events by propagating them to scrollable child
	 * components.
	 * 
	 * @param event the mouse wheel event
	 */
	@Override
	public void mouseWheel(MouseEvent event) {
		if (entryList.isEmpty()) {
			return;
		}

		for (int i = 0; i < entryList.size(); i++) {
			ContentView contentView = entryList.get(i).contentView();
			if (contentView instanceof Scrollable scrollable) {
				scrollable.mouseWheel(event);
			}
		}

	}

	/**
	 * Handles key press events by propagating them to key-pressable child
	 * components.
	 */
	@Override
	public void keyPressed() {
		if (entryList.isEmpty()) {
			return;
		}

		for (int i = 0; i < entryList.size(); i++) {
			ContentView contentView = entryList.get(i).contentView();
			if (contentView instanceof KeyPressable pressable) {
				pressable.keyPressed();
			}
		}

	}

	/**
	 * Handles key press events with KeyEvent details by propagating them to
	 * key-pressable child components.
	 * 
	 * @param e the key event
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (entryList.isEmpty()) {
			return;
		}

		for (int i = 0; i < entryList.size(); i++) {
			ContentView contentView = entryList.get(i).contentView();
			if (contentView instanceof KeyPressable pressable) {
				pressable.keyPressed(e);
			}
		}
	}

	/**
	 * Provides searching of ContentView objects in this Container
	 * 
	 * @param id unique number of ContentView
	 * @return ContentView object or null if object with this id not found
	 */
	public ContentView findById(final int id) {
		for (int i = 0; i < entryList.size(); i++) {
			final ContentView contentView = entryList.get(i).contentView();
			if (contentView.getId() == id) {
				return contentView;
			}
		}

		return null;
	}

	/**
	 * Searches for a ContentView by its text ID.
	 * 
	 * @param textId the text identifier of the ContentView (cannot be null)
	 * @return the ContentView with the specified text ID, or null if not found
	 * @throws NullPointerException if textId is null
	 */
	public ContentView findByTextId(final String textId) {
		requireNonNull(textId, "textId");

		for (int i = 0; i < entryList.size(); i++) {
			ContentView contentView = entryList.get(i).contentView();
			if (contentView.getTextId().equals(textId)) {
				return contentView;
			}
		}

		return null;
	}

	/**
	 * Retrieves a ContentView by its numeric ID.
	 * 
	 * @param id the unique numeric identifier of the ContentView
	 * @return the ContentView with the specified ID
	 * @throws NoSuchElementException if no ContentView with the specified ID is
	 *                                found
	 */
	public ContentView getById(final int id) {
		ContentView contentView = findById(id);
		if (contentView != null) {
			return contentView;
		}
		throw new NoSuchElementException("ContentView with id [" + id + "] not found");
	}

	/**
	 * Retrieves a ContentView by its text ID.
	 * 
	 * @param textId the text identifier of the ContentView (cannot be null)
	 * @return the ContentView with the specified text ID
	 * @throws NullPointerException   if textId is null
	 * @throws NoSuchElementException if no ContentView with the specified text ID
	 *                                is found
	 */
	public ContentView getByTextId(final String textId) {
		ContentView contentView = findByTextId(textId);
		if (contentView != null) {
			return contentView;
		}
		throw new NoSuchElementException("ContentView with text id [" + textId + "] not found");
	}

	/**
	 * Adds a ContentView to the container with specified layout parameters and
	 * numeric ID.
	 * 
	 * @param contentView  the component to add (cannot be null)
	 * @param layoutParams the layout parameters for the component (cannot be null)
	 * @param id           the numeric identifier for the component
	 * @return this container for method chaining
	 * @throws NullPointerException     if contentView or layoutParams is null
	 * @throws IllegalArgumentException if contentView is already added or is the
	 *                                  container itself
	 */
	public Container add(ContentView contentView, LayoutParams layoutParams, int id) {
		addInternal(contentView, layoutParams);
		contentView.setId(id);
		return this;
	}

	/**
	 * Adds a ContentView to the container with specified layout parameters and text
	 * ID.
	 * 
	 * @param contentView  the component to add (cannot be null)
	 * @param layoutParams the layout parameters for the component (cannot be null)
	 * @param textId       the text identifier for the component (cannot be null)
	 * @return this container for method chaining
	 * @throws NullPointerException     if contentView, layoutParams, or textId is
	 *                                  null
	 * @throws IllegalArgumentException if contentView is already added or is the
	 *                                  container itself
	 */
	public Container add(ContentView contentView, LayoutParams layoutParams, String textId) {
		addInternal(contentView, layoutParams);
		contentView.setTextId(textId);
		return this;
	}

	/**
	 * Adds a ContentView to the container with specified layout parameters.
	 * 
	 * @param contentView  the component to add (cannot be null)
	 * @param layoutParams the layout parameters for the component (cannot be null)
	 * @return this container for method chaining
	 * @throws NullPointerException     if contentView or layoutParams is null
	 * @throws IllegalArgumentException if contentView is already added or is the
	 *                                  container itself
	 */
	public Container add(ContentView contentView, LayoutParams layoutParams) {
		addInternal(contentView, layoutParams);
		return this;
	}

	/**
	 * Removes a ContentView from the container.
	 * 
	 * @param contentView the component to remove (cannot be null)
	 * @return this container for method chaining
	 * @throws NullPointerException   if contentView is null
	 * @throws NoSuchElementException if the component is not found in the container
	 */
	public Container remove(ContentView contentView) {
		removeInternal(contentView);
		return this;
	}

	/**
	 * Removes a ContentView from the container by its numeric ID.
	 * 
	 * @param id the numeric identifier of the component to remove
	 * @return this container for method chaining
	 * @throws NoSuchElementException if no component with the specified ID is found
	 */
	public Container removeById(int id) {
		removeInternal(getById(id));

		return this;
	}

	/**
	 * Removes a ContentView from the container by its text ID.
	 * 
	 * @param textId the text identifier of the component to remove (cannot be null)
	 * @return this container for method chaining
	 * @throws NullPointerException   if textId is null
	 * @throws NoSuchElementException if no component with the specified text ID is
	 *                                found
	 */
	public Container removeByTextId(String textId) {
		removeInternal(getByTextId(textId));

		return this;
	}

	/**
	 * Removes all child components from the container.
	 */
	public void removeAll() {
		entryList.clear();
		layoutManager.onRemove();
		priorityManager.recalculateMax();
	}

	/**
	 * Returns an unmodifiable view of the entry list.
	 * 
	 * @return an unmodifiable list of container entries
	 */
	public List<Entry> getEntryList() {
		return Collections.unmodifiableList(entryList);
	}

	/**
	 * Returns the current container mode.
	 * 
	 * @return the current mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets the container mode.
	 * 
	 * @param mode the mode to set (cannot be null)
	 * @return this container for method chaining
	 * @throws NullPointerException if mode is null
	 */
	public Container setMode(Mode mode) {
		requireNonNull(mode, "mode");

		if (this.mode == mode) {
			return this;
		}

		this.mode = mode;

		requestUpdate();

		return this;
	}

	/**
	 * Returns the background image of the container.
	 * 
	 * @return the background image, or null if no image is set
	 */
	public PImage getImage() {
		return image.get();
	}

	/**
	 * Sets the background image of the container.
	 * 
	 * @param image the image to set as background
	 */
	public void setImage(PImage image) {
		this.image.set(image);
	}

	/**
	 * Returns the color applied to the background image.
	 * 
	 * @return the image color
	 */
	public AbstractColor getImageColor() {
		return image.getColor();
	}

	/**
	 * Sets the color to apply to the background image.
	 * 
	 * @param color the color to apply
	 */
	public void setImageColor(AbstractColor color) {
		image.setColor(color);
	}

	/**
	 * Changing mode of constraints
	 */
	public void respectConstraints() {
		setMode(Mode.RESPECT_CONSTRAINTS);
	}
	
	/**
	 * Changing mode of constraints
	 */
	public void ignoreConstraints() {
		setMode(Mode.IGNORE_CONSTRAINTS);
	}
	
	/**
	 * Checking mode of constraints
	 * @return true if respect constraints, false if isn't
	 */
	public boolean isRespectConstraints() {
		return getMode() == Mode.RESPECT_CONSTRAINTS;
	}
	
	/**
	 * Checking mode of constraints
	 * @return true if ignore constraints, false if isn't
	 */
	public boolean isIgnoreConstraints() {
		return getMode() == Mode.IGNORE_CONSTRAINTS;
	}
	
	/**
	 * Changing mode of constraints on other
	 */
	public void toggleConstraintsMode() {
		setMode(isRespectConstraints() ?  Mode.IGNORE_CONSTRAINTS : Mode.RESPECT_CONSTRAINTS);
	}
	
	@Override
	protected void render() {
		backgroundOnDraw();

		debugOnDraw();

		contentViewsOnDraw();
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (layoutManager != null) {
			layoutManager.recalculate();
		}

		if (image != null) {
			image.setBounds(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		}
	}

	private void addInternal(ContentView contentView, LayoutParams layoutParams) {
		requireNonNull(contentView, "contentView");
		requireNonNull(layoutParams, "layoutParams");

		if (checkListContains(contentView)) {
			throw new IllegalArgumentException("ContentView cannot be added twice");
		}

		if (contentView == this) {
			throw new IllegalArgumentException("Cannot add in container itself");
		}

		final Entry entry = new Entry(contentView, layoutParams);

		entryList.add(entry);
		layoutManager.onAdd(entry);

		priorityManager.recalculateMax();
	}

	private void removeInternal(ContentView contentView) {
		requireNonNull(contentView, "contentView");

		if (!checkListContains(contentView)) {
			throw new NoSuchElementException("ContentView not found");
		}

		for (int i = 0; i < entryList.size(); i++) {
			ContentView c = entryList.get(i).contentView();
			if (c == contentView) {
				entryList.remove(i);
				break;
			}
		}

		layoutManager.onRemove();
		priorityManager.recalculateMax();
	}

	private void contentViewsOnDraw() {
		if (entryList.isEmpty()) {
			return;
		}

		for (int priority = 0; priority <= priorityManager.getMax(); priority++) {
			for (int i = 0; i < entryList.size(); i++) {
				final ContentView c = entryList.get(i).contentView();
				if (c.getPriority() == priority) {
					c.draw();
				}
			}
		}

	}

	private void debugOnDraw() {
		if (Debugger.isEnabled()) {

			ctx.pushStyle();
			ctx.noStroke();
			ctx.fill(0, 0, 255, 32);
			ctx.rect(getAbsoluteX(), getAbsoluteY(), getAbsoluteWidth(), getAbsoluteHeight());
			ctx.popStyle();

			layoutManager.debugOnDraw();
		}
	}

	private boolean checkListContains(ContentView contentView) {
		for (int i = 0; i < entryList.size(); i++) {
			if (entryList.get(i).contentView() == contentView) {
				return true;
			}
		}

		return false;
	}

	private void backgroundOnDraw() {
		if (image.isLoaded()) {
			image.draw();
		} else {
			ctx.noStroke();
			getBackgroundColor().apply();
			ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		}
	}

	private static final class PriorityManager {
		private final List<Entry> list;
		private int max;

		private PriorityManager(List<Entry> list) {
			super();

			this.list = requireNonNull(list, "list");
		}

		public int getMax() {
			return max;
		}

		public void recalculateMax() {
			setMax(0);
			for (int i = 0; i < list.size(); i++) {
				final int priority = list.get(i).contentView().getPriority();
				setMax(Math.max(max, priority));
			}
		}

		private void setMax(int max) {
			if (max < 0) {
				throw new IllegalArgumentException("Max priority cannot be less than 0");
			}

			this.max = max;
		}
	}

	/**
	 * Container operation modes.
	 */
	public static enum Mode {
		/** Ignore component constraints during layout. */
		IGNORE_CONSTRAINTS,
		/** Respect component constraints during layout (default). */
		RESPECT_CONSTRAINTS;
	}
	
	/**
	 * Entry the class for storing the data of content and layout parameters
	 */
	public static final class Entry {
		private final ContentView contentView;
		private final LayoutParams layoutParams;
		
		/**
		 * Constructs of Entry
		 * @param contentView is content for view
		 * @param layoutParams is parameters for layout
		 */
		public Entry(ContentView contentView, LayoutParams layoutParams) {
			this.contentView = requireNonNull(contentView, "contentView");
			this.layoutParams = requireNonNull(layoutParams, "layoutParams");
		}
		
		/**
		 * Returns the contentView object
		 * 
		 * @return the current contentView object
		 */
		public ContentView contentView() {
			return contentView;
		}
		
		/**
		 * Returns the LayoutParams object
		 * 
		 * @return the current LayoutParams object
		 */
		public LayoutParams layoutParams() {
			return layoutParams;
		}
		
	}

}