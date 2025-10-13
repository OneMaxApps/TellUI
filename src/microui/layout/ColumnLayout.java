package microui.layout;

import microui.core.base.Container.ContentViewEntry;

public final class ColumnLayout extends LinearAxisLayout {

	@Override
	public void onAddContentView(ContentViewEntry contentViewEntry) {
		super.onAddContentView(contentViewEntry);
		if (isOutOfSpace()) {
			throw new IllegalStateException("weight limit out of bounds in ColumnLayout");
		}
	}

	public ColumnLayout() {
		super();
		setVerticalMode(true);
	}

	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof ColumnLayoutParams)) {
			throw new IllegalArgumentException("using not correct layout params for ColumnLayout");
		}
	}

}