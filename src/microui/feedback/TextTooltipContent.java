package microui.feedback;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

import microui.core.style.AbstractColor;

public final class TextTooltipContent extends TooltipContent {
	private static final byte DEFAULT_TEXT_SIZE = 12;
	private static final byte MIN_TEXT_SIZE = 4;
	private AbstractColor backgroundColor, textColor;
	private String text;
	private int textSize;
	
	public TextTooltipContent(String text) {
		super();
		setVisible(true);
		setBackgroundColor(getTheme().getTooltipBackgroundColor());
		setTextColor(getTheme().getTooltipTextColor());
		setTextSize(DEFAULT_TEXT_SIZE);
		setText(text);
	}
	
	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		if (textSize < MIN_TEXT_SIZE) {
			throw new IllegalArgumentException("Text size for tooltip cannot be less than " + MIN_TEXT_SIZE);
		}
		this.textSize = textSize;
	}

	public AbstractColor getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(AbstractColor backgroundColor) {
		this.backgroundColor = requireNonNull(backgroundColor,"backgroundColor");
	}

	public AbstractColor getTextColor() {
		return textColor;
	}

	public void setTextColor(AbstractColor textColor) {
		this.textColor = requireNonNull(textColor,"textColor");
	}

	@Override
	public boolean isPreparedShow() {
		if (text == null || text.isBlank()) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isPreparedClose() {
		return true;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		requireNonNull(text,"text");
		
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
		requireNonNull(text,"text");
		
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
		
		setSize(newWidth,newHeight);
	}
}