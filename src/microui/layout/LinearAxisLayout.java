package microui.layout;

import microui.core.base.Container.ContentViewEntry;
import microui.core.base.ContentView;

public abstract class LinearAxisLayout extends LayoutManager {
	private static final float EPSILON = .01f;
	private static final float TOTAL_WEIGHT = 1.0f;
	private boolean isVerticalMode;

	@Override
	public void recalculate() {
		float usedWeight = 0;

		float containerX = getContainer().getX();
		float containerY = getContainer().getY();
		float containerW = getContainer().getWidth();
		float containerH = getContainer().getHeight();

		for (ContentViewEntry entry : getContentViewEntryList()) {
			ContentView contentView = entry.contentView();
			LinearAxisLayoutParams params = (LinearAxisLayoutParams) entry.layoutParams();

			float usedSpace = isVerticalMode ? containerH * usedWeight : containerW * usedWeight;

			switch (getContainer().getContainerMode()) {

			case IGNORE_CONSTRAINTS:
				contentView.setConstrainDimensionsEnabled(false);
				contentView.setAbsolutePosition(isVerticalMode ? containerX : containerX + usedSpace,
						isVerticalMode ? containerY + usedSpace : containerY);
				contentView.setAbsoluteWidth(isVerticalMode ? containerW : containerW * params.getWeight());
				contentView.setAbsoluteHeight(isVerticalMode ? containerH * params.getWeight() : containerH);

				break;

			case RESPECT_CONSTRAINTS:
				float alignXLeft = containerX;
				float alignXCenter = containerX + containerW / 2 - contentView.getAbsoluteWidth() / 2;
				float alignXRight = containerX + containerW - contentView.getAbsoluteWidth();

				float alignYTop = containerY;
				float alignYCenter = containerY + containerH / 2 - contentView.getAbsoluteHeight() / 2;
				float alignYBottom = containerY + containerH - contentView.getAbsoluteHeight();

				contentView.setAbsoluteX(isVerticalMode
						? params.getAlignX() == -1 ? alignXLeft : params.getAlignX() == 1 ? alignXRight : alignXCenter
						: containerX + usedSpace);
				contentView.setAbsoluteY(isVerticalMode ? containerY + usedSpace
						: params.getAlignY() == -1 ? alignYTop : params.getAlignY() == 1 ? alignYBottom : alignYCenter);
				contentView.setAbsoluteWidth(isVerticalMode ? containerW : containerW * params.getWeight());
				contentView.setAbsoluteHeight(isVerticalMode ? containerH * params.getWeight() : containerH);

				break;

			}

			usedWeight += params.getWeight();
		}
	}

	@Override
	public void debugOnDraw() {
		ctx.pushStyle();
		ctx.stroke(0);
		ctx.fill(200, 0, 0, 32);

		getContentViewEntryList().forEach(entry -> {
			ContentView contentView = entry.contentView();
			LinearAxisLayoutParams params = (LinearAxisLayoutParams) entry.layoutParams();
			if (isVerticalMode) {
				ctx.rect(getContainer().getX(), contentView.getAbsoluteY(), getContainer().getWidth(),
						getContainer().getHeight() * params.getWeight());
			} else {
				ctx.rect(contentView.getAbsoluteX(), getContainer().getY(),
						getContainer().getWidth() * params.getWeight(), getContainer().getHeight());
			}
		});

		ctx.popStyle();
	}

	@Override
	protected void checkCorrectParams(LayoutParams layoutParams) {
		if (!(layoutParams instanceof LinearAxisLayoutParams)) {
			throw new IllegalArgumentException("using not correct layout params for LinearAxisLayoutParams");
		}
	}

	protected boolean isVerticalMode() {
		return isVerticalMode;
	}

	protected void setVerticalMode(boolean isVerticalMode) {
		if (this.isVerticalMode == isVerticalMode) {
			return;
		}
		this.isVerticalMode = isVerticalMode;
		if (getContainer() != null) {
			recalculate();
		}
	}

	protected boolean isOutOfSpace() {
		float usedWeight = 0;
		for (ContentViewEntry entry : getContentViewEntryList()) {
			LinearAxisLayoutParams params = (LinearAxisLayoutParams) entry.layoutParams();
			usedWeight += params.getWeight();
		}
		if (usedWeight - TOTAL_WEIGHT > EPSILON) {
			return true;
		}

		return false;
	}

}