package microui.component;

import static java.util.Objects.requireNonNull;
import static microui.constants.AutoResizeMode.BIG;
import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.AbstractButton;
import microui.core.ImageBuffer;
import microui.core.style.AbstractColor;
import processing.core.PFont;
import processing.core.PImage;

public class Button extends AbstractButton {
	private final TextView textView;
	private final ImageBuffer image;
	
	public Button(String text, float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinMaxSize(20, 10, 100, 40);

		textView = new TextView(getX(), getY(), getWidth(), getHeight());
		textView.setConstrainDimensionsEnabled(false);
		textView.setAutoResizeModeEnabled(true);
		textView.setAutoResizeMode(BIG);
		textView.setTextColor(getTheme().getButtonTextColor());
		setText(text);
		
		image = new ImageBuffer();
		image.setVisible(true);
	}

	public Button(float x, float y, float w, float h) {
		this("", x, y, w, h);
	}

	public Button(String text) {
		this(requireNonNull(text, "text cannot be null"), 0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	public Button() {
		this("BUTTON");
	}

	public final String getText() {
		return textView.getText();
	}

	public final void setText(String text) {
		this.textView.setText(text);
	}

	public final PFont getFont() {
		return textView.getFont();
	}

	public final void setFont(PFont font) {
		textView.setFont(requireNonNull(font, "font cannot be null"));
	}

	public final AbstractColor getTextColor() {
		return textView.getTextColor();
	}

	public final void setTextColor(AbstractColor color) {
		textView.setTextColor(color);
	}

	public final boolean isTextVisible() {
		return textView.isVisible();
	}

	public final void setTextVisible(boolean isVisible) {
		textView.setVisible(isVisible);
	}
	
	public final PImage getImage() {
		return image.get();
	}
	
	public final void setImage(PImage image) {
		this.image.set(image);
	}
	
	public final AbstractColor getImageColor() {
		return image.getColor();
	}
	
	public final void setImageColor(AbstractColor color) {
		image.setColor(color);
	}
	
	public final void setTextAlignX(int alignX) {
		textView.setAlignX(alignX);
	}
	
	public final void setTextAlignY(int alignY) {
		textView.setAlignY(alignY);
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		if (textView != null) {
			textView.setBoundsFrom(this);
		}
		
		if(image != null) {
			image.setBoundsFrom(this);
		}
		
	}

	@Override
	protected void render() {
		super.render();
		image.draw();
		getRipplesInternal().draw();
		getHoverInternal().draw();
		textView.draw();
	}
	
}