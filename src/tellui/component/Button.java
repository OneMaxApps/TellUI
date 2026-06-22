package tellui.component;

import static tellui.constants.AutoResizeMode.BIG;
import static tellui.core.style.theme.ThemeManager.getTheme;

import processing.core.PFont;
import processing.core.PImage;
import tellui.core.AbstractButton;
import tellui.core.ImageBuffer;
import tellui.core.effect.Hover;
import tellui.core.effect.Ripples;
import tellui.core.style.AbstractColor;

/**
 * Represents a clickable button component with text and/or image content. The
 * Button class extends AbstractButton to provide interactive button
 * functionality with support for text rendering, image display, hover effects,
 * and ripple animations. This component automatically handles text resizing and
 * positioning within the button bounds.
 * 
 * @see AbstractButton
 * @see TextView
 * @see ImageBuffer
 */
public class Button extends AbstractButton {
	private static final String DEFAULT_TITLE = "BUTTON";
	private final Ripples ripples;
	private final Hover hover;
	private final TextView textView;
	private final ImageBuffer image;

	/**
	 * Constructs a Button with specified text, position, and dimensions. The button
	 * is initialized with default styling from the current theme and includes
	 * automatic text resizing capabilities.
	 *
	 * @param text the text to display on the button
	 * @param x    the x-coordinate of the button's top-left corner
	 * @param y    the y-coordinate of the button's top-left corner
	 * @param w    the width of the button
	 * @param h    the height of the button
	 */
	public Button(String text, float x, float y, float w, float h) {
		super(x, y, w, h);
		setMinMaxSize(20, 10, 100, 40);

		ripples = new Ripples(this);
		hover = new Hover(this);
		
		ripples.setId(IGNORE_INTERNAL_COMPONENT_ID);
		hover.setId(IGNORE_INTERNAL_COMPONENT_ID);
		
		textView = new TextView(getX(), getY(), getWidth(), getHeight());
		textView.setConstrainDimensionsEnabled(false);
		textView.setAutoResizeModeEnabled(true);
		textView.setAutoResizeMode(BIG);
		textView.setTextColor(getTheme().getButtonTextColor());
		textView.setId(IGNORE_INTERNAL_COMPONENT_ID);
		setText(text);

		image = new ImageBuffer();
		image.setVisible(true);
		image.setId(IGNORE_INTERNAL_COMPONENT_ID);
	}

	/**
	 * Constructs a Button with specified position and dimensions but no initial
	 * text.
	 *
	 * @param x the x-coordinate of the button's top-left corner
	 * @param y the y-coordinate of the button's top-left corner
	 * @param w the width of the button
	 * @param h the height of the button
	 */
	public Button(float x, float y, float w, float h) {
		this("", x, y, w, h);
	}

