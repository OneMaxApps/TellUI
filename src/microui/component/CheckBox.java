package microui.component;

import static java.lang.Math.max;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.PROJECT;

import microui.core.AbstractButton;
import microui.core.style.AbstractColor;
import microui.event.Listener;

public class CheckBox extends AbstractButton {
	public static final int DEFAULT_SIZE = 16;
	private AbstractColor markColor;
	private boolean isChecked;

	public CheckBox(float x, float y) {
		super(x, y, DEFAULT_SIZE, DEFAULT_SIZE);
		setMinMaxSize(DEFAULT_SIZE);
		onClick(() -> toggle());
		markColor = getTheme().getPrimaryColor();
	}

	public CheckBox(boolean isChecked) {
		this(ctx.width / 2 - DEFAULT_SIZE / 2, ctx.height / 2 - DEFAULT_SIZE / 2);
		setChecked(isChecked);
	}

	public CheckBox() {
		this(false);
	}

	@Override
	protected void render() {
		super.render();
		
		getRipplesInternal().draw();
		getHoverInternal().draw();
		
		if (isChecked) {
			markOnDraw();
		}
	}

	public final boolean isChecked() {
		return isChecked;
	}

	public final void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public final void toggle() {
		isChecked = !isChecked;
	}

	public final AbstractColor getMarkColor() {
		return markColor;
	}

	public final void setMarkColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("Color for mark cannot be null");
		}

		markColor = color;
	}

	public final void onStateChangedListener(Listener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener cannot be null");
		}
		onClick(listener);
	}

	private void markOnDraw() {
		ctx.pushStyle();
		ctx.noStroke();
		markColor.apply();
		ctx.rect(getX(), getY(), getWidth(), getHeight());
		ctx.stroke(255);
		ctx.strokeWeight(max(1, getWidth() / 5));
		ctx.strokeCap(PROJECT);
		ctx.line(getX() + getWidth() * .3f, getY() + getHeight() * .6f, getX() + getWidth() / 2,
				getY() + getHeight() * .8f);
		ctx.line(getX() + getWidth() * .8f, getY() + getHeight() * .2f, getX() + getWidth() / 2,
				getY() + getHeight() * .8f);
		ctx.popStyle();
	}

}