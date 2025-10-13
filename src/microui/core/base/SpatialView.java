package microui.core.base;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static microui.core.base.SpatialView.HooksUpdateMode.REACTIVE;

import microui.MicroUI;
import microui.core.effect.SpatialAnimator;

//Status: STABLE - Do not modify
//Last Reviewed: 11.10.2025
public abstract class SpatialView extends View {
	private static final int DEFAULT_MIN_SIZE = 1;
	private static final int DEFAULT_MAX_SIZE = 1000;
	private static final float EPSILON = .01f;

	private boolean isPosDirty, isDimDirty, isNegativeDimensionsEnabled, isConstrainDimensionsEnabled;
	private float x, y, width, height, minWidth, minHeight, maxWidth, maxHeight;
	private long lastUpdateHooksMs;

	private HooksUpdateMode hooksUpdateMode;

	private SpatialAnimator spatialAnimator;

	public SpatialView(float x, float y, float width, float height) {
		initDefaultMinMaxSize();
		setBounds(x, y, width, height);
		setHooksUpdateMode(REACTIVE);
	}

	public SpatialView(SpatialView spatialView) {
		this(requireNonNull(spatialView, "spatialView cannot be null").getX(), spatialView.getY(),
				spatialView.getWidth(), spatialView.getHeight());
	}

	public SpatialView() {
		this(0, 0, DEFAULT_MIN_SIZE, DEFAULT_MIN_SIZE);
	}

	@Override
	public void draw() {
		if (!isVisible()) {
			return;
		}

		boolean isHooksCalling = isPosDirty || isDimDirty;

		if (hooksUpdateMode == REACTIVE) {
			hooksUpdate();
		} else {
			if (currentTimeMillis() - lastUpdateHooksMs >= hooksUpdateMode.getMs()) {
				hooksUpdate();
				lastUpdateHooksMs = currentTimeMillis();
			}
		}

		super.draw();

		if (isHooksCalling) {
			debugOnDraw();
		}

		if (spatialAnimator != null) {
			spatialAnimator.update();
		}
	}

	public final SpatialAnimator getSpatialAnimator() {
		return spatialAnimator;
	}

	public final void setSpatialAnimator(SpatialAnimator spatialAnimator) {
		if (spatialAnimator == null) {
			throw new NullPointerException("the spatialAnimator object cannot be null");
		}

		this.spatialAnimator = spatialAnimator;

		spatialAnimator.setTargetSpatialView(this);
	}

	public final float getX() {
		return x;
	}

	public final void setX(float x) {
		if (areEqual(this.x, x)) {
			return;
		}

		this.x = x;
		isPosDirty = true;
	}

	public final float getY() {
		return y;
	}

	public final void setY(float y) {
		if (areEqual(this.y, y)) {
			return;
		}
		this.y = y;
		isPosDirty = true;
	}

	public final float getWidth() {
		return width;
	}

	public final void setWidth(float width) {
		this.width = getCorrectDimension(this.width, width, minWidth, maxWidth);
	}

	public final float getHeight() {
		return height;
	}

	public final void setHeight(float height) {
		this.height = getCorrectDimension(this.height, height, minHeight, maxHeight);
	}

	public final void setPosition(float x, float y) {
		setX(x);
		setY(y);
	}

	public final void setSize(float width, float height) {
		setWidth(width);
		setHeight(height);
	}

	public final void setSize(float size) {
		setSize(size, size);
	}

	public final void setBounds(float x, float y, float width, float height) {
		setPosition(x, y);
		setSize(width, height);
	}

	public final float getMinWidth() {
		return minWidth;
	}

	public final void setMinWidth(float minWidth) {
		if (areEqual(this.minWidth, minWidth)) {
			return;
		}

		this.minWidth = isNegativeDimensionsEnabled ? minWidth : max(0, minWidth);

		if (this.minWidth > maxWidth) {
			throw new IllegalArgumentException("min width cannot be greater than max width");
		}

		setWidth(max(this.minWidth, width));

	}

	public final float getMinHeight() {
		return minHeight;
	}

	public final void setMinHeight(float minHeight) {
		if (areEqual(this.minHeight, minHeight)) {
			return;
		}

		this.minHeight = isNegativeDimensionsEnabled ? minHeight : max(0, minHeight);

		if (this.minHeight > maxHeight) {
			throw new IllegalArgumentException("min height cannot be greater than max height");
		}

		setHeight(max(this.minHeight, height));
	}

