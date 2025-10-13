package microui.core.base;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.MicroUI.isDebugModeEnabled;
import static microui.constants.ContainerMode.RESPECT_CONSTRAINTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import microui.constants.ContainerMode;
import microui.core.ImageBuffer;
import microui.core.interfaces.KeyPressable;
import microui.core.interfaces.Scrollable;
import microui.core.style.Color;
import microui.layout.LayoutManager;
import microui.layout.LayoutParams;
import processing.core.PImage;
import processing.event.MouseEvent;

public final class Container extends Component implements KeyPressable, Scrollable {
	private final List<ContentViewEntry> contentViewEntryList;
	private final LayoutManager layoutManager;
	private ImageBuffer backgroundImage;
	private ContainerMode containerMode;
	private int maxPriority;

	public Container(LayoutManager layoutManager, float x, float y, float width, float height) {
		super(x, y, width, height);
		setConstrainDimensionsEnabled(false);
		setMinMaxSize(1, 1, width, height);

		setBackgroundColor(Color.TRANSPARENT);

		contentViewEntryList = new ArrayList<ContentViewEntry>();

		this.layoutManager = requireNonNull(layoutManager, "layout manager cannot be null");
		layoutManager.setContainer(this);

		setContainerMode(RESPECT_CONSTRAINTS);

	}

	public Container(LayoutManager layoutManager) {
		this(layoutManager, 0, 0, ctx.width, ctx.height);
	}

