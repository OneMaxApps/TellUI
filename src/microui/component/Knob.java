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

/**
 * A graphical knob component that allows users to select a value from a range
 * by rotating a handle along a circular scale.
 * <p>
 * The knob supports mouse/touch dragging, mouse wheel scrolling, and
 * double-click to reset to a default value. Appearance properties such as
 * colors, handle size/offset, scale thickness, and start/end angles are
 * fully customizable.
 * </p>
 *
 * @see RangeControl
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
	 * Constructs a Knob with the specified bounds.
	 *
	 * @param x      the x-coordinate of the knob
	 * @param y      the y-coordinate of the knob
	 * @param width  the width of the knob
	 * @param height the height of the knob
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
	 * Constructs a Knob with default size, centered on the screen.
	 */
	public Knob() {
		this(0,0,0,0);
		
		setSize(getMaxWidth(), getMaxHeight());

		final float x = ctx.width / 2 - getWidth() / 2;
		final float y = ctx.height / 2 - getHeight() / 2;
		
		setPosition(x,y);
	}
	
	/**
	 * Returns the color used for the scale arc.
	 *
	 * @return the scale color
	 */
	public AbstractColor getScaleColor() {
		return scaleColor;
	}

	/**
	 * Sets the color of the scale arc.
	 *
	 * @param scaleColor the new scale color (must not be null)
	 * @throws NullPointerException if {@code scaleColor} is null
	 */
	public void setScaleColor(AbstractColor scaleColor) {
		this.scaleColor = requireNonNull(scaleColor,"scaleColor");
	}
	
	/**
	 * Sets the scale color using an interpolated gradient between two colors.
	 * The interpolation factor is derived from the current value's position
	 * within the min/max range.
	 *
	 * @param startColor the color used at the minimum value
	 * @param endColor   the color used at the maximum value
	 */
	public void setScaleColor(AbstractColor startColor, AbstractColor endColor) {
		setScaleColor(new LerpedColor(startColor, endColor, () -> convert(getValue(),getMinValue(), getMaxValue(), 0, 1)));
	}

	/**
	 * Returns the ratio that determines how far from the center the handle is drawn.
	 * A value of {@value #MIN_HANDLE_OFFSET_RATIO} places the handle at the center;
	 * {@value #MAX_HANDLE_OFFSET_RATIO} places it near the edge.
	 *
	 * @return the handle offset ratio (between {@value #MIN_HANDLE_OFFSET_RATIO} and {@value #MAX_HANDLE_OFFSET_RATIO})
	 */
	public float getHandleOffsetRatio() {
		return handleOffsetRatio;
	}

	/**
	 * Sets the handle offset ratio. Controls the distance of the handle from the
	 * knob's center.
	 *
	 * @param handleOffsetRatio the new ratio (must be between {@value #MIN_HANDLE_OFFSET_RATIO} and {@value #MAX_HANDLE_OFFSET_RATIO})
	 * @throws IllegalArgumentException if the value is out of allowed bounds
	 */
	public void setHandleOffsetRatio(float handleOffsetRatio) {
		if (handleOffsetRatio < MIN_HANDLE_OFFSET_RATIO || handleOffsetRatio > MAX_HANDLE_OFFSET_RATIO) {
			throw new IllegalArgumentException(String.format("Handle offset ratio must be between min(%f) and max(%f) value",MIN_HANDLE_OFFSET_RATIO,MAX_HANDLE_OFFSET_RATIO));
		}
		
		this.handleOffsetRatio = handleOffsetRatio;
	}

	/**
	 * Returns the ratio of the handle size relative to the knob's diameter.
	 *
	 * @return the handle size ratio (between {@value #MIN_HANDLE_SIZE_RATIO} and {@value #MAX_HANDLE_SIZE_RATIO})
	 */
	public float getHandleSizeRatio() {
		return handleSizeRatio;
	}

	/**
	 * Sets the handle size ratio. Determines how large the handle circle is
	 * compared to the overall knob size.
	 *
	 * @param handleSizeRatio the new ratio (must be between {@value #MIN_HANDLE_SIZE_RATIO} and {@value #MAX_HANDLE_SIZE_RATIO})
	 * @throws IllegalArgumentException if the value is out of allowed bounds
	 */
	public void setHandleSizeRatio(float handleSizeRatio) {
		if (handleSizeRatio < MIN_HANDLE_SIZE_RATIO || handleSizeRatio > MAX_HANDLE_SIZE_RATIO) {
			throw new IllegalArgumentException(String.format("Handle size ratio must be between min(%f) and max(%f) value",MIN_HANDLE_SIZE_RATIO,MAX_HANDLE_SIZE_RATIO));
		}
		
		this.handleSizeRatio = handleSizeRatio;
	}

	/**
	 * Returns the default value to which the knob resets on double-click.
	 *
	 * @return the default value, or {@code null} if not set
	 */
	public Float getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value for the knob. When the knob is double-clicked,
	 * the current value is reset to this default.
	 *
	 * @param defaultValue the default value (must be within the current min/max range)
	 * @throws IllegalArgumentException if {@code defaultValue} is outside the valid range
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
	 * @return the scale weight ratio (between {@value #MIN_SCALE_WEIGHT_RATIO} and {@value #MAX_SCALE_WEIGHT_RATIO})
	 */
	public float getScaleWeightRatio() {
		return scaleWeightRatio;
	}

	/**
	 * Sets the scale weight ratio. Higher values produce a thicker scale arc.
	 *
	 * @param scaleWeightRatio the new ratio (must be between {@value #MIN_SCALE_WEIGHT_RATIO} and {@value #MAX_SCALE_WEIGHT_RATIO})
	 * @throws IllegalArgumentException if the ratio is out of bounds
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
	 * @return the handle color
	 */
	public AbstractColor getHandleColor() {
		return handleColor;
	}

	/**
	 * Sets the color of the handle.
	 *
	 * @param handleColor the new handle color (must not be null)
	 * @throws NullPointerException if {@code handleColor} is null
	 */
	public void setHandleColor(AbstractColor handleColor) {
		this.handleColor = requireNonNull(handleColor,"handleColor");
	}

	/**
	 * Returns the start angle of the scale arc, in radians.
	 *
	 * @return the start angle (0 … 2π)
	 */
	public float getStartAngle() {
		return startAngle;
	}

	/**
	 * Sets the start angle of the scale arc.
	 *
	 * @param startAngle the start angle in radians (must be between 0 and 2π,
	 *                   and not greater than the current end angle)
	 * @throws IllegalArgumentException if the angle is invalid or exceeds the end angle
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
	 * @return the end angle (0 … 2π)
	 */
	public float getEndAngle() {
		return endAngle;
	}

	/**
	 * Sets the end angle of the scale arc.
	 *
	 * @param endAngle the end angle in radians (must be between 0 and 2π,
	 *                 and not lower than the current start angle)
	 * @throws IllegalArgumentException if the angle is invalid or below the start angle
	 */
	public void setEndAngle(float endAngle) {
		validateAngle(endAngle);
		
		if (endAngle < startAngle) {
			throw new IllegalArgumentException("End angle cannot be lower than start angle");
		}
		
		this.endAngle = endAngle;
	}
	
	/**
	 * Convenience method to set both start and end angles simultaneously.
	 *
	 * @param startAngle the start angle in radians (0 … 2π)
	 * @param endAngle   the end angle in radians (0 … 2π, must be greater than startAngle)
	 * @throws IllegalArgumentException if either angle is invalid or if startAngle >= endAngle
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
	 * Determines whether the knob is ready to receive input.
	 * The knob is considered prepared if the mouse is inside its circular area,
	 * or if it is being dragged, or if internal scrolling is active.
	 * <p>
	 * On Android, the knob does not respond to a simple press without movement;
	 * the mouse must be inside the circle and the component must be pressed.
	 * If another component already owns the pointer, this knob will not react.
	 * </p>
	 *
	 * @return {@code true} if the knob should handle input, {@code false} otherwise
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
	 * @param mouseEvent the mouse wheel event
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