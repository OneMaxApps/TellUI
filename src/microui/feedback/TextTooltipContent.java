package microui.feedback;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import microui.core.style.AbstractColor;

/**
 * Tooltip content implementation that displays text with customizable styling.
 * Extends TooltipContent to provide a text-based tooltip with automatic sizing
 * based on text content and multiline support.
 * <p>
 * This class manages text tooltips with theme integration, automatic size calculation,
 * and configurable text properties. It supports multiline text (using newline characters)
 * and automatically adjusts its dimensions to fit the text content.
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see TooltipContent
 * @see AbstractColor
 */
public final class TextTooltipContent extends TooltipContent {
	/** Default text size in points. */
	private static final byte DEFAULT_TEXT_SIZE = 12;
	/** Default padding around text in pixels. */
	private static final byte DEFAULT_PADDING = 5;
	/** Default left margin from target element in pixels. */
	private static final byte DEFAULT_MARGIN_LEFT = 10;
	
	/** Minimum allowed text size in points. */
	private static final byte MIN_TEXT_SIZE = 4;
	
	/** Background color of the tooltip. */
	private AbstractColor backgroundColor, textColor;
	/** The text content to display. */
	private String text;
	/** Text size in points. */
	private int textSize;
	
	/**
	 * Constructs a TextTooltipContent with the specified text.
	 * Initializes with default styling from the current theme.
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
		this.backgroundColor = requireNonNull(backgroundColor,"backgroundColor");
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
		this.textColor = requireNonNull(textColor,"textColor");
	}

	/**
	 * Checks if the tooltip is prepared to show.
	 * The tooltip can show if it has non-blank text content.
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
	 * Checks if the tooltip is prepared to close.
	 * Text tooltips can always close.
	 * 
	 * @return always true for text tooltips
	 */
	@Override
	public boolean isPreparedClose() {
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
	 * Sets the text content and automatically adjusts tooltip size.
	 * Supports multiline text using newline characters ('\n').
	 * 
	 * @param text the text to display (cannot be null)
	 * @throws NullPointerException if text is null
	 */
	public void setText(String text) {
		requireNonNull(text,"text");
		
		// Split text into lines for size calculation
		final String[] lines = text.split("\n");
		
		// Calculate and set optimal size based on text content
		prepareSize(lines);
		
		this.text = text;
	}

	/**
	 * Renders the text tooltip with background and text.
	 * Applies styling and draws both background rectangle and text content.
	 */
	@Override
	protected void render() {
		// Draw background
		backgroundColor.apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		
		// Draw text
		textColor.apply();
		ctx.textSize(textSize);
		ctx.textAlign(LEFT, TOP);
		ctx.textLeading(textSize);
		ctx.text(text, getX(), getY());
	}
	
	/**
	 * Calculates the pixel width of text with current text size settings.
	 * 
	 * @param text the text to measure
	 * @return the width of the text in pixels
	 * @throws NullPointerException if text is null
	 */
	private float getTextWidth(String text) {
		requireNonNull(text,"text");
		
		final float width;
		
		// Temporarily apply text settings for measurement
		ctx.pushStyle();
		ctx.textSize(textSize);
		ctx.textAlign(LEFT, TOP);
		width = ctx.textWidth(text);
		ctx.popStyle();
		
		return width;
	}
	
	/**
	 * Prepares the tooltip size based on text content.
	 * Calculates width as the widest line and height based on line count.
	 * 
	 * @param text array of text lines to measure
	 */
	private void prepareSize(String... text) {
		float newWidth = 0;
		final float newHeight;
		
		// Find the widest line
		for (int i = 0; i < text.length; i++) {
			newWidth = Math.max(newWidth, getTextWidth(text[i]));
		}
		
		// Calculate height based on line count
		newHeight = textSize * text.length;
		
		// Update tooltip size
		setSize(newWidth,newHeight);
	}
}