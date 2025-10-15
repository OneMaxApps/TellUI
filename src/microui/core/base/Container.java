package microui.core.base;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.isDebugModeEnabled;
import static microui.core.base.Container.Mode.RESPECT_CONSTRAINTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import microui.core.ImageBuffer;
import microui.core.exception.RenderException;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.layout.LayoutManager;
import microui.layout.LayoutParams;
import processing.core.PImage;
import processing.event.MouseEvent;

public final class Container extends Component implements KeyPressable, Scrollable {
	private final List<Entry> entryList;
	private final LayoutManager layoutManager;
	private final PriorityManager priorityManager;
	private final ImageBuffer image;
	private Mode mode;

	public Container(LayoutManager layoutManager, float x, float y, float width, float height) {
		super(x, y, width, height);
		setConstrainDimensionsEnabled(false);
		setMinMaxSize(1, 1, width, height);

		setBackgroundColor(Color.TRANSPARENT);

		entryList = new ArrayList<Entry>();

		this.layoutManager = requireNonNull(layoutManager, "layout manager cannot be null");
		layoutManager.setContainer(this);

		priorityManager = new PriorityManager(entryList);
		
		image = new ImageBuffer();
		
		setMode(RESPECT_CONSTRAINTS);
	}

	public Container(LayoutManager layoutManager) {
		this(layoutManager, 0, 0, ctx.width, ctx.height);
	}

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
	 * Provides searching of ContentView objects in this Container
	 * 
	 * @param id unique number of ContentView
	 * @return ContentView object or null if object with this id is not found
	 */
	public ContentView findById(final int id) {
		if(id < 0) {
			throw new IllegalArgumentException("id cannot be negative");
		}
		
		for (int i = 0; i < entryList.size(); i++) {
			ContentView contentView = entryList.get(i).contentView();
			if (contentView.getId() == id) {
				return contentView;
			}
		}
		
		return null;
	}
	
	public ContentView findByTextId(final String textId) {
		if(textId == null) {
			throw new NullPointerException("the textId cannot be null");
		}
		
		if(textId.isEmpty()) {
			throw new IllegalArgumentException("textId cannot be empty");
		}
		
		for (int i = 0; i < entryList.size(); i++) {
			ContentView contentView = entryList.get(i).contentView();
			if (contentView.getTextId().equals(textId)) {
				return contentView;
			}
		}

		return null;
	}
	
	public ContentView getById(final int id) {
		ContentView contentView = findById(id);
		if(contentView != null) { return contentView; }
		throw new IllegalArgumentException("contentView with id: " + id + " is not found in this Container");
	}

	public ContentView getByTextId(final String textId) {
		ContentView contentView = findByTextId(textId);
		if(contentView != null) { return contentView; }
		throw new IllegalArgumentException("contentView with text id: " + textId + " is not found in to this Container");
	}

	public Container add(ContentView contentView, LayoutParams layoutParams, int id) {
		addInternal(contentView, layoutParams);
		contentView.setId(id);
		return this;
	}

	public Container add(ContentView contentView, LayoutParams layoutParams, String textId) {
		addInternal(contentView, layoutParams);
		contentView.setTextId(textId);
		return this;
	}

	public Container add(ContentView contentView, LayoutParams layoutParams) {
		addInternal(contentView, layoutParams);
		return this;
	}

	public Container remove(ContentView contentView) {
		removeInternal(contentView);
		return this;
	}

	public Container removeById(int id) {
		ContentView contentView = findById(id);
		
		if(contentView != null) {
			removeInternal(contentView);
			return this;
		}
		
		throw new IllegalArgumentException("contentView with id: \" " + id + "\" is not found in this Container");
	}

	public Container removeByTextId(String textId) {
		ContentView contentView = findByTextId(textId);
		
		if(contentView != null) {
			removeInternal(contentView);
			return this;
		}
		
		throw new IllegalArgumentException("contentView with text id: \"" + textId + "\" is not found in this Container");
	}