	@Override
	protected void render() {
		backgroundOnDraw();

		debugOnDraw();

		contentViewsOnDraw();
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		if (contentViewEntryList.isEmpty()) {
			return;
		}

		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView contentView = contentViewEntryList.get(i).contentView();
			if (contentView instanceof Scrollable scrollable) {
				scrollable.mouseWheel(event);
			}
		}

	}

	@Override
	public void keyPressed() {
		if (contentViewEntryList.isEmpty()) {
			return;
		}

		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView contentView = contentViewEntryList.get(i).contentView();
			if (contentView instanceof KeyPressable pressable) {
				pressable.keyPressed();
			}
		}

	}

	public ContentView getContentViewById(final int id) {
		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView contentView = contentViewEntryList.get(i).contentView();
			if (contentView.getId() == id) {
				return contentView;
			}
		}

		throw new IllegalArgumentException("contentView with id: " + id + " is not found in to this Container");
	}

	public Container getContainerById(final int id) {
		return (Container) getContentViewById(id);
	}

	public ContentView getContentViewByTextId(final String textId) {
		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView contentView = contentViewEntryList.get(i).contentView();
			if (contentView.getTextId().equals(textId)) {
				return contentView;
			}
		}

		throw new IllegalArgumentException("contentView with text id: " + textId + " is not found in to this Container");
	}

	public ContentView getContainerByTextId(final String textId) {
		return (Container) getContentViewByTextId(textId);
	}

	public Container addContentView(ContentView contentView, LayoutParams layoutParams, int id) {
		addContentViewSafe(contentView, layoutParams);
		contentView.setId(id);
		return this;
	}

	public Container addContentView(ContentView contentView, LayoutParams layoutParams, String textId) {
		addContentViewSafe(contentView, layoutParams);
		contentView.setTextId(textId);
		return this;
	}

	public Container addContentView(ContentView contentView, LayoutParams layoutParams) {
		addContentViewSafe(contentView, layoutParams);
		return this;
	}

	public Container removeContentView(ContentView contentView) {
		removeContentViewSafe(contentView);
		return this;
	}

	public Container removeContentViewById(int id) {
		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView contentView = contentViewEntryList.get(i).contentView();
			if (contentView.getId() == id) {
				removeContentViewSafe(contentView);
				return this;
			}
		}

		throw new IllegalArgumentException("contentView with id: \" " + id + "\" is not found in to Container");
	}

	public Container removeContentViewByTextId(String textId) {
		if (textId == null) {
			throw new NullPointerException("textId cannot be null");
		}

		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView contentView = contentViewEntryList.get(i).contentView();
			if (contentView.getTextId().equals(textId)) {
				removeContentViewSafe(contentView);
				return this;
			}
		}

		throw new IllegalArgumentException("contentView with text id: \"" + textId + "\" is not found in to Container");
	}

	public void removeAllContentViews() {
		contentViewEntryList.clear();
	}

	public List<ContentViewEntry> getContentViewEntryList() {
		return Collections.unmodifiableList(contentViewEntryList);
	}

	public ContainerMode getContainerMode() {
		return containerMode;
	}

	public Container setContainerMode(ContainerMode containerMode) {
		if (containerMode == null) {
			throw new NullPointerException("containerMode cannot be null");
		}

		if (this.containerMode == containerMode) {
			return this;
		}

		this.containerMode = containerMode;

		requestUpdate();

		return this;
	}

	public void setBackgroundImage(PImage image) {
		ensureBackgroundImage();

		if (image == backgroundImage.get()) {
			return;
		}

		backgroundImage.set(image);
		requestUpdate();
	}

	public void setBackgroundImageColor(Color color) {
		ensureBackgroundImage();
		backgroundImage.setColor(color);
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();

		if (layoutManager != null) {
			layoutManager.recalculate();
		}

		if (backgroundImage != null) {
			backgroundImage.setBounds(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		}
	}

	private void recalculateMaxPriority() {
		maxPriority = 0;
		for (int i = 0; i < contentViewEntryList.size(); i++) {
			int priority = contentViewEntryList.get(i).contentView().getPriority();
			maxPriority = max(maxPriority, priority);
		}
	}

	private void addContentViewSafe(ContentView contentView, LayoutParams layoutParams) {
		checkContentViewNotNull(contentView);
		checkLayoutParamsNotNull(layoutParams);
		checkContentViewAlreadyAddedInList(contentView);

		ContentViewEntry contentViewEntry = new ContentViewEntry(contentView, layoutParams);
		contentViewEntryList.add(contentViewEntry);
		layoutManager.onAddContentView(contentViewEntry);

		recalculateMaxPriority();
	}

	private void contentViewsOnDraw() {
		if (contentViewEntryList.isEmpty()) {
			return;
		}

		for (int priority = 0; priority <= maxPriority; priority++) {
			for (int i = 0; i < contentViewEntryList.size(); i++) {
				ContentView contentView = contentViewEntryList.get(i).contentView();
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
		for (int i = 0; i < contentViewEntryList.size(); i++) {
			if (contentViewEntryList.get(i).contentView() == contentView) {
				throw new IllegalArgumentException("contentView cannot be added twice");
			}
		}

	}

	private void backgroundOnDraw() {
		if (backgroundImage != null && backgroundImage.isLoaded()) {
			backgroundImage.draw();
		} else {
			ctx.noStroke();
			getBackgroundColor().apply();
			ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		}
	}

	private void ensureBackgroundImage() {
		if (backgroundImage != null) {
			return;
		}
		backgroundImage = new ImageBuffer();
	}

	private void removeContentViewSafe(ContentView contentView) {
		checkContentViewNotNull(contentView);
		checkContentViewExistInList(contentView);

		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView c = contentViewEntryList.get(i).contentView();
			if (c == contentView) {
				contentViewEntryList.remove(i);
			}
		}

		layoutManager.onRemoveContentView();

		recalculateMaxPriority();

	}

	private void checkContentViewExistInList(ContentView contentView) {
		for (int i = 0; i < contentViewEntryList.size(); i++) {
			ContentView content = contentViewEntryList.get(i).contentView();
			if (content == contentView) {
				return;
			}
		}

		throw new IllegalArgumentException("contentView not found in to Container");
	}
	
	public static final record ContentViewEntry(ContentView contentView, LayoutParams layoutParams) {}
}