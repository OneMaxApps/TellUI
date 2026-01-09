package microui.layout;

import microui.core.base.Container.Entry;
import microui.core.base.ContentView;

/**
 * Abstract base class for linear axis layouts that arrange components along a single axis.
 * 
 * <p>This layout manager supports both vertical and horizontal arrangements and uses
 * weight-based distribution of space among components. Components can be arranged
 * in two different constraint modes.</p>
 */
public abstract class LinearAxisLayout extends LayoutManager {
    private static final float EPSILON = .01f;
    private static final float TOTAL_WEIGHT = 1.0f;
    private boolean verticalMode;

    /**
     * Recalculates the layout by positioning and sizing all entries based on their weights.
     * 
     * <p>The layout supports two modes:
     * <ul>
     *   <li>IGNORE_CONSTRAINTS: Ignores component constraints and positions them based solely on weight</li>
     *   <li>RESPECT_CONSTRAINTS: Positions components with respect to their alignment parameters</li>
     * </ul>
     * 
     * <p>In vertical mode, components are stacked vertically with heights proportional to their weights.
     * In horizontal mode, components are arranged horizontally with widths proportional to their weights.</p>
     */
    @Override
    public void recalculate() {
        float usedWeight = 0;

        float containerX = getContainer().getX();
        float containerY = getContainer().getY();
        float containerW = getContainer().getWidth();
        float containerH = getContainer().getHeight();

        for (Entry entry : getEntryList()) {
            ContentView contentView = entry.contentView();
            LinearAxisLayoutParams params = (LinearAxisLayoutParams) entry.layoutParams();

            float usedSpace = verticalMode ? containerH * usedWeight : containerW * usedWeight;

            switch (getContainer().getMode()) {

            case IGNORE_CONSTRAINTS:
                contentView.setConstrainDimensionsEnabled(false);
                contentView.setAbsolutePosition(verticalMode ? containerX : containerX + usedSpace,
                		verticalMode ? containerY + usedSpace : containerY);
                contentView.setAbsoluteWidth(verticalMode ? containerW : containerW * params.getWeight());
                contentView.setAbsoluteHeight(verticalMode ? containerH * params.getWeight() : containerH);

                break;

            case RESPECT_CONSTRAINTS:
                float alignXLeft = containerX;
                float alignXCenter = containerX + containerW / 2 - contentView.getAbsoluteWidth() / 2;
                float alignXRight = containerX + containerW - contentView.getAbsoluteWidth();

                float alignYTop = containerY;
                float alignYCenter = containerY + containerH / 2 - contentView.getAbsoluteHeight() / 2;
                float alignYBottom = containerY + containerH - contentView.getAbsoluteHeight();

                contentView.setAbsoluteX(verticalMode
                        ? params.getAlignX() == -1 ? alignXLeft : params.getAlignX() == 1 ? alignXRight : alignXCenter
                        : containerX + usedSpace);
                contentView.setAbsoluteY(verticalMode ? containerY + usedSpace
                        : params.getAlignY() == -1 ? alignYTop : params.getAlignY() == 1 ? alignYBottom : alignYCenter);
                contentView.setAbsoluteWidth(verticalMode ? containerW : containerW * params.getWeight());
                contentView.setAbsoluteHeight(verticalMode ? containerH * params.getWeight() : containerH);

                break;

            }

            usedWeight += params.getWeight();
        }
    }

    /**
     * Draws debug visualization of the layout.
     * 
     * <p>Displays semi-transparent red rectangles showing the allocated space
     * for each component based on their weights in the layout.</p>
     */
    @Override
    public void debugOnDraw() {
        ctx.pushStyle();
        ctx.stroke(0);
        ctx.fill(200, 0, 0, 32);

        getEntryList().forEach(entry -> {
            ContentView contentView = entry.contentView();
            LinearAxisLayoutParams params = (LinearAxisLayoutParams) entry.layoutParams();
            if (verticalMode) {
                ctx.rect(getContainer().getX(), contentView.getAbsoluteY(), getContainer().getWidth(),
                        getContainer().getHeight() * params.getWeight());
            } else {
                ctx.rect(contentView.getAbsoluteX(), getContainer().getY(),
                        getContainer().getWidth() * params.getWeight(), getContainer().getHeight());
            }
        });

        ctx.popStyle();
    }

    /**
     * Validates that the provided layout parameters are of the correct type.
     * 
     * @param layoutParams the layout parameters to validate
     * @throws IllegalArgumentException if the parameters are not an instance of LinearAxisLayoutParams
     */
    @Override
    protected void checkCorrectParams(LayoutParams layoutParams) {
        if (!(layoutParams instanceof LinearAxisLayoutParams)) {
            throw new IllegalArgumentException("using not correct layout params for LinearAxisLayoutParams");
        }
    }

    /**
     * Returns whether the layout is in vertical mode.
     * 
     * @return true if the layout is vertical, false if horizontal
     */
    protected boolean isVerticalMode() {
        return verticalMode;
    }

    /**
     * Sets the orientation mode of the layout.
     * 
     * <p>If the mode changes, triggers a recalculation of the layout.</p>
     * 
     * @param isVerticalMode true for vertical layout, false for horizontal layout
     */
    protected void setVerticalMode(boolean verticalMode) {
        if (this.verticalMode == verticalMode) {
            return;
        }
        this.verticalMode = verticalMode;
        if (getContainer() != null) {
            recalculate();
        }
    }

    /**
     * Checks if the total weight of all entries exceeds the available space.
     * 
     * <p>Compares the sum of all component weights against TOTAL_WEIGHT (1.0)
     * with a small epsilon for floating-point comparison.</p>
     * 
     * @return true if the total weight exceeds available space, false otherwise
     */
    protected boolean isOutOfSpace() {
        float usedWeight = 0;
        for (Entry entry : getEntryList()) {
            LinearAxisLayoutParams params = (LinearAxisLayoutParams) entry.layoutParams();
            usedWeight += params.getWeight();
        }
        if (usedWeight - TOTAL_WEIGHT > EPSILON) {
            return true;
        }

        return false;
    }
}