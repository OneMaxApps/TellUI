package microui.feedback;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import microui.core.style.AbstractColor;
import microui.core.style.LerpedColor;

/**
 * Tooltip content implementation that displays text with customizable styling.
 * Extends TooltipContent to provide a text-based tooltip with automatic sizing
 * based on text content and multiline support.
 * <p>
 * This class manages text tooltips with theme integration, automatic size
 * calculation, and configurable text properties. It supports multiline text
 * (using newline characters) and automatically adjusts its dimensions to fit
 * the text content.
 * </p>
 * 
 * @see TooltipContent
 * @see AbstractColor
 */
public final class TextTooltipContent extends TooltipContent {
	private static final byte DEFAULT_TEXT_SIZE = 12;
	private static final byte DEFAULT_PADDING = 5;
	private static final byte DEFAULT_MARGIN_LEFT = 10;

	private static final byte MIN_TEXT_SIZE = 4;

	private AbstractColor backgroundColor, textColor;
	private String text;
	private int textSize;

	/**
	 * Constructs a TextTooltipContent with the specified text. Initializes with
	 * default styling from the current theme.
	 * 
	 * @param text the text to display in the tooltip (cannot be null)
	 * @throws NullPointerException if text is null
	 */
	public TextTooltipContent(String text) {
		super();
		setVisible(true);

		setPadding(DEFAULT_PADDING);
		setMarginLeft(DEFAULT_MARGIN_LEFT);

		setBackgroundColor(getTheme().getTooltipBackgroundColor());
		setTextColor(getTheme().getTooltipTextColor());
		setTextSize(DEFAULT_TEXT_SIZE);
		setText(text);
	}

	/**
	 * Returns the current text size.
	 * 
	 * @return the text size in points
	 */
	public int getTextSize() {
		return textSize;
	}

	/**
	 * Sets the text size.
	 * 
	 * @param textSize the text size in points (must be ≥ MIN_TEXT_SIZE)
	 * @throws IllegalArgumentException if textSize is less than MIN_TEXT_SIZE
	 */
	public void setTextSize(int textSize) {
		if (textSize < MIN_TEXT_SIZE) {
			throw new IllegalArgumentException("Text size for tooltip cannot be less than " + MIN_TEXT_SIZE);
		}
		this.textSize = textSize;
	}

	/**
	 * Returns the background color of the tooltip.
	 * 
	 * @return the background color
	 */
	public AbstractColor getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Sets the background color of the tooltip.
	 * 
	 * @param backgroundColor the background color to set (cannot be null)
	 * @throws NullPointerException if backgroundColor is null
	 */
	public void setBackgroundColor(AbstractColor backgroundColor) {
		this.backgroundColor = requireNonNull(backgroundColor, "backgroundColor");
	}

	/**
	 * Returns the text color of the tooltip.
	 * 
	 * @return the text color
	 */
	public AbstractColor getTextColor() {
		return textColor;
	}

	/**
	 * Sets the text color of the tooltip.
	 * 
	 * @param textColor the text color to set (cannot be null)
	 * @throws NullPointerException if textColor is null
	 */
	public void setTextColor(AbstractColor textColor) {
		this.textColor = requireNonNull(textColor, "textColor");
	}

	/**
	 * Checks if the tooltip is prepared to show. The tooltip can show if it has
	 * non-blank text content.
	 * 
	 * @return true if tooltip has text to display, false otherwise
	 */
	@Override
	public boolean isPreparedShow() {
		if (text == null || text.isBlank()) {
			return false;
		}

		return true;
	}

	/**
	 * Checks if the tooltip is prepared to close. Text tooltips can always close.
	 * 
	 * @return always true for text tooltips
	 */
	@Override
	public boolean isPreparedClose() {
		if (backgroundColor instanceof LerpedColor l) {
			l.resetAnimationProgress();
		}
		
		if (textColor instanceof LerpedColor l) {
			l.resetAnimationProgress();
		}
		
		return true;
	}

	/**
	 * Returns the current text content.
	 * 
	 * @return the text content
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text content and automatically adjusts tooltip size. Supports
	 * multiline text using newline characters ('\n').
	 * 
	 * @param text the text to display (cannot be null)
	 * @throws NullPointerException if text is null
	 */
	public void setText(String text) {
		requireNonNull(text, "text");

		final String[] lines = text.split("\n");

		prepareSize(lines);

		this.text = text;
	}

	@Override
	protected void render() {
		backgroundColor.apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());

		textColor.apply();
		ctx.textSize(textSize);
		ctx.textAlign(LEFT, TOP);
		ctx.textLeading(textSize);
		ctx.text(text, getX(), getY());
	}

	private float getTextWidth(String text) {
		requireNonNull(text, "text");

		final float width;

		ctx.pushStyle();
		ctx.textSize(textSize);
		ctx.textAlign(LEFT, TOP);
		width = ctx.textWidth(text);
		ctx.popStyle();

		return width;
	}

	private void prepareSize(String... text) {
		float newWidth = 0;
		final float newHeight;

		for (int i = 0; i < text.length; i++) {
			newWidth = Math.max(newWidth, getTextWidth(text[i]));
		}

		newHeight = textSize * text.length;

		setSize(newWidth, newHeight);
	}
}