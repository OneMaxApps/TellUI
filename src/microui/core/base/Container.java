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

//Status: STABLE - Do not modify
//Last Reviewed: 08.11.2025

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
		
		this.layoutManager = requireNonNull(layoutManager, "layoutManager");
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
	
	public ContentView findByTextId(final String textId) {
		requireNonNull(textId,"textId");
		
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
		throw new NoSuchElementException("ContentView with id [" + id + "] not found");
	}

	public ContentView getByTextId(final String textId) {
		ContentView contentView = findByTextId(textId);
		if(contentView != null) { return contentView; }
		throw new NoSuchElementException("ContentView with text id [" + textId + "] not found");
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
		removeInternal(getById(id));
		
		return this;
	}

	public Container removeByTextId(String textId) {
		removeInternal(getByTextId(textId));
		
		return this;
	}

	public void removeAll() {
		entryList.clear();
		layoutManager.onRemove();
		priorityManager.recalculateMax();
	}

	public List<Entry> getEntryList() {
		return Collections.unmodifiableList(entryList);
	}

	public Mode getMode() {
		return mode;
	}

	public Container setMode(Mode mode) {
		requireNonNull(mode,"mode");

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
	}
	
	public AbstractColor getImageColor() {
		return image.getColor();
	}

	public void setImageColor(AbstractColor color) {
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
		requireNonNull(contentView,"contentView");
		requireNonNull(layoutParams,"layoutParams");
		
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
		requireNonNull(contentView,"contentView");
		
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
			
			this.list = requireNonNull(list,"list");
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
			if(max < 0) {
				throw new IllegalArgumentException("Max priority cannot be less than 0");
			}
			
			this.max = max;
		}
	}
	
	public static enum Mode {
		IGNORE_CONSTRAINTS, RESPECT_CONSTRAINTS;
	}

	public static final record Entry(ContentView contentView, LayoutParams layoutParams) {
		
		public Entry {
			requireNonNull(contentView,"contentView");
			requireNonNull(layoutParams,"layoutParams");
		}
		
	}
	
}