	/**
	 * Constructs a Button with specified text, centered on the screen.
	 *
	 * @param text the text to display on the button
	 */
	public Button(String text) {
		this(text, 0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	/**
	 * Constructs a default Button with the text "BUTTON", centered on the screen.
	 * This is a convenience constructor for quick button creation.
	 */
	public Button() {
		this(DEFAULT_TITLE);
	}
	
	
	
	/**
	 * Gets the color of the ripple effects.
	 *
	 * @return the current ripple effect color
	 */
	public final AbstractColor getRipplesColor() {
		return ripples.getColor();
	}

	/**
	 * Sets the color of the ripple effects.
	 *
	 * @param color the color to use for ripple effects
	 * @return this Button instance for method chaining
	 */
	public final Button setRipplesColor(AbstractColor color) {
		ripples.setColor(color);
		return this;
	}

	/**
	 * Checks if ripple effects are enabled.
	 *
	 * @return true if ripple effects are enabled, false otherwise
	 */
	public final boolean isRipplesEnabled() {
		return ripples.isEnabled();
	}

	/**
	 * Enables or disables ripple effects.
	 *
	 * @param enabled true to enable ripple effects, false to disable
	 * @return this Button instance for method chaining
	 */
	public final Button setRipplesEnabled(boolean enabled) {
		ripples.setEnabled(enabled);
		return this;
	}

	/**
	 * Checks if hover effects are enabled.
	 *
	 * @return true if hover effects are enabled, false otherwise
	 */
	public final boolean isHoverEnabled() {
		return hover.isEnabled();
	}

	/**
	 * Enables or disables hover effects.
	 *
	 * @param enabled true to enable hover effects, false to disable
	 * @return this Button instance for method chaining
	 */
	public final Button setHoverEnabled(boolean enabled) {
		hover.setEnabled(enabled);
		return this;
	}
	
	/**
	 * Gets the color of the hover effect.
	 *
	 * @return the current hover effect color
	 */
	public final AbstractColor getHoverColor() {
		return hover.getColor();
	}

	/**
	 * Sets the color of the hover effect.
	 *
	 * @param color the color to use for hover effects
	 * @return this Button instance for method chaining
	 */
	public final Button setHoverColor(AbstractColor color) {
		hover.setColor(color);
		return this;
	}

	/**
	 * Gets the animation speed of the hover effect.
	 *
	 * @return the current hover animation speed
	 */
	public final float getHoverSpeed() {
		return hover.getSpeed();
	}

	/**
	 * Sets the animation speed of the hover effect.
	 *
	 * @param speed the animation speed (must be greater than 0)
	 * @return this Button instance for method chaining
	 */
	public final Button setHoverSpeed(float speed) {
		hover.setSpeed(speed);
		return this;
	}

	/**
	 * Gets the text currently displayed on the button.
	 *
	 * @return the button's text content
	 */
	public final String getText() {
		return textView.getText();
	}

	/**
	 * Sets the text to display on the button. The text will be automatically
	 * resized and positioned according to the button's current dimensions and
	 * auto-resize settings.
	 *
	 * @param text the text to display
	 * @return this Button instance for method chaining
	 */
	public final Button setText(String text) {
		textView.setText(text);
		return this;
	}

	/**
	 * Gets the font used for rendering the button's text.
	 *
	 * @return the current font, or null if using the default font
	 */
	public final PFont getFont() {
		return textView.getFont();
	}

	/**
	 * Sets the font for rendering the button's text.
	 *
	 * @param font the font to use for text rendering
	 * @return this Button instance for method chaining
	 */
	public final Button setFont(PFont font) {
		textView.setFont(font);
		return this;
	}

	/**
	 * Gets the color of the button's text.
	 *
	 * @return the current text color
	 */
	public final AbstractColor getTextColor() {
		return textView.getTextColor();
	}

	/**
	 * Sets the color of the button's text.
	 *
	 * @param color the color to use for text rendering
	 * @return this Button instance for method chaining
	 */
	public final Button setTextColor(AbstractColor color) {
		textView.setTextColor(color);
		return this;
	}

	/**
	 * Checks whether the button's text is currently visible.
	 *
	 * @return true if text is visible, false otherwise
	 */
	public final boolean isTextVisible() {
		return textView.isVisible();
	}

	/**
	 * Sets the visibility of the button's text. This can be used to hide text while
	 * keeping an image visible, or to create icon-only buttons.
	 *
	 * @param isVisible true to show text, false to hide it
	 * @return this Button instance for method chaining
	 */
	public final Button setTextVisible(boolean isVisible) {
		textView.setVisible(isVisible);
		return this;
	}

	/**
	 * Gets the image displayed on the button.
	 *
	 * @return the current image, or null if no image is set
	 */
	public final PImage getImage() {
		return image.get();
	}

	/**
	 * Sets an image to display on the button.
	 *
	 * @param image the image to display on the button
	 * @return this Button instance for method chaining
	 */
	public final Button setImage(PImage image) {
		this.image.set(image);
		return this;
	}

	/**
	 * Gets the tint color applied to the button's image.
	 *
	 * @return the current image tint color
	 */
	public final AbstractColor getImageColor() {
		return image.getColor();
	}

	/**
	 * Sets the tint color to apply to the button's image.
	 *
	 * @param color the tint color to apply to the image
	 * @return this Button instance for method chaining
	 */
	public final Button setImageColor(AbstractColor color) {
		image.setColor(color);
		return this;
	}

	/**
	 * Sets the horizontal alignment of the button's text. This determines how text
	 * is positioned within the button's bounds.
	 *
	 * @param alignX the horizontal alignment (LEFT, CENTER, RIGHT)
	 * @return this Button instance for method chaining
	 */
	public final Button setTextAlignX(int alignX) {
		textView.setAlignX(alignX);
		return this;
	}

	/**
	 * Sets the vertical alignment of the button's text. This determines how text is
	 * positioned within the button's bounds.
	 *
	 * @param alignY the vertical alignment (TOP, CENTER, BOTTOM)
	 * @return this Button instance for method chaining
	 */
	public final Button setTextAlignY(int alignY) {
		textView.setAlignY(alignY);
		return this;
	}
	
	protected final Ripples getRipplesInternal() {
		return ripples;
	}

	protected final Hover getHoverInternal() {
		return hover;
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
		ctx.pushStyle();
		getStrokeColor().applyStroke();
		getBackgroundColor().apply();
		ctx.rect(getPadX(), getPadY(), getPadWidth(), getPadHeight());
		ctx.popStyle();
		
		image.draw();
		getRipplesInternal().draw();
		getHoverInternal().draw();
		textView.draw();
	}
}