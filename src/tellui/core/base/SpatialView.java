package tellui.core.base;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static tellui.core.base.SpatialView.HooksUpdateMode.REACTIVE;
import static telluii.util.MathUtils.constrain;

import tellui.core.effect.SpatialAnimator;
import telluii.util.Debugger;

/**
 * Abstract base class for GUI components that have spatial properties (position
 * and dimensions). Provides comprehensive management of position, size,
 * constraints, and animation capabilities.
 * <p>
 * SpatialView extends View to add spatial properties and constraints system. It
 * manages: - Position (x, y coordinates) and dimensions (width, height) -
 * Minimum and maximum size constraints - Negative dimensions handling - Spatial
 * animation through SpatialAnimator - Update hooks with configurable timing
 * modes
 * </p>
 * 
 * @see View
 * @see SpatialAnimator
 * @see HooksUpdateMode
 */
public abstract class SpatialView extends View {
	private static final int DEFAULT_MIN_SIZE = 1;
	private static final int DEFAULT_MAX_SIZE = 1000;
	private static final float EPSILON = .01f;

	private boolean posDirty, dimDirty, negativeDimensionsEnabled, constrainDimensionsEnabled;
	private float x, y, width, height, minWidth, minHeight, maxWidth, maxHeight;
	private long lastUpdateHooksMs;

	private HooksUpdateMode hooksUpdateMode;

	private SpatialAnimator spatialAnimator;

	/**
	 * Constructs a SpatialView with specified position and dimensions.
	 * 
	 * @param x      the x-coordinate
	 * @param y      the y-coordinate
	 * @param width  the width
	 * @param height the height
	 */
	public SpatialView(float x, float y, float width, float height) {
		initDefaultMinMaxSize();
		setBounds(x, y, width, height);
		setHooksUpdateMode(REACTIVE);
	}

	/**
	 * Constructs a SpatialView by copying properties from another SpatialView.
	 * 
	 * @param spatialView the SpatialView to copy from (cannot be null)
	 * @throws NullPointerException if spatialView is null
	 */
	public SpatialView(SpatialView spatialView) {
		this(requireNonNull(spatialView, "spatialView").getX(), spatialView.getY(), spatialView.getWidth(),
				spatialView.getHeight());
	}

	/**
	 * Constructs a SpatialView at origin (0,0) with default minimum size {@value #DEFAULT_MIN_SIZE}.
	 */
	public SpatialView() {
		this(0, 0, DEFAULT_MIN_SIZE, DEFAULT_MIN_SIZE);
	}

	/**
	 * Draws the SpatialView and manages update hooks and animations. Overrides
	 * parent draw method to add spatial update logic.
	 */
	@Override
	public void draw() {
		if (!isVisible()) {
			return;
		}

		boolean hooksCalled = posDirty || dimDirty;

		if (hooksUpdateMode == REACTIVE) {
			hooksUpdate();
		} else {
			if (currentTimeMillis() - lastUpdateHooksMs >= hooksUpdateMode.getMs()) {
				hooksUpdate();
				lastUpdateHooksMs = currentTimeMillis();
			}
		}

		super.draw();

		if (hooksCalled) {
			debugOnDraw();
		}

		if (spatialAnimator != null) {
			spatialAnimator.update();
		}
	}

	/**
	 * Returns the spatial animator associated with this view.
	 * 
	 * @return the spatial animator, or null if not set
	 */
	public final SpatialAnimator getSpatialAnimator() {
		return spatialAnimator;
	}

	/**
	 * Sets a spatial animator for this view.
	 * 
	 * @param spatialAnimator the spatial animator to set (cannot be null)
	 * @throws NullPointerException if spatialAnimator is null
	 */
	public final void setSpatialAnimator(SpatialAnimator spatialAnimator) {
		this.spatialAnimator = requireNonNull(spatialAnimator, "spatialAnimator");

		spatialAnimator.setTargetSpatialView(this);
	}

	/**
	 * Returns the x-coordinate.
	 * 
	 * @return the x-coordinate
	 */
	public final float getX() {
		return x;
	}

