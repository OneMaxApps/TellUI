package microui.component;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.BOTTOM;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.TOP;

import microui.constants.AutoResizeMode;
import microui.core.base.Component;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.util.CheckOnNPE;
import processing.core.PFont;

public final class TextView extends Component {
	private static final String DEFAULT_TEXT = "";
	private AbstractColor textColor;
	private PFont font;
	private String text;
	private AutoResizeMode autoResizeMode;
	private float textSize, autoTextSize;
	private int alignX, alignY;
	private boolean isAutoResizeModeEnabled, isClipModeEnabled;

	public TextView(String text, float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinSize(10);
		setMaxSize(100, 40);

		setBackgroundColor(Color.TRANSPARENT);
		setTextColor(textColor = getTheme().getTextViewColor());

		setText(text);
		setTextSize(max(1, min(width, height)));
		setAutoResizeModeEnabled(true);
		setAutoResizeMode(AutoResizeMode.BIG);
		setClipModeEnabled(true);
		setAlignX(CENTER);
		setAlignY(CENTER);
	}

	public TextView(float x, float y, float width, float height) {
		this(DEFAULT_TEXT, x, y, width, height);
	}

	public TextView(String text) {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
		setText(text);
	}

	public TextView() {
		this(DEFAULT_TEXT);
	}

	public int getAlignX() {
		return alignX;
	}

	public void setAlignX(int alignX) {
		if (alignX != LEFT && alignX != CENTER && alignX != RIGHT) {
			throw new IllegalArgumentException("alignX for text must be only LEFT, CENTER or RIGHT");
		}
		this.alignX = alignX;
	}

	public int getAlignY() {
		return alignY;
	}

	public void setAlignY(int alignY) {
		if (alignY != TOP && alignY != CENTER && alignY != BOTTOM) {
			throw new IllegalArgumentException("alignY for text must be only TOP, CENTER or BOTTOM");
		}

		this.alignY = alignY;
	}

	public boolean isClipModeEnabled() {
		return isClipModeEnabled;
	}

	public void setClipModeEnabled(boolean isClipModeEnabled) {
		this.isClipModeEnabled = isClipModeEnabled;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		if (textSize <= 0) {
			throw new IllegalArgumentException("text size cannot be equal to zero and lower");
		}
		this.textSize = textSize;
	}

	public PFont getFont() {
		return font;
	}

	public void setFont(PFont font) {
		if (font == null) {
			throw new IllegalArgumentException("Font cannot be null");
		}
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		CheckOnNPE.checkParam(text, "text");
		this.text = text;
	}

	public boolean isAutoResizeModeEnabled() {
		return isAutoResizeModeEnabled;
	}

	public void setAutoResizeModeEnabled(boolean isAutoResizeModeEnabled) {
		if (isAutoResizeModeEnabled && !this.isAutoResizeModeEnabled) {
			recalculateAutoTextSize();
		}

		this.isAutoResizeModeEnabled = isAutoResizeModeEnabled;

	}

	public AutoResizeMode getAutoResizeMode() {
		return autoResizeMode;
	}

	public void setAutoResizeMode(AutoResizeMode autoResizeMode) {
		this.autoResizeMode = autoResizeMode;
	}

	public AbstractColor getTextColor() {
		return textColor;
	}

	public void setTextColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("Color for text cannot be null");
		}
		textColor = color;
	}

	@Override
	protected void render() {
		ctx.noStroke();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());

		if (text == DEFAULT_TEXT) {
			return;
		}

		if (font != null) {
			ctx.textFont(font);
		}

		if (isAutoResizeModeEnabled()) {
			ctx.textSize(autoTextSize);
		} else {
			ctx.textSize(getTextSize());
		}
		textColor.apply();
		ctx.textAlign(alignX, alignY);

		if (isClipModeEnabled()) {
			ctx.text(text, getX(), getY(), getWidth(), getHeight());
		} else {
			ctx.text(text, getX(), getY());
		}

	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		recalculateAutoTextSize();
	}

	private void recalculateAutoTextSize() {
		if (getAutoResizeMode() == null) {
			return;
		}

		autoTextSize = max(1, min(getWidth(), getHeight()) / getAutoResizeMode().getValue());
	}
}