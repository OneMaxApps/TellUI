package microui.component;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
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
import processing.core.PFont;

/**
 * A component for displaying text with automatic resizing, alignment, and
 * clipping options. TextView provides flexible text rendering with support for
 * automatic font size adjustment to fit within bounds, horizontal and vertical
 * alignment, and optional text clipping.
 */
public final class TextView extends Component {
	private static final String DEFAULT_TEXT = "";

	private AbstractColor textColor;
	private PFont font;
	private String text;
	private AutoResizeMode autoResizeMode;
	private float textSize, autoTextSize;
	private int alignX, alignY;
	private boolean autoResizeModeEnabled, clipModeEnabled;

	/**
	 * Constructs a TextView with specified text, position, and dimensions. The text
	 * view is initialized with automatic resizing enabled and centered alignment.
	 *
	 * @param text   the text to display
	 * @param x      the x-coordinate of the text view's top-left corner
	 * @param y      the y-coordinate of the text view's top-left corner
	 * @param width  the width of the text view
	 * @param height the height of the text view
	 */
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

	/**
	 * Constructs a TextView with specified position and dimensions but no initial
	 * text.
	 *
	 * @param x      the x-coordinate of the text view's top-left corner
	 * @param y      the y-coordinate of the text view's top-left corner
	 * @param width  the width of the text view
	 * @param height the height of the text view
	 */
	public TextView(float x, float y, float width, float height) {
		this(DEFAULT_TEXT, x, y, width, height);
	}

	/**
	 * Constructs a TextView with specified text, centered on the screen.
	 *
	 * @param text the text to display
	 */
	public TextView(String text) {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
		setText(text);
	}

	/**
	 * Constructs a TextView with empty text, centered on the screen.
	 */
	public TextView() {
		this(DEFAULT_TEXT);
	}

	/**
	 * Gets the horizontal alignment of the text.
	 *
	 * @return the horizontal alignment (LEFT, CENTER, or RIGHT)
	 */
	public int getAlignX() {
		return alignX;
	}

	/**
	 * Sets the horizontal alignment of the text.
	 *
	 * @param alignX the horizontal alignment (must be LEFT, CENTER, or RIGHT)
	 * @throws IllegalArgumentException if alignX is not LEFT, CENTER, or RIGHT
	 */
	public void setAlignX(int alignX) {
		if (alignX != LEFT && alignX != CENTER && alignX != RIGHT) {
			throw new IllegalArgumentException("alignX for text must be only LEFT, CENTER or RIGHT");
		}
		this.alignX = alignX;
	}

	/**
	 * Gets the vertical alignment of the text.
	 *
	 * @return the vertical alignment (TOP, CENTER, or BOTTOM)
	 */
	public int getAlignY() {
		return alignY;
	}

	/**
	 * Sets the vertical alignment of the text.
	 *
	 * @param alignY the vertical alignment (must be TOP, CENTER, or BOTTOM)
	 * @throws IllegalArgumentException if alignY is not TOP, CENTER, or BOTTOM
	 */
	public void setAlignY(int alignY) {
		if (alignY != TOP && alignY != CENTER && alignY != BOTTOM) {
			throw new IllegalArgumentException("alignY for text must be only TOP, CENTER or BOTTOM");
		}

		this.alignY = alignY;
	}

	/**
	 * Checks if text clipping to bounds is enabled.
	 *
	 * @return true if text is clipped to component bounds, false if text can
	 *         overflow
	 */
	public boolean isClipModeEnabled() {
		return clipModeEnabled;
	}

	/**
	 * Enables or disables text clipping to bounds.
	 *
	 * @param clipModeEnabled true to clip text to component bounds, false to allow
	 *                        overflow
	 */
	public void setClipModeEnabled(boolean clipModeEnabled) {
		this.clipModeEnabled = clipModeEnabled;
	}

	/**
	 * Gets the manual text size (used when auto-resize is disabled).
	 *
	 * @return the current manual text size in pixels
	 */
	public float getTextSize() {
		return textSize;
	}

	/**
	 * Sets the manual text size (used when auto-resize is disabled).
	 *
	 * @param textSize the text size in pixels (must be greater than 0)
	 * @throws IllegalArgumentException if textSize is less than or equal to 0
	 */
	public void setTextSize(float textSize) {
		if (textSize <= 0) {
			throw new IllegalArgumentException("text size cannot be equal to zero and lower");
		}
		this.textSize = textSize;
	}

	/**
	 * Gets the current font.
	 *
	 * @return the current font, or null if using default font
	 */
	public PFont getFont() {
		return font;
	}

	/**
	 * Sets the font for text rendering.
	 *
	 * @param font the font to use for text
	 * @throws IllegalArgumentException if font is null
	 */
	public void setFont(PFont font) {
		if (font == null) {
			throw new IllegalArgumentException("Font cannot be null");
		}
		this.font = font;
	}

	/**
	 * Gets the current text content.
	 *
	 * @return the current text string
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text content to display.
	 *
	 * @param text the text to display
	 * @throws NullPointerException if text is null
	 */
	public void setText(String text) {
		this.text = requireNonNull(text, "text");
	}

	/**
	 * Checks if automatic text resizing is enabled.
	 *
	 * @return true if auto-resize is enabled, false if using manual text size
	 */
	public boolean isAutoResizeModeEnabled() {
		return autoResizeModeEnabled;
	}

	/**
	 * Enables or disables automatic text resizing. When enabled, text size is
	 * automatically calculated to fit within component bounds.
	 *
	 * @param autoResizeModeEnabled true to enable auto-resize, false to use manual
	 *                              text size
	 */
	public void setAutoResizeModeEnabled(boolean autoResizeModeEnabled) {
		if (autoResizeModeEnabled && !this.autoResizeModeEnabled) {
			recalculateAutoTextSize();
		}

		this.autoResizeModeEnabled = autoResizeModeEnabled;
	}

	/**
	 * Gets the auto-resize mode (determines how aggressively text is resized).
	 *
	 * @return the current auto-resize mode
	 */
	public AutoResizeMode getAutoResizeMode() {
		return autoResizeMode;
	}

	/**
	 * Sets the auto-resize mode (determines how aggressively text is resized).
	 *
	 * @param autoResizeMode the auto-resize mode to use
	 */
	public void setAutoResizeMode(AutoResizeMode autoResizeMode) {
		this.autoResizeMode = autoResizeMode;
	}

	/**
	 * Gets the text color.
	 *
	 * @return the current text color
	 */
	public AbstractColor getTextColor() {
		return textColor;
	}

	/**
	 * Sets the text color.
	 *
	 * @param textColor the color for text rendering
	 * @throws NullPointerException if textColor is null
	 */
	public void setTextColor(AbstractColor textColor) {
		this.textColor = requireNonNull(textColor, "textColor");
	}

	/**
	 * Renders the text view. Draws background, then text with configured alignment,
	 * sizing, and clipping.
	 */
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

	/**
	 * Called when the text view's bounds change. Recalculates auto-text size if
	 * auto-resize is enabled.
	 */
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