	/**
	 * Sets the x-coordinate.
	 * 
	 * @param x the x-coordinate to set
	 */
	public final void setX(float x) {
		if (areEqual(this.x, x)) {
			return;
		}

		this.x = x;
		posDirty = true;
	}

	/**
	 * Returns the y-coordinate.
	 * 
	 * @return the y-coordinate
	 */
	public final float getY() {
		return y;
	}

	/**
	 * Sets the y-coordinate.
	 * 
	 * @param y the y-coordinate to set
	 */
	public final void setY(float y) {
		if (areEqual(this.y, y)) {
			return;
		}
		this.y = y;
		posDirty = true;
	}

	/**
	 * Returns the width.
	 * 
	 * @return the width
	 */
	public final float getWidth() {
		return width;
	}

	/**
	 * Sets the width with constraint validation.
	 * 
	 * @param width the width to set
	 */
	public final void setWidth(float width) {
		this.width = getCorrectDimension(this.width, width, minWidth, maxWidth);
	}

	/**
	 * Returns the height.
	 * 
	 * @return the height
	 */
	public final float getHeight() {
		return height;
	}

	/**
	 * Sets the height with constraint validation.
	 * 
	 * @param height the height to set
	 */
	public final void setHeight(float height) {
		this.height = getCorrectDimension(this.height, height, minHeight, maxHeight);
	}

	/**
	 * Sets the position (x and y coordinates).
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public final void setPosition(float x, float y) {
		setX(x);
		setY(y);
	}

	/**
	 * Sets the size (width and height).
	 * 
	 * @param width  the width
	 * @param height the height
	 */
	public final void setSize(float width, float height) {
		setWidth(width);
		setHeight(height);
	}

	/**
	 * Sets uniform size for both width and height.
	 * 
	 * @param size the size for both dimensions
	 */
	public final void setSize(float size) {
		setSize(size, size);
	}

	/**
	 * Sets all bounds (position and size).
	 * 
	 * @param x      the x-coordinate
	 * @param y      the y-coordinate
	 * @param width  the width
	 * @param height the height
	 */
	public final void setBounds(float x, float y, float width, float height) {
		setPosition(x, y);
		setSize(width, height);
	}

	/**
	 * Returns the minimum width constraint.
	 * 
	 * @return the minimum width
	 */
	public final float getMinWidth() {
		return minWidth;
	}

	/**
	 * Sets the minimum width constraint.
	 * 
	 * @param minWidth the minimum width to set
	 * @throws IllegalArgumentException if minWidth is greater than current maxWidth
	 */
	public final void setMinWidth(float minWidth) {
		if (areEqual(this.minWidth, minWidth)) {
			return;
		}

		this.minWidth = negativeDimensionsEnabled ? minWidth : max(0, minWidth);

		if (this.minWidth > maxWidth) {
			throw new IllegalArgumentException(String.format("Min width [%d] cannot be greater than max width [%d]",
					(int) minWidth, (int) maxWidth));
		}

		setWidth(max(this.minWidth, width));

	}

	/**
	 * Returns the minimum height constraint.
	 * 
	 * @return the minimum height
	 */
	public final float getMinHeight() {
		return minHeight;
	}

	/**
	 * Sets the minimum height constraint.
	 * 
	 * @param minHeight the minimum height to set
	 * @throws IllegalArgumentException if minHeight is greater than current
	 *                                  maxHeight
	 */
	public final void setMinHeight(float minHeight) {
		if (areEqual(this.minHeight, minHeight)) {
			return;
		}

		this.minHeight = negativeDimensionsEnabled ? minHeight : max(0, minHeight);

		if (this.minHeight > maxHeight) {
			throw new IllegalArgumentException(String.format("Min height [%d] cannot be greater than max height [%d]",
					(int) minHeight, (int) maxHeight));
		}

		setHeight(max(this.minHeight, height));
	}

