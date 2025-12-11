package microui.component;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.component.CheckBox.CheckStyle.MARK;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.PROJECT;

import microui.core.AbstractButton;
import microui.core.style.AbstractColor;
import microui.event.Listener;

/**
 * A CheckBox component that represents a binary state (checked or unchecked).
 * The CheckBox extends AbstractButton to provide toggle functionality with
 * three visual styles for the checked state: MARK, RECT, and DOT.
 * 
 * <p><strong>Status:</strong> STABLE - Do not modify</p>
 * <p><strong>Last Reviewed:</strong> 17.11.2025</p>
 * 
 * @see AbstractButton
 * @see CheckStyle
 */
public class CheckBox extends AbstractButton {
    /** Default size of the CheckBox in pixels. */
    public static final int DEFAULT_SIZE = 16;
    
    private AbstractColor markColor;
    private Listener onStateChangedListener;
    private CheckStyle checkStyle;
    private boolean checked;

    /**
     * Constructs a CheckBox at the specified position with default size.
     * The checkbox is initialized with MARK style and the theme's primary color.
     *
     * @param x the x-coordinate of the checkbox's top-left corner
     * @param y the y-coordinate of the checkbox's top-left corner
     */
    public CheckBox(float x, float y) {
        super(x, y, DEFAULT_SIZE, DEFAULT_SIZE);
        setMinMaxSize(DEFAULT_SIZE);
        onClick(() -> toggle());
        markColor = getTheme().getPrimaryColor();
        setCheckStyle(MARK);
    }

    /**
     * Constructs a CheckBox with the specified checked state, centered on the screen.
     *
     * @param checked the initial checked state (true for checked, false for unchecked)
     */
    public CheckBox(boolean checked) {
        this(0, 0);
        setPositionInCenter();
        setChecked(checked);
    }

    /**
     * Constructs a default CheckBox in the unchecked state, centered on the screen.
     */
    public CheckBox() {
        this(false);
    }

    // == PUBLIC API ==

    /**
     * Gets the visual style used to display the checked state.
     *
     * @return the current check style
     */
    public final CheckStyle getCheckStyle() {
        return checkStyle;
    }

    /**
     * Sets the visual style for displaying the checked state.
     *
     * @param checkStyle the check style to use (MARK, RECT, or DOT)
     * @throws NullPointerException if checkStyle is null
     */
    public final void setCheckStyle(CheckStyle checkStyle) {
        this.checkStyle = requireNonNull(checkStyle, "checkStyle");
    }

    /**
     * Gets the current checked state of the checkbox.
     *
     * @return true if the checkbox is checked, false otherwise
     */
    public final boolean isChecked() {
        return checked;
    }

    /**
     * Sets the checked state of the checkbox.
     * If the state changes, the onStateChangedListener will be notified.
     *
     * @param checked true to check the checkbox, false to uncheck it
     */
    public final void setChecked(boolean checked) {
        if (this.checked == checked) {
            return;
        }
        this.checked = checked;
        notifyOnStateChanged();
    }

    /**
     * Toggles the checked state of the checkbox.
     * This method is automatically called when the checkbox is clicked.
     */
    public final void toggle() {
        setChecked(!isChecked());
    }

    /**
     * Gets the color used to draw the check mark or indicator.
     *
     * @return the current mark color
     */
    public final AbstractColor getMarkColor() {
        return markColor;
    }

    /**
     * Sets the color used to draw the check mark or indicator.
     *
     * @param markColor the color to use for the check mark
     * @throws NullPointerException if markColor is null
     */
    public final void setMarkColor(AbstractColor markColor) {
        this.markColor = requireNonNull(markColor, "markColor");
    }

    /**
     * Sets the listener to be called when the checkbox's state changes.
     *
     * @param onStateChangedListener the listener to call on state changes
     * @throws NullPointerException if the listener is null
     */
    public final void setOnStateChangedListener(Listener onStateChangedListener) {
        this.onStateChangedListener = requireNonNull(onStateChangedListener, "onStateChangedListener");
    }

    /**
     * Renders the checkbox and its visual state.
     * The rendering includes background, ripple effects, hover effects,
     * and the check mark if the checkbox is checked.
     */
    @Override
    protected void render() {
        super.render();

        getRipplesInternal().draw();
        getHoverInternal().draw();

        if (checked) {
            markOnDraw();
        }
    }

    /**
     * Positions the checkbox at the center of the current drawing context.
     */
    private void setPositionInCenter() {
        setPosition(ctx.width / 2 - DEFAULT_SIZE / 2, ctx.height / 2 - DEFAULT_SIZE / 2);
    }

    /**
     * Notifies the state change listener if one is registered.
     */
    private void notifyOnStateChanged() {
        if (onStateChangedListener != null) {
            onStateChangedListener.action();
        }
    }

    /**
     * Draws the check mark based on the current check style.
     */
    private void markOnDraw() {
        switch (checkStyle) {
            case MARK:
                styleMarkOnDraw();
                break;
            case RECT:
                styleRectOnDraw();
                break;
            case DOT:
                styleDotOnDraw();
                break;
        }
    }

    /**
     * Draws a check mark (✓) style indicator.
     */
    private void styleMarkOnDraw() {
        ctx.pushStyle();

        getTheme().getPrimaryColor().applyStroke();
        ctx.strokeWeight(max(1, getWidth() / 5));
        ctx.strokeCap(PROJECT);

        ctx.line(getX() + getWidth() * .3f, getY() + getHeight() * .6f, 
                 getX() + getWidth() / 2, getY() + getHeight() * .8f);

        ctx.line(getX() + getWidth() * .8f, getY() + getHeight() * .2f, 
                 getX() + getWidth() / 2, getY() + getHeight() * .8f);

        ctx.popStyle();
    }

    /**
     * Draws a filled rectangle style indicator.
     */
    private void styleRectOnDraw() {
        ctx.pushStyle();
        ctx.noStroke();
        markColor.apply();
        ctx.rect(getX(), getY(), getWidth(), getHeight());
        ctx.popStyle();
    }

    /**
     * Draws a dot style indicator.
     */
    private void styleDotOnDraw() {
        ctx.pushStyle();
        ctx.noFill();
        markColor.applyStroke();
        ctx.strokeWeight(Math.min(getWidth() / 2, getHeight() / 2));
        ctx.point(getX() + getWidth() / 2, getY() + getHeight() / 2);
        ctx.popStyle();
    }

    /**
     * Enumeration defining the visual styles for displaying a checked checkbox.
     */
    public enum CheckStyle {
        /** Displays a check mark (✓). */
        MARK,
        
        /** Displays a filled rectangle. */
        RECT,
        
        /** Displays a dot. */
        DOT;
    }
}