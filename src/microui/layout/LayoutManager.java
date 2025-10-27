package microui.layout;

import static java.util.Objects.requireNonNull;

import java.util.List;

import microui.MicroUI;
import microui.core.base.Container;
import microui.core.base.Container.Entry;
import processing.core.PApplet;

public abstract class LayoutManager {
	protected PApplet ctx = MicroUI.getContext();
	private Container container;
	private List<Container.Entry> entryList;

	public abstract void recalculate();

	public abstract void debugOnDraw();

	public void onAdd(Entry entry) {
		checkCorrectParams(entry.layoutParams());
		recalculate();
	}

	public void onRemove() {
		recalculate();
	}

	public final Container getContainer() {
		return container;
	}

	public final void setContainer(Container container) {
		this.container = requireNonNull(container,"container");
		entryList = container.getEntryList();
	}

	protected final List<Entry> getEntryList() {
		return entryList;
	}

	protected abstract void checkCorrectParams(LayoutParams layoutParams);
}