	/**
	 * Returns the maximum width constraint.
	 * 
	 * @return the maximum width
	 */
	public final float getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Sets the maximum width constraint.
	 * 
	 * @param maxWidth the maximum width to set
	 * @throws IllegalArgumentException if maxWidth is less than current minWidth
	 */
	public final void setMaxWidth(float maxWidth) {
		if (areEqual(this.maxWidth, maxWidth)) {
			return;
		}

		this.maxWidth = negativeDimensionsEnabled ? maxWidth : max(0, maxWidth);

		if (this.maxWidth < minWidth) {
			throw new IllegalArgumentException(String.format("Max width [%d] cannot be lower than min width [%d]",
					(int) maxWidth, (int) minWidth));
		}

		setWidth(min(this.maxWidth, width));

	}

	/**
	 * Returns the maximum height constraint.
	 * 
	 * @return the maximum height
	 */
	public final float getMaxHeight() {
		return maxHeight;
	}

	/**
	 * Sets the maximum height constraint.
	 * 
	 * @param maxHeight the maximum height to set
	 * @throws IllegalArgumentException if maxHeight is less than current minHeight
	 */
	public final void setMaxHeight(float maxHeight) {
		if (areEqual(this.maxHeight, maxHeight)) {
			return;
		}

		this.maxHeight = negativeDimensionsEnabled ? maxHeight : max(0, maxHeight);

		if (this.maxHeight < minHeight) {
			throw new IllegalArgumentException(String.format("Max height [%d] cannot be lower than min height [%d]",
					(int) maxHeight, (int) minHeight));
		}

		setHeight(min(this.maxHeight, height));
	}

	/**
	 * Sets both minimum width and height constraints.
	 * 
	 * @param minWidth  the minimum width
	 * @param minHeight the minimum height
	 */
	public final void setMinSize(float minWidth, float minHeight) {
		setMinWidth(minWidth);
		setMinHeight(minHeight);
	}

	/**
	 * Sets uniform minimum size for both dimensions.
	 * 
	 * @param minSize the minimum size for both width and height
	 */
	public final void setMinSize(float minSize) {
		setMinWidth(minSize);
		setMinHeight(minSize);
	}

	/**
	 * Sets both maximum width and height constraints.
	 * 
	 * @param maxWidth  the maximum width
	 * @param maxHeight the maximum height
	 */
	public final void setMaxSize(float maxWidth, float maxHeight) {
		setMaxWidth(maxWidth);
		setMaxHeight(maxHeight);
	}

	/**
	 * Sets uniform maximum size for both dimensions.
	 * 
	 * @param maxSize the maximum size for both width and height
	 */
	public final void setMaxSize(float maxSize) {
		setMaxWidth(maxSize);
		setMaxHeight(maxSize);
	}

	/**
	 * Sets both minimum and maximum size constraints uniformly.
	 * 
	 * @param minSize the minimum size for both dimensions
	 * @param maxSize the maximum size for both dimensions
	 */
	public final void setMinMaxSize(float minSize, float maxSize) {
		setMinSize(minSize);
		setMaxSize(maxSize);
	}

	/**
	 * Sets all minimum and maximum size constraints.
	 * 
	 * @param minWidth  the minimum width
	 * @param minHeight the minimum height
	 * @param maxWidth  the maximum width
	 * @param maxHeight the maximum height
	 */
	public final void setMinMaxSize(float minWidth, float minHeight, float maxWidth, float maxHeight) {
		setMinWidth(minWidth);
		setMinHeight(minHeight);
		setMaxWidth(maxWidth);
		setMaxHeight(maxHeight);
	}

	/**
	 * Sets both minimum and maximum size constraints to the same value.
	 * 
	 * @param size the size for both minimum and maximum constraints
	 */
	public final void setMinMaxSize(float size) {
		setMinMaxSize(size, size);
	}

	/**
	 * Checks if dimension constraints are enabled.
	 * 
	 * @return true if dimension constraints are enabled, false otherwise
	 */
	public final boolean isConstrainDimensionsEnabled() {
		return constrainDimensionsEnabled;
	}