	public final float getMaxWidth() {
		return maxWidth;
	}

	public final void setMaxWidth(float maxWidth) {
		if (areEqual(this.maxWidth, maxWidth)) {
			return;
		}

		this.maxWidth = isNegativeDimensionsEnabled ? maxWidth : max(0, maxWidth);

		if (this.maxWidth < minWidth) {
			throw new IllegalArgumentException("max width cannot be lower than min width");
		}

		setWidth(min(this.maxWidth, width));

	}

	public final float getMaxHeight() {
		return maxHeight;
	}

	public final void setMaxHeight(float maxHeight) {
		if (areEqual(this.maxHeight, maxHeight)) {
			return;
		}

		this.maxHeight = isNegativeDimensionsEnabled ? maxHeight : max(0, maxHeight);

		if (this.maxHeight < minHeight) {
			throw new IllegalArgumentException("max height cannot be lower than min height");
		}

		setHeight(min(this.maxHeight, height));
	}

	public final void setMinSize(float minWidth, float minHeight) {
		setMinWidth(minWidth);
		setMinHeight(minHeight);
	}

	public final void setMinSize(float minSize) {
		setMinWidth(minSize);
		setMinHeight(minSize);
	}

	public final void setMaxSize(float maxWidth, float maxHeight) {
		setMaxWidth(maxWidth);
		setMaxHeight(maxHeight);
	}

	public final void setMaxSize(float maxSize) {
		setMaxWidth(maxSize);
		setMaxHeight(maxSize);
	}

	public final void setMinMaxSize(float minSize, float maxSize) {
		setMinSize(minSize);
		setMaxSize(maxSize);
	}

	public final void setMinMaxSize(float minWidth, float minHeight, float maxWidth, float maxHeight) {
		setMinWidth(minWidth);
		setMinHeight(minHeight);
		setMaxWidth(maxWidth);
		setMaxHeight(maxHeight);
	}

	public final void setMinMaxSize(float size) {
		setMinMaxSize(size, size);
	}

	public final boolean isConstrainDimensionsEnabled() {
		return isConstrainDimensionsEnabled;
	}

	public final void setConstrainDimensionsEnabled(boolean isConstrainDimensionsEnabled) {
		if (this.isConstrainDimensionsEnabled == isConstrainDimensionsEnabled) {
			return;
		}

		this.isConstrainDimensionsEnabled = isConstrainDimensionsEnabled;
		setSize(width, height);

	}

	public final void appendX(float delta) {
		setX(getX() + delta);
	}

	public final void appendX(float delta, float min, float max) {
		setX(constrain(getX() + delta, min, max));
	}

	public final void appendY(float delta) {
		setY(getY() + delta);
	}

	public final void appendY(float delta, float min, float max) {
		setY(constrain(getY() + delta, min, max));
	}

	public final void appendWidth(float delta) {
		setWidth(getWidth() + delta);
	}

	public final void appendWidth(float delta, float min, float max) {
		setWidth(constrain(getWidth() + delta, min, max));
	}

	public final void appendHeight(float delta) {
		setHeight(getHeight() + delta);
	}

	public final void appendHeight(float delta, float min, float max) {
		setHeight(constrain(getHeight() + delta, min, max));
	}

	public final void appendSize(float delta) {
		setSize(getWidth() + delta, getHeight() + delta);
	}

	public final void appendSize(float delta, float min, float max) {
		setSize(constrain(getWidth() + delta, min, max), constrain(getHeight() + delta, min, max));
	}

	public final HooksUpdateMode getHooksUpdateMode() {
		return hooksUpdateMode;
	}

	public final void setHooksUpdateMode(HooksUpdateMode hooksUpdateMode) {
		if (hooksUpdateMode == null) {
			throw new NullPointerException("hooksUpdateMode cannot be null");
		}
		this.hooksUpdateMode = hooksUpdateMode;
	}

