package microui.component;

import static microui.constants.AutoResizeMode.BIG;
import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.AbstractButton;
import microui.core.ImageBuffer;
import microui.core.style.AbstractColor;
import processing.core.PFont;
import processing.core.PImage;

/**
 * Represents a clickable button component with text and/or image content.
 * The Button class extends AbstractButton to provide interactive button functionality
 * with support for text rendering, image display, hover effects, and ripple animations.
 * This component automatically handles text resizing and positioning within the button bounds.
 * 
 * @see AbstractButton
 * @see TextView
 * @see ImageBuffer
 */
public class Button extends AbstractButton {
    private final TextView textView;
    private final ImageBuffer image;

    /**
     * Constructs a Button with specified text, position, and dimensions.
     * The button is initialized with default styling from the current theme
     * and includes automatic text resizing capabilities.
     *
     * @param text the text to display on the button
     * @param x the x-coordinate of the button's top-left corner
     * @param y the y-coordinate of the button's top-left corner
     * @param w the width of the button
     * @param h the height of the button
     */
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

    /**
     * Constructs a Button with specified position and dimensions but no initial text.
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
     * The button uses maximum dimensions and is positioned at the center
     * of the current drawing context.
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
        this("BUTTON");
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
     * Sets the text to display on the button.
     * The text will be automatically resized and positioned according to
     * the button's current dimensions and auto-resize settings.
     *
     * @param text the text to display
     * @return this Button instance for method chaining
     */
    public final Button setText(String text) {
        this.textView.setText(text);
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
     * This overrides the theme's default button text color.
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
     * Sets the visibility of the button's text.
     * This can be used to hide text while keeping an image visible,
     * or to create icon-only buttons.
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
     * The image will be drawn behind the text (if any) and above
     * the button's background and effects.
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
     * Use null or a fully opaque white color to display the image without tinting.
     *
     * @param color the tint color to apply to the image
     * @return this Button instance for method chaining
     */
    public final Button setImageColor(AbstractColor color) {
        image.setColor(color);
        return this;
    }

    /**
     * Sets the horizontal alignment of the button's text.
     * This determines how text is positioned within the button's bounds.
     *
     * @param alignX the horizontal alignment (e.g., LEFT, CENTER, RIGHT)
     * @return this Button instance for method chaining
     */
    public final Button setTextAlignX(int alignX) {
        textView.setAlignX(alignX);
        return this;
    }

    /**
     * Sets the vertical alignment of the button's text.
     * This determines how text is positioned within the button's bounds.
     *
     * @param alignY the vertical alignment (e.g., TOP, CENTER, BOTTOM)
     * @return this Button instance for method chaining
     */
    public final Button setTextAlignY(int alignY) {
        textView.setAlignY(alignY);
        return this;
    }

    /**
     * Called when the button's bounds (position or dimensions) change.
     * Updates the bounds of the internal TextView and ImageBuffer components
     * to match the button's new bounds.
     */
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

    /**
     * Renders the button and all its components.
     * The rendering order is: background, image, ripple effects,
     * hover effects, and finally the text.
     */
    @Override
    protected void render() {
        super.render();
        image.draw();
        getRipplesInternal().draw();
        getHoverInternal().draw();
        textView.draw();
    }
}