	/**
	 * Enables or disables dimension constraints.
	 * 
	 * @param constrainDimensionsEnabled true to enable constraints, false to
	 *                                   disable
	 */
	public final void setConstrainDimensionsEnabled(boolean constrainDimensionsEnabled) {
		if (this.constrainDimensionsEnabled == constrainDimensionsEnabled) {
			return;
		}

		this.constrainDimensionsEnabled = constrainDimensionsEnabled;
		setSize(width, height);

	}

	/**
	 * Appends a delta value to the x-coordinate.
	 * 
	 * @param delta the value to add to x-coordinate
	 */
	public final void appendX(float delta) {
		setX(getX() + delta);
	}

	/**
	 * Appends a delta value to the x-coordinate within specified bounds.
	 * 
	 * @param delta the value to add to x-coordinate
	 * @param min   the minimum allowed x-coordinate
	 * @param max   the maximum allowed x-coordinate
	 */
	public final void appendX(float delta, float min, float max) {
		setX(constrain(getX() + delta, min, max));
	}

	/**
	 * Appends a delta value to the y-coordinate.
	 * 
	 * @param delta the value to add to y-coordinate
	 */
	public final void appendY(float delta) {
		setY(getY() + delta);
	}

	/**
	 * Appends a delta value to the y-coordinate within specified bounds.
	 * 
	 * @param delta the value to add to y-coordinate
	 * @param min   the minimum allowed y-coordinate
	 * @param max   the maximum allowed y-coordinate
	 */
	public final void appendY(float delta, float min, float max) {
		setY(constrain(getY() + delta, min, max));
	}

	/**
	 * Appends a delta value to the width.
	 * 
	 * @param delta the value to add to width
	 */
	public final void appendWidth(float delta) {
		setWidth(getWidth() + delta);
	}

	/**
	 * Appends a delta value to the width within specified bounds.
	 * 
	 * @param delta the value to add to width
	 * @param min   the minimum allowed width
	 * @param max   the maximum allowed width
	 */
	public final void appendWidth(float delta, float min, float max) {
		setWidth(constrain(getWidth() + delta, min, max));
	}

	/**
	 * Appends a delta value to the height.
	 * 
	 * @param delta the value to add to height
	 */
	public final void appendHeight(float delta) {
		setHeight(getHeight() + delta);
	}

	/**
	 * Appends a delta value to the height within specified bounds.
	 * 
	 * @param delta the value to add to height
	 * @param min   the minimum allowed height
	 * @param max   the maximum allowed height
	 */
	public final void appendHeight(float delta, float min, float max) {
		setHeight(constrain(getHeight() + delta, min, max));
	}

	/**
	 * Appends a delta value to both width and height.
	 * 
	 * @param delta the value to add to both dimensions
	 */
	public final void appendSize(float delta) {
		setSize(getWidth() + delta, getHeight() + delta);
	}

	/**
	 * Appends a delta value to both width and height within specified bounds.
	 * 
	 * @param delta the value to add to both dimensions
	 * @param min   the minimum allowed size
	 * @param max   the maximum allowed size
	 */
	public final void appendSize(float delta, float min, float max) {
		setSize(constrain(getWidth() + delta, min, max), constrain(getHeight() + delta, min, max));
	}

	/**
	 * Returns the current hooks update mode.
	 * 
	 * @return the current hooks update mode
	 */
	public final HooksUpdateMode getHooksUpdateMode() {
		return hooksUpdateMode;
	}

	/**
	 * Sets the hooks update mode.
	 * 
	 * @param hooksUpdateMode the update mode to set (cannot be null)
	 * @throws NullPointerException if hooksUpdateMode is null
	 */
	public final void setHooksUpdateMode(HooksUpdateMode hooksUpdateMode) {
		this.hooksUpdateMode = requireNonNull(hooksUpdateMode, "hooksUpdateMode");
	}

