package microui.layout;

import java.util.List;

import microui.MicroUI;
import microui.core.base.Container;
import microui.core.base.Container.ContentViewEntry;
import processing.core.PApplet;

public abstract class LayoutManager {
	protected PApplet ctx = MicroUI.getContext();
	private Container container;
	private List<Container.ContentViewEntry> contentViewEntryList;

	public abstract void recalculate();

	public abstract void debugOnDraw();

	public void onAddContentView(ContentViewEntry contentViewEntry) {
		checkCorrectParams(contentViewEntry.layoutParams());
		recalculate();
	}

	public void onRemoveContentView() {
		recalculate();
	}

	public final Container getContainer() {
		return container;
	}

	public final void setContainer(Container container) {
		if (container == null) {
			throw new NullPointerException("container cannot be null");
		}
		this.container = container;
		contentViewEntryList = container.getContentViewEntryList();
	}

	protected final List<ContentViewEntry> getContentViewEntryList() {
		return contentViewEntryList;
	}

	protected abstract void checkCorrectParams(LayoutParams layoutParams);
}