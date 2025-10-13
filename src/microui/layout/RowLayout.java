package microui.layout;

import microui.core.base.Container.ContentViewEntry;

public final class RowLayout extends LinearAxisLayout {

	@Override
	public void onAddContentView(ContentViewEntry contentViewEntry) {
		super.onAddContentView(contentViewEntry);
		if (isOutOfSpace()) {
			throw new IllegalStateException("weight limit out of bounds in RowLayout");
		}
	}

	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof RowLayoutParams)) {
			throw new IllegalArgumentException("using not correct layout params for RowLayout");
		}
	}

}