	public final void setXFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setX(other.getX());
	}

	public final void setYFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setY(other.getY());
	}

	public final void setHeightFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setHeight(other.getHeight());
	}

	public final void setWidthFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setWidth(other.getWidth());
	}

	public final void setPositionFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setPosition(other.getX(), other.getY());
	}

	public final void setSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setSize(other.getWidth(), other.getHeight());
	}

	public final void setBoundsFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setBounds(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}

	public final void setMinWidthFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinWidth(other.getMinWidth());
	}

	public final void setMinHeightFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinHeight(other.getMinHeight());
	}

	public final void setMaxWidthFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMaxWidth(other.getMaxWidth());
	}

	public final void setMaxHeightFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMaxHeight(other.getMaxHeight());
	}

	public final void setMaxSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMaxWidthFrom(other);
		setMaxHeightFrom(other);
	}

	public final void setMinSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinWidthFrom(other);
		setMinHeightFrom(other);
	}

	public final void setMinMaxSizeFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinSize(other.getMinWidth(), other.getMinHeight());
		setMaxSize(other.getMaxWidth(), other.getMaxHeight());
	}

	public final void setSpatialConfigFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setConstrainConfigFrom(other);
		setBoundsFrom(other);
	}

	public final void setConstrainConfigFrom(SpatialView other) {
		checkSpatialViewObject(other);

		setMinWidth(other.getMinWidth());
		setMinHeight(other.getMinHeight());
		setMaxWidth(other.getMaxWidth());
		setMaxHeight(other.getMaxHeight());
		setConstrainDimensionsEnabled(other.isConstrainDimensionsEnabled());
		setNegativeDimensionsEnabled(other.isNegativeDimensionsEnabled());
	}

	protected void onChangePosition() {
	}

	protected void onChangeDimensions() {
	}

	protected void onChangeBounds() {
	}

	protected final boolean isNegativeDimensionsEnabled() {
		return isNegativeDimensionsEnabled;
	}

	protected final void setNegativeDimensionsEnabled(boolean isNegativeDimensionsEnabled) {
		if (this.isNegativeDimensionsEnabled == isNegativeDimensionsEnabled) {
			return;
		}

		this.isNegativeDimensionsEnabled = isNegativeDimensionsEnabled;

		if (isNegativeDimensionsEnabled) {
			setSize(width, height);
		}
	}

	/**
	 * request for update state
	 * <p>
	 * this method calling hooks force, even if not was really changes in position
	 * or dimensions
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

	// static int hooksCalledCount;
	private void hooksUpdate() {
		if (isPosDirty || isDimDirty) {
			onChangeBounds();
			if (isPosDirty) {
				onChangePosition();
			}
			if (isDimDirty) {
				onChangeDimensions();
			}
			// hooksCalledCount++;
			// System.out.println("count of called hooks: "+hooksCalledCount);
			// Metrics.printAll();

			isPosDirty = isDimDirty = false;
		}
	}

	private float getCorrectDimension(float currentValue, float newValue, float min, float max) {
		if (areEqual(currentValue, newValue)) {
			return currentValue;
		}

		float correctValue = currentValue;

		float constrainedValue = constrain(newValue, min, max);

		if (isConstrainDimensionsEnabled) {
			correctValue = constrainedValue;
		} else {
			if (isNegativeDimensionsEnabled) {
				correctValue = newValue;
			} else {
				correctValue = max(0, newValue);
			}
		}

		isDimDirty = !areEqual(currentValue, correctValue);

		return correctValue;
	}

	private static float constrain(float value, float min, float max) {
		return value < min ? min : value > max ? max : value;
	}

	private void initDefaultMinMaxSize() {
		minWidth = minHeight = DEFAULT_MIN_SIZE;
		maxWidth = maxHeight = DEFAULT_MAX_SIZE;
	}

	private void checkSpatialViewObject(SpatialView other) {
		requireNonNull(other, "other SpatialView cannot be null");

		if (other == this) {
			throw new IllegalArgumentException("Cannot set property from itself");
		}
	}

	private void debugOnDraw() {
		if (MicroUI.isDebugModeEnabled()) {
			ctx.fill(255, 0, 0, 64);
			ctx.rect(getX(), getY(), getWidth(), getHeight());
		}
	}

	public static enum HooksUpdateMode {
		REACTIVE(0), FAST(16), NORMAL(32), SLOW(64), VERY_SLOW(128);

		private final int ms;

		HooksUpdateMode(int ms) {
			this.ms = ms;
		}

		public int getMs() {
			return ms;
		}

	}

}