package microui.component;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.component.CheckBox.CheckStyle.MARK;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.PROJECT;

import microui.core.AbstractButton;
import microui.core.style.AbstractColor;
import microui.event.Listener;

//Status: STABLE - Do not modify
//Last Reviewed: 17.11.2025

public class CheckBox extends AbstractButton {
	public static final int DEFAULT_SIZE = 16;
	private AbstractColor markColor;
	private Listener onStateChangedListener;
	private CheckStyle checkStyle;
	private boolean checked;

	public CheckBox(float x, float y) {
		super(x, y, DEFAULT_SIZE, DEFAULT_SIZE);
		setMinMaxSize(DEFAULT_SIZE);
		onClick(() -> toggle());
		markColor = getTheme().getPrimaryColor();
		setCheckStyle(MARK);
	}

	public CheckBox(boolean checked) {
		this(0, 0);
		setPositionInCenter();
		setChecked(checked);
	}

	public CheckBox() {
		this(false);
	}

	// == PUBLIC API ==

	public final CheckStyle getCheckStyle() {
		return checkStyle;
	}

	public final void setCheckStyle(CheckStyle checkStyle) {
		this.checkStyle = requireNonNull(checkStyle, "checkStyle");
	}

	public final boolean isChecked() {
		return checked;
	}

	public final void setChecked(boolean checked) {
		if (this.checked == checked) {
			return;
		}
		this.checked = checked;
		notifyOnStateChanged();
	}

	public final void toggle() {
		setChecked(!isChecked());
	}

	public final AbstractColor getMarkColor() {
		return markColor;
	}

	public final void setMarkColor(AbstractColor markColor) {
		this.markColor = requireNonNull(markColor, "markColor");
	}

	public final void setOnStateChangedListener(Listener onStateChangedListener) {
		this.onStateChangedListener = requireNonNull(onStateChangedListener, "onStateChangedListener");
	}

	@Override
	protected void render() {
		super.render();

		getRipplesInternal().draw();
		getHoverInternal().draw();

		if (checked) {
			markOnDraw();
		}
	}

	private void setPositionInCenter() {
		setPosition(ctx.width / 2 - DEFAULT_SIZE / 2, ctx.height / 2 - DEFAULT_SIZE / 2);
	}

	private void notifyOnStateChanged() {
		if (onStateChangedListener != null) {
			onStateChangedListener.action();
		}
	}

	private void markOnDraw() {
		switch (checkStyle) {
		case MARK:
			styleMarkOnDraw();
			break;
		case RECT:
			styleRectOnDraw();
			break;
		case DOT:
			styleDotOnDraw();
			break;
		}

	}

	private void styleMarkOnDraw() {
		ctx.pushStyle();

		getTheme().getPrimaryColor().applyStroke();
		ctx.strokeWeight(max(1, getWidth() / 5));
		ctx.strokeCap(PROJECT);

		ctx.line(getX() + getWidth() * .3f, getY() + getHeight() * .6f, getX() + getWidth() / 2,
				getY() + getHeight() * .8f);

		ctx.line(getX() + getWidth() * .8f, getY() + getHeight() * .2f, getX() + getWidth() / 2,
				getY() + getHeight() * .8f);

		ctx.popStyle();
	}

	private void styleRectOnDraw() {
		ctx.pushStyle();
		ctx.noStroke();
		markColor.apply();
		ctx.rect(getX(), getY(), getWidth(), getHeight());
		ctx.popStyle();
	}

	private void styleDotOnDraw() {
		ctx.pushStyle();
		ctx.noFill();
		markColor.applyStroke();
		ctx.strokeWeight(Math.min(getWidth() / 2, getHeight() / 2));
		ctx.point(getX() + getWidth() / 2, getY() + getHeight() / 2);
		ctx.popStyle();
	}

	public enum CheckStyle {
		MARK, RECT, DOT;
	}
}