	public void removeAll() {
		entryList.clear();
	}

	public List<Entry> getEntryList() {
		return Collections.unmodifiableList(entryList);
	}

	public Mode getMode() {
		return mode;
	}

	public Container setMode(Mode mode) {
		if (mode == null) {
			throw new NullPointerException("mode for container cannot be null");
		}

		if (this.mode == mode) {
			return this;
		}

		this.mode = mode;

		requestUpdate();

		return this;
	}

	public PImage getImage() {
		return image.get();
	}
	
	public void setImage(PImage image) {
		this.image.set(image);
		this.image.setBoundsFrom(this);
	}
	
	public AbstractColor getImageColor() {
		return image.getColor();
	}

	public void setImageColor(Color color) {
		image.setColor(color);
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
		checkContentViewNotNull(contentView);
		checkLayoutParamsNotNull(layoutParams);
		checkContentViewAlreadyAddedInList(contentView);

		Entry entry = new Entry(contentView, layoutParams);
		entryList.add(entry);
		layoutManager.onAdd(entry);
		
		priorityManager.recalculateMax();
	}

	private void removeInternal(ContentView contentView) {
		checkContentViewNotNull(contentView);
		checkContentViewExistInList(contentView);

		for (int i = 0; i < entryList.size(); i++) {
			ContentView c = entryList.get(i).contentView();
			if (c == contentView) {
				entryList.remove(i);
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
				ContentView contentView = entryList.get(i).contentView();
				if (contentView.getPriority() == priority) {
					contentView.draw();
				}
			}
		}

	}

	private void debugOnDraw() {
		if (isDebugModeEnabled()) {

			ctx.pushStyle();
			ctx.noStroke();
			ctx.fill(0, 0, 255, 32);
			ctx.rect(getAbsoluteX(), getAbsoluteY(), getAbsoluteWidth(), getAbsoluteHeight());
			ctx.popStyle();

			layoutManager.debugOnDraw();
		}
	}

	private void checkContentViewNotNull(ContentView contentView) {
		if (contentView == null) {
			throw new NullPointerException("contentView cannot be null");
		}
	}

	private void checkLayoutParamsNotNull(LayoutParams layoutParams) {
		if (layoutParams == null) {
			throw new NullPointerException("layoutParams cannot be null");
		}
	}

	private void checkContentViewAlreadyAddedInList(ContentView contentView) {
		for (int i = 0; i < entryList.size(); i++) {
			if (entryList.get(i).contentView() == contentView) {
				throw new IllegalArgumentException("contentView cannot be added twice");
			}
		}

	}

	private void backgroundOnDraw() {
		if (image != null && image.isLoaded()) {
			image.draw();
		} else {
			ctx.noStroke();
			getBackgroundColor().apply();
			ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		}
	}

	private void checkContentViewExistInList(ContentView contentView) {
		for (int i = 0; i < entryList.size(); i++) {
			ContentView content = entryList.get(i).contentView();
			if (content == contentView) {
				return;
			}
		}

		throw new IllegalArgumentException("contentView not found in to Container");
	}

	private static final class PriorityManager {
		private final List<Entry> entryList;
		
		private int max;
		
		private PriorityManager(List<Entry> entryList) {
			super();
			
			this.entryList = entryList;
		}

		public int getMax() {
			return max;
		}

		public void recalculateMax() {
			setMax(0);
			for (int i = 0; i < entryList.size(); i++) {
				int priority = entryList.get(i).contentView().getPriority();
				setMax(Math.max(max, priority));
			}
		}
		
		private void setMax(int max) {
			if(max < 0) {
				throw new RenderException("max priority cannot be less than 0");
			}
			
			this.max = max;
		}
	}
	
	public static enum Mode {
		IGNORE_CONSTRAINTS, RESPECT_CONSTRAINTS;
	}

	public static final record Entry(ContentView contentView, LayoutParams layoutParams) {
	}
}