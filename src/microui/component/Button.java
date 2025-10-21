package microui.component;

import static java.util.Objects.requireNonNull;
import static microui.constants.AutoResizeMode.BIG;
import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.AbstractButton;
import microui.core.ImageBuffer;
import microui.core.style.AbstractColor;
import processing.core.PFont;
import processing.core.PImage;

//Status: STABLE - Do not modify
//Last Reviewed: 21.10.2025
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

	public final Button setText(String text) {
		this.textView.setText(text);

		return this;
	}

	public final PFont getFont() {
		return textView.getFont();
	}

	public final Button setFont(PFont font) {
		textView.setFont(requireNonNull(font, "font cannot be null"));

		return this;
	}

	public final AbstractColor getTextColor() {
		return textView.getTextColor();
	}

	public final Button setTextColor(AbstractColor color) {
		textView.setTextColor(color);

		return this;
	}

	public final boolean isTextVisible() {
		return textView.isVisible();
	}

	public final Button setTextVisible(boolean isVisible) {
		textView.setVisible(isVisible);

		return this;
	}

	public final PImage getImage() {
		return image.get();
	}

	public final Button setImage(PImage image) {
		this.image.set(image);

		return this;
	}

	public final AbstractColor getImageColor() {
		return image.getColor();
	}

	public final Button setImageColor(AbstractColor color) {
		image.setColor(color);

		return this;
	}

	public final Button setTextAlignX(int alignX) {
		textView.setAlignX(alignX);

		return this;
	}

	public final Button setTextAlignY(int alignY) {
		textView.setAlignY(alignY);

		return this;
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		if (textView != null) {
			textView.setBoundsFrom(this);
		}

		if (image != null) {
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