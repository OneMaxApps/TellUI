package microui.component;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.convert;
import static processing.core.PApplet.dist;
import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.TWO_PI;

import microui.core.RangeControl;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.event.PointerManager;
import microui.util.Environment;
import processing.event.MouseEvent;

//Status: STABLE - Do not modify
//Last Reviewed: 01.03.2026

/**
 * A graphical component for providing Knob
 */
public final class Knob extends RangeControl {
	private static final float DEFAULT_START_ANGLE = HALF_PI/2;
	private static final float DEFAULT_END_ANGLE = TWO_PI - HALF_PI / 2;
	private static final float DEFAULT_SCALE_WEIGHT_RATIO = 1f;
	private static final float MIN_SCALE_WEIGHT_RATIO = 0f;
	private static final float MAX_SCALE_WEIGHT_RATIO = 2f;
	private static final float SCALE_START_WEIGHT = .05f;
	private static final float SCALE_END_WEIGHT = .1f;		
	private static final float DEFAULT_HANDLE_SIZE_RATIO = .2f;
	private static final float DEFAULT_HANDLE_OFFSET_RATIO = .3f;
	private static final float MIN_HANDLE_SIZE_RATIO = .1f;
	private static final float MAX_HANDLE_SIZE_RATIO = 1f;
	private static final float MIN_HANDLE_OFFSET_RATIO = 0f;
	private static final float MAX_HANDLE_OFFSET_RATIO = .4f;
	private static final float MAX_DRAGGING_DIST = dist(0,0,ctx.width,ctx.height);
	private static final float MIN_MANUAL_DRAGGING_SPEED = .1f;
	private static final float MANUAL_DRAGGING_SENSITIVITY_FACTOR = .01f;
	
	private AbstractColor scaleColor, handleColor;
	private float scaleWeightRatio;
	
	private float startAngle, endAngle;
	private float cachedCenterX, cachedCenterY, cachedSize;
	private float cachedScaleWeight;
	private float handleOffsetRatio, handleSizeRatio;
	private boolean draggableState;
	
	private Float defaultValue;

	/**
	 * Default constructor for initialization of bounds
	 * @param x position X
	 * @param y position Y
	 * @param width current width
	 * @param height current height
	 */
	public Knob(float x, float y, float width, float height) {
		super(x, y, width, height);
		setMinMaxSize(20, 20, 100, 100);
		
		setAngles(DEFAULT_START_ANGLE, DEFAULT_END_ANGLE);

		setScaleColor(new Color(0,255,0,200), new Color(255,128,0,200));
		
		setHandleColor(new Color(128,128));
		
		setHandleSizeRatio(DEFAULT_HANDLE_SIZE_RATIO);
		setHandleOffsetRatio(DEFAULT_HANDLE_OFFSET_RATIO);
		
		setScaleWeightRatio(DEFAULT_SCALE_WEIGHT_RATIO);
		
		onDragStart(() -> {
			draggableState = mouseInsideCircle();
			notifyOnStartChangeValueListeners();
		});
		
		onRelease(() -> {
			draggableState = false;
			notifyOnEndChangeValueListeners();
		});
		
		onDoubleClick(() -> {
			if (mouseInsideCircle()) {
				setDefaultValue();
			}
		});
		
		addOnChangeValueListener(() -> {
			recalculateScaleWeight();
		});
		
	}
	
	/**
	 * Constructs with default bounds in center of screen
	 */
	public Knob() {
		this(0,0,0,0);
		
		setSize(getMaxWidth(), getMaxHeight());

		final float x = ctx.width / 2 - getWidth() / 2;
		final float y = ctx.height / 2 - getHeight() / 2;
		
		setPosition(x,y);
	}
	
	/**
	 * @return color of scale
	 */
	public AbstractColor getScaleColor() {
		return scaleColor;
	}

	/**
	 * Setter of scale color
	 * @param scaleColor current color of scale
	 */
	public void setScaleColor(AbstractColor scaleColor) {
		this.scaleColor = requireNonNull(scaleColor,"scaleColor");
	}
	
	/**
	 * Setter of scale color with interpolation
	 * @param startColor current start color
	 * @param endColor current end color
	 */
	public void setScaleColor(AbstractColor startColor, AbstractColor endColor) {
		setScaleColor(new LerpedColor(startColor, endColor, () -> convert(getValue(),getMinValue(), getMaxValue(), 0, 1)));
	}

	/**
	 * @return value of ration by offset for handle
	 */
	public float getHandleOffsetRatio() {
		return handleOffsetRatio;
	}

	/**
	 * Setter for ratio of offset by handle
	 * @param handleOffsetRatio ratio of offset by handle
	 */
	public void setHandleOffsetRatio(float handleOffsetRatio) {
		if (handleOffsetRatio < MIN_HANDLE_OFFSET_RATIO || handleOffsetRatio > MAX_HANDLE_OFFSET_RATIO) {
			throw new IllegalArgumentException(String.format("Handle offset ratio must be between min(%f) and max(%f) value",MIN_HANDLE_OFFSET_RATIO,MAX_HANDLE_OFFSET_RATIO));
		}
		
		this.handleOffsetRatio = handleOffsetRatio;
	}

