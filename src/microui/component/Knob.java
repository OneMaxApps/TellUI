package microui.component;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PApplet.dist;

import microui.core.RangeControl;
import microui.core.style.AbstractColor;
import microui.util.MathUtils;
import processing.event.MouseEvent;

/**
 * A circular knob control for selecting values within a range.
 * The Knob component allows users to adjust a numeric value by dragging
 * the knob or using mouse wheel scrolling. It provides visual feedback
 * through an arc indicator that shows the current value.
 * 
 * <p>The knob is circular and responds to mouse dragging within its diameter
 * as well as mouse wheel events when hovered.</p>
 * 
 * @see RangeControl
 */
public final class Knob extends RangeControl {
    /** Starting angle for the indicator arc (0 radians). */
    private static final float START = 0;
    
    /** Ending angle for the indicator arc (2π radians). */
    private static final float END = (float) (PI * 2);
    
    private AbstractColor indicatorColor;
    private float centerX, centerY, diameter;
    private boolean isCanDrag;

    /**
     * Constructs a Knob with specified position and dimensions.
     * The knob is initialized with the theme's primary color for the indicator
     * and minimum/maximum sizes of 10 and 50 pixels respectively.
     *
     * @param x the x-coordinate of the knob's bounding box
     * @param y the y-coordinate of the knob's bounding box
     * @param width the width of the knob's bounding box
     * @param height the height of the knob's bounding box
     */
    public Knob(float x, float y, float width, float height) {
        super(x, y, width, height);
        setMinMaxSize(10, 50);

        indicatorColor = getTheme().getPrimaryColor();

        onDragging(() -> {
            if (isMouseInDiameter()) {
                isCanDrag = true;
            }
        });
    }

    /**
     * Constructs a Knob with default maximum size, centered on the screen.
     * This is a convenience constructor for creating a centered knob.
     */
    public Knob() {
        this(0, 0, 0, 0);
        setSize(getMaxWidth(), getMaxHeight());
        setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
    }

    /**
     * Renders the knob and handles user interaction.
     * The rendering includes:
     * <ol>
     *   <li>The circular background</li>
     *   <li>The arc indicator showing the current value</li>
     *   <li>Interaction state updates</li>
     *   <li>Value updates based on dragging or scrolling</li>
     * </ol>
     */
    @Override
    protected void render() {
        // Draw circular background
        getMutableStroke().apply();
        getBackgroundColor().apply();
        ctx.ellipse(centerX, centerY, diameter, diameter);

        // Draw the value indicator
        indicatorOnDraw();

        // Reset drag state when mouse is released
        if (!ctx.mousePressed) {
            isCanDrag = false;
        }

        // Update value based on vertical dragging
        if (isCanDrag) {
            getMutableValue().append(ctx.pmouseY - ctx.mouseY);
        }

        // Update value based on mouse wheel scrolling
        getMutableValue().append(getMutableScrolling().get());
    }

    /**
     * Called when the knob's bounds change.
     * Recalculates the center point and diameter based on the new bounds.
     */
    @Override
    protected void onChangeBounds() {
        super.onChangeBounds();
        recalculateCenter();
        recalculateDiameter();
    }

    /**
     * Handles mouse wheel events when the mouse is within the knob's diameter.
     * The scrolling delta is captured for value adjustment.
     *
     * @param mouseEvent the mouse wheel event
     */
    @Override
    public void mouseWheel(MouseEvent mouseEvent) {
        if (isMouseInDiameter()) {
            getMutableScrolling().init(mouseEvent);
        }
    }

    /**
     * Gets the color used for the indicator arc.
     *
     * @return the current indicator color
     */
    public AbstractColor getIndicatorColor() {
        return indicatorColor;
    }

    /**
     * Sets the color used for the indicator arc.
     *
     * @param indicatorColor the color to use for the indicator
     * @throws NullPointerException if indicatorColor is null
     */
    public void setIndicatorColor(AbstractColor indicatorColor) {
        this.indicatorColor = requireNonNull(indicatorColor, "indicatorColor");
    }

    /**
     * Recalculates the center coordinates based on current bounds.
     */
    private void recalculateCenter() {
        centerX = getX() + getWidth() / 2;
        centerY = getY() + getHeight() / 2;
    }

    /**
     * Recalculates the diameter based on current bounds.
     * The diameter is the smaller of the width and height.
     */
    private void recalculateDiameter() {
        diameter = min(getWidth(), getHeight());
    }

    /**
     * Checks if the mouse cursor is within the knob's circular area.
     *
     * @return true if the mouse is within the knob's diameter, false otherwise
     */
    private boolean isMouseInDiameter() {
        return dist(centerX, centerY, ctx.mouseX, ctx.mouseY) < diameter / 2;
    }

    /**
     * Draws the arc indicator showing the current value.
     * The indicator consists of:
     * <ul>
     *   <li>A full circle outline (grayed out)</li>
     *   <li>A filled arc representing the current value</li>
     *   <li>A small circle at the center when at maximum value</li>
     * </ul>
     */
    private void indicatorOnDraw() {
        ctx.push();

        // Move to knob center and rotate for proper arc drawing
        ctx.translate(centerX, centerY);
        ctx.rotate((float) PI / 2);
        ctx.noFill();
        ctx.strokeWeight(getIndicatorWeight());

        // Draw full circle outline (potential range)
        indicatorColor.apply();
        ctx.arc(0, 0, diameter * .8f, diameter * .8f, START, END);

        // Draw filled arc representing current value
        indicatorColor.applyStroke();
        ctx.arc(0, 0, diameter * .8f, diameter * .8f, START, 
                MathUtils.convert(getMutableValue().get(),
                                  getMutableValue().getMin(), 
                                  getMutableValue().getMax(), 
                                  START, END));

        // Draw center dot when at maximum value
        if (getMutableValue().get() == getMutableValue().getMax()) {
            ctx.ellipse(0, 0, diameter / 4, diameter / 4);
        }

        ctx.pop();
    }

    /**
     * Calculates the stroke weight for the indicator arc.
     * The weight increases when scrolling is active for visual feedback.
     *
     * @return the stroke weight for the indicator
     */
    private float getIndicatorWeight() {
        return diameter * .1f + abs(getMutableScrolling().get() * 2);
    }
}