	/**
	 * Sets the x-coordinate from another SpatialView.
	 * 
	 * @param other the SpatialView to copy x-coordinate from (cannot be null)
	 */
	public final void setXFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setX(other.getX());
	}

	/**
	 * Sets the y-coordinate from another SpatialView.
	 * 
	 * @param other the SpatialView to copy y-coordinate from (cannot be null)
	 */
	public final void setYFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setY(other.getY());
	}

	/**
	 * Sets the height from another SpatialView.
	 * 
	 * @param other the SpatialView to copy height from (cannot be null)
	 */
	public final void setHeightFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setHeight(other.getHeight());
	}

	/**
	 * Sets the width from another SpatialView.
	 * 
	 * @param other the SpatialView to copy width from (cannot be null)
	 */
	public final void setWidthFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setWidth(other.getWidth());
	}

	/**
	 * Sets the position from another SpatialView.
	 * 
	 * @param other the SpatialView to copy position from (cannot be null)
	 */
	public final void setPositionFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setPosition(other.getX(), other.getY());
	}

	/**
	 * Sets the size from another SpatialView.
	 * 
	 * @param other the SpatialView to copy size from (cannot be null)
	 */
	public final void setSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setSize(other.getWidth(), other.getHeight());
	}

	/**
	 * Sets all bounds from another SpatialView.
	 * 
	 * @param other the SpatialView to copy bounds from (cannot be null)
	 */
	public final void setBoundsFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setBounds(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}

	/**
	 * Sets the minimum width from another SpatialView.
	 * 
	 * @param other the SpatialView to copy minimum width from (cannot be null)
	 */
	public final void setMinWidthFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinWidth(other.getMinWidth());
	}

	/**
	 * Sets the minimum height from another SpatialView.
	 * 
	 * @param other the SpatialView to copy minimum height from (cannot be null)
	 */
	public final void setMinHeightFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinHeight(other.getMinHeight());
	}

	/**
	 * Sets the maximum width from another SpatialView.
	 * 
	 * @param other the SpatialView to copy maximum width from (cannot be null)
	 */
	public final void setMaxWidthFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMaxWidth(other.getMaxWidth());
	}

	/**
	 * Sets the maximum height from another SpatialView.
	 * 
	 * @param other the SpatialView to copy maximum height from (cannot be null)
	 */
	public final void setMaxHeightFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMaxHeight(other.getMaxHeight());
	}

	/**
	 * Sets the maximum size from another SpatialView.
	 * 
	 * @param other the SpatialView to copy maximum size from (cannot be null)
	 */
	public final void setMaxSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMaxWidthFrom(other);
		setMaxHeightFrom(other);
	}

	/**
	 * Sets the minimum size from another SpatialView.
	 * 
	 * @param other the SpatialView to copy minimum size from (cannot be null)
	 */
	public final void setMinSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinWidthFrom(other);
		setMinHeightFrom(other);
	}

	/**
	 * Sets both minimum and maximum size from another SpatialView.
	 * 
	 * @param other the SpatialView to copy size constraints from (cannot be null)
	 */
	public final void setMinMaxSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinSize(other.getMinWidth(), other.getMinHeight());
		setMaxSize(other.getMaxWidth(), other.getMaxHeight());
	}

	/**
	 * Copies all spatial configuration from another SpatialView. Includes
	 * constraints and bounds.
	 * 
	 * @param other the SpatialView to copy configuration from (cannot be null)
	 */
	public final void setSpatialConfigFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setConstrainConfigFrom(other);
		setBoundsFrom(other);
	}

	/**
	 * Copies constraint configuration from another SpatialView.
	 * 
	 * @param other the SpatialView to copy constraints from (cannot be null)
	 */
	public final void setConstrainConfigFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinWidth(other.getMinWidth());
		setMinHeight(other.getMinHeight());
		setMaxWidth(other.getMaxWidth());
		setMaxHeight(other.getMaxHeight());
		setConstrainDimensionsEnabled(other.isConstrainDimensionsEnabled());
		setNegativeDimensionsEnabled(other.isNegativeDimensionsEnabled());
	}

	/**
	 * Hook method called when position changes. Override to implement custom
	 * behavior.
	 */
	protected void onChangePosition() {
	}

	/**
	 * Hook method called when dimensions change. Override to implement custom
	 * behavior.
	 */
	protected void onChangeDimensions() {
	}

	/**
	 * Hook method called when bounds (position or dimensions) change. Override to
	 * implement custom behavior.
	 */
	protected void onChangeBounds() {
	}

	/**
	 * Checks if negative dimensions are allowed.
	 * 
	 * @return true if negative dimensions are enabled, false otherwise
	 */
	protected final boolean isNegativeDimensionsEnabled() {
		return negativeDimensionsEnabled;
	}

	/**
	 * Enables or disables negative dimensions.
	 * 
	 * @param negativeDimensionsEnabled true to allow negative dimensions, false to
	 *                                  restrict to non-negative values
	 */
	protected final void setNegativeDimensionsEnabled(boolean negativeDimensionsEnabled) {
		if (this.negativeDimensionsEnabled == negativeDimensionsEnabled) {
			return;
		}

		this.negativeDimensionsEnabled = negativeDimensionsEnabled;

		if (negativeDimensionsEnabled) {
			setSize(width, height);
		}
	}

	/**
	 * Requests an update of state hooks.
	 * <p>
	 * This method calls hooks forcefully, even if there were no actual changes in
	 * position or dimensions.
	 * </p>
	 */
	protected final void requestUpdate() {
		onChangePosition();
		onChangeDimensions();
		onChangeBounds();
	}

	private static boolean areEqual(float firstValue, float secondValue) {
		return abs(firstValue - secondValue) < EPSILON;
	}

	private void hooksUpdate() {
		if (posDirty || dimDirty) {
			onChangeBounds();
			if (posDirty) {
				onChangePosition();
			}
			if (dimDirty) {
				onChangeDimensions();
			}
			posDirty = dimDirty = false;
		}
	}

	private float getCorrectDimension(float currentValue, float newValue, float min, float max) {
		if (areEqual(currentValue, newValue)) {
			return currentValue;
		}

		float correctValue = currentValue;

		if (constrainDimensionsEnabled) {
			correctValue = constrain(newValue, min, max);
		} else {
			if (negativeDimensionsEnabled) {
				correctValue = newValue;
			} else {
				correctValue = max(0, newValue);
			}
		}

		dimDirty = !areEqual(currentValue, correctValue);

		return correctValue;
	}

	private void initDefaultMinMaxSize() {
		minWidth = minHeight = DEFAULT_MIN_SIZE;
		maxWidth = maxHeight = DEFAULT_MAX_SIZE;
	}

	private void checkSpatialViewObject(SpatialView other) {
		requireNonNull(other, "other");

		if (other == this) {
			throw new IllegalArgumentException("Cannot set property from itself");
		}

	}

	private void debugOnDraw() {
		if (Debugger.isEnabled() && Debugger.isShowUpdateBoundsEnabled()) {
			ctx.fill(255, 0, 0, 64);
			ctx.rect(getX(), getY(), getWidth(), getHeight());
		}
	}

	/**
	 * Update modes for change hooks with different timing intervals.
	 */
	public static enum HooksUpdateMode {
		/** Hooks are called immediately when changes occur. */
		REACTIVE(0),
		/** Hooks are called approximately every 16ms (~60fps). */
		FAST(16),
		/** Hooks are called approximately every 32ms (~30fps). */
		NORMAL(32),
		/** Hooks are called approximately every 64ms (~15fps). */
		SLOW(64),
		/** Hooks are called approximately every 128ms (~8fps). */
		VERY_SLOW(128);

		private final int ms;

		/**
		 * Creates a HooksUpdateMode with specified interval.
		 * 
		 * @param ms the time interval in milliseconds
		 */
		private HooksUpdateMode(int ms) {
			this.ms = ms;
		}

		/**
		 * Returns the time interval in milliseconds.
		 * 
		 * @return the time interval in milliseconds
		 */
		public int getMs() {
			return ms;
		}

	}
}