	/**
	 * 
	 * @return ratio of size by handle
	 */
	public float getHandleSizeRatio() {
		return handleSizeRatio;
	}

	/**
	 * Setter for ratio of size by handle
	 * @param handleSizeRatio current ratio of size by handle
	 */
	public void setHandleSizeRatio(float handleSizeRatio) {
		if (handleSizeRatio < MIN_HANDLE_SIZE_RATIO || handleSizeRatio > MAX_HANDLE_SIZE_RATIO) {
			throw new IllegalArgumentException(String.format("Handle size ratio must be between min(%f) and max(%f) value",MIN_HANDLE_SIZE_RATIO,MAX_HANDLE_SIZE_RATIO));
		}
		
		this.handleSizeRatio = handleSizeRatio;
	}

	/**
	 * @return default value if it's exist, but if it's not initialized, it's will return null
	 */
	public Float getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value for the knob. When double clicked, the knob will reset to this value.
	 *
	 * @param defaultValue the default value, must be within min and max range.
	 * @throws IllegalArgumentException if {@code defaultValue} is out of range.
	 */
	public void setDefaultValue(float defaultValue) {
		if (defaultValue < getMinValue() || defaultValue > getMaxValue()) {
			throw new IllegalArgumentException(String.format("Default value must be between min(%f) and max(%f) value",getMinValue(),getMaxValue()));
		}

		this.defaultValue = Float.valueOf(defaultValue);
	}

	/**
	 * Returns the scale weight ratio, which controls the thickness of the scale arc.
	 *
	 * @return the scale weight ratio.
	 */
	public float getScaleWeightRatio() {
		return scaleWeightRatio;
	}

	/**
	 * Sets the scale weight ratio. The ratio influences the stroke weight of the scale arc.
	 *
	 * @param scaleWeightRatio the ratio, must be between 0 and 2 (inclusive).
	 * @throws IllegalArgumentException if the ratio is out of bounds.
	 */
	public void setScaleWeightRatio(float scaleWeightRatio) {
		if (scaleWeightRatio < MIN_SCALE_WEIGHT_RATIO || scaleWeightRatio > MAX_SCALE_WEIGHT_RATIO) {
			throw new IllegalArgumentException("Scale weight ratio must be between " + MIN_SCALE_WEIGHT_RATIO + " and " + MAX_SCALE_WEIGHT_RATIO);
		}
		this.scaleWeightRatio = scaleWeightRatio;
	}
	
	/**
	 * Returns the color used for the handle.
	 *
	 * @return the handle color.
	 */
	public AbstractColor getHandleColor() {
		return handleColor;
	}

	/**
	 * Sets the color for the handle.
	 *
	 * @param handleColor the new handle color, cannot be null.
	 * @throws NullPointerException if {@code handleColor} is null.
	 */
	public void setHandleColor(AbstractColor handleColor) {
		this.handleColor = requireNonNull(handleColor,"handleColor");
	}

	/**
	 * Returns the start angle of the scale arc, in radians.
	 *
	 * @return the start angle.
	 */
	public float getStartAngle() {
		return startAngle;
	}

	/**
	 * Sets the start angle of the scale arc.
	 *
	 * @param startAngle the start angle in radians, must be between 0 and TWO_PI
	 *                   and not greater than the current end angle.
	 * @throws IllegalArgumentException if the angle is invalid or exceeds the end angle.
	 */
	public void setStartAngle(float startAngle) {
		validateAngle(startAngle);
		
		if (startAngle > endAngle) {
			throw new IllegalArgumentException("Start angle cannot be greater than end angle");
		}
		
		this.startAngle = startAngle;
	}

	/**
	 * Returns the end angle of the scale arc, in radians.
	 *
	 * @return the end angle.
	 */
	public float getEndAngle() {
		return endAngle;
	}

	/**
	 * Sets the end angle of the scale arc.
	 *
	 * @param endAngle the end angle in radians, must be between 0 and TWO_PI
	 *                 and not lower than the current start angle.
	 * @throws IllegalArgumentException if the angle is invalid or below the start angle.
	 */
	public void setEndAngle(float endAngle) {
		validateAngle(endAngle);
		
		if (endAngle < startAngle) {
			throw new IllegalArgumentException("End angle cannot be lower than start angle");
		}
		
		this.endAngle = endAngle;
	}
	
	/**
	 * Sets both start and end angles of the scale arc.
	 *
	 * @param startAngle the start angle in radians, must be between 0 and TWO_PI.
	 * @param endAngle   the end angle in radians, must be between 0 and TWO_PI
	 *                   and greater than startAngle.
	 * @throws IllegalArgumentException if either angle is invalid or if startAngle >= endAngle.
	 */
	public void setAngles(float startAngle, float endAngle) {
		validateAngle(startAngle);
		validateAngle(endAngle);
		
		if (startAngle == endAngle) {
			throw new IllegalArgumentException("Start and end angle cannot be equals");
		}
		
		if (startAngle > endAngle) {
			throw new IllegalArgumentException("Start angle cannot be greater than end angle");
		}
		
		this.startAngle = startAngle;
		this.endAngle = endAngle;
	}
	
	/**
	 * Determines whether the component is ready to receive input.
	 * The knob is considered prepared if the mouse is inside its circular area,
	 * or if it is being dragged, or if internal scrolling is active.
	 *
	 * @return true if the knob should handle input, false otherwise.
	 */
	@Override
	public boolean isContentPrepared() {
		if (Environment.isAndroid() && mouseInsideCircle() && !isPressed()) {
			return false;
		}
		
		if (!PointerManager.isOwner(this) && PointerManager.hasOwner()) {
			return false;
		}
		
		return mouseInsideCircle() || draggableState || getInternalScrolling().isScrolling();
	}

	/**
	 * Handles mouse wheel events. If the mouse is inside the knob, scrolling is initiated.
	 *
	 * @param mouseEvent the mouse wheel event.
	 */
	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		if (mouseInsideCircle()) {
			getInternalScrolling().init(mouseEvent);
		}
	}
	
	@Override
	protected void render() {
		super.render();
		
		if (getShadow() != null) {
			getShadow().setCustomBounds(cachedCenterX, cachedCenterY, cachedSize, cachedSize);
		}
		
		getInternalStroke().apply();
		getBackgroundColor().apply();
		ctx.ellipse(cachedCenterX,cachedCenterY,cachedSize,cachedSize);
		
		scaleOnRender();
		handleOnRender();
		
		appendValue(getInternalScrolling().get());
		
		if (draggableState) {
			manualDragging();
			notifyOnChangeValueListeners();
		}
		
		if (getInternalScrolling().isScrolling()) {
			
			setEndedChangeValue(false);
			
			if (!isStartedChangeValue()) {
				notifyOnStartChangeValueListeners();
				setStartedChangeValue(true);
			}
			
			notifyOnChangeValueListeners();
		} else {
			setStartedChangeValue(false);
			
			if (!isEndedChangeValue()) {
				notifyOnEndChangeValueListeners();
				setEndedChangeValue(true);
			}
		}
	}
	
	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		
		cachedCenterX = getX() + getWidth()/2;
		cachedCenterY = getY() + getHeight()/2;
		
		cachedSize = Math.min(getWidth(),getHeight());
		
		recalculateScaleWeight();
	}
	
	private boolean mouseInsideCircle() {
		return dist(cachedCenterX,cachedCenterY,ctx.mouseX,ctx.mouseY) < cachedSize/2;
	}
	
	private float mapFromValue(float start, float end) {
		return convert(getValue(),getMinValue(), getMaxValue(),start, end);
	}
	
	private void scaleOnRender() {
		ctx.pushMatrix();
		ctx.translate(cachedCenterX,cachedCenterY);
		ctx.rotate(HALF_PI);
		
		ctx.noFill();
		scaleColor.applyStroke();
		
		ctx.strokeWeight(cachedScaleWeight);
		
		ctx.arc(0, 0, cachedSize, cachedSize, startAngle, mapFromValue(startAngle, endAngle));
		ctx.popMatrix();
	}
	
	private void handleOnRender() {
		ctx.pushMatrix();
		ctx.translate(cachedCenterX,cachedCenterY);
		ctx.rotate(HALF_PI + mapFromValue(startAngle, endAngle));
		ctx.noStroke();
		handleColor.apply();
		float size = cachedSize * handleSizeRatio;
		size = draggableState || (isPressed() && mouseInsideCircle()) ? size * .8f : size;
		ctx.ellipse(cachedSize*handleOffsetRatio, 0, size, size);
		
		ctx.popMatrix();
	}
	
	private void setDefaultValue() {
		if (defaultValue == null) {
			return;
		}
		
		setValue(defaultValue);
	}
	
	private void manualDragging() {
		final boolean dragging = ctx.mouseX != ctx.pmouseX || ctx.mouseY != ctx.pmouseY;
		
		if (!dragging) {
			return;
		}
		
		final float dist = dist(cachedCenterX,cachedCenterY,ctx.mouseX,ctx.mouseY);
		
		final float mouseVerticalDist = ctx.pmouseY-ctx.mouseY;
		
		final float rawSpeed = mouseVerticalDist * convert(dist, 0, MAX_DRAGGING_DIST, 1, MANUAL_DRAGGING_SENSITIVITY_FACTOR);

		final boolean mouseDirectionUp = ctx.mouseY < ctx.pmouseY;
		
		final float speed = mouseDirectionUp ? max(MIN_MANUAL_DRAGGING_SPEED,rawSpeed) : min(-MIN_MANUAL_DRAGGING_SPEED,rawSpeed);
		
		appendValue(speed);
	}
	
	private void validateAngle(final float angle) {
		if (angle < 0 || angle > TWO_PI) {
			throw new IllegalArgumentException("Angle must be between 0 and " + TWO_PI);
		}
	}
	
	private void recalculateScaleWeight() {
		cachedScaleWeight = mapFromValue(cachedSize * SCALE_START_WEIGHT, cachedSize * SCALE_END_WEIGHT) * scaleWeightRatio;
	}
}