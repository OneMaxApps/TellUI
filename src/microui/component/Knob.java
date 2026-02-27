package microui.component;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.convert;
import static processing.core.PApplet.dist;
import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.TWO_PI;

import java.util.Optional;

import microui.core.RangeControl;
import microui.core.base.ContainerManager;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.core.style.LerpedColor;
import microui.util.Environment;
import processing.event.MouseEvent;

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
	private float handleOffsetRatio, handleSizeRatio;
	private boolean draggableState;
	
	private Optional<Float> defaultValue;
	
	public Knob(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		defaultValue = Optional.empty();
		
		setAngles(DEFAULT_START_ANGLE, DEFAULT_END_ANGLE);

		setScaleColor(new Color(0,255,0,200), new Color(255,128,0,200));
		
		setHandleColor(new Color(128,128));
		
		setHandleSizeRatio(DEFAULT_HANDLE_SIZE_RATIO);
		setHandleOffsetRatio(DEFAULT_HANDLE_OFFSET_RATIO);
		
		setScaleWeightRatio(DEFAULT_SCALE_WEIGHT_RATIO);
		
		onDragStart(() -> {
			draggableState = mouseInsideCircle();
		});
		
		onRelease(() -> {
			draggableState = false;
		});
		
		onDoubleClick(() -> {
			if (mouseInsideCircle()) {
				setDefaultValue();
			}
		});
		
	}
	
	public Knob() {
		this(0,0,0,0);
		
		final float x = ctx.width / 2 - getWidth() / 2;
		final float y = ctx.height / 2 - getHeight() / 2;
		
		setPosition(x,y);
	}		

	public AbstractColor getScaleColor() {
		return scaleColor;
	}

	public void setScaleColor(AbstractColor scaleColor) {
		this.scaleColor = requireNonNull(scaleColor,"scaleColor");
	}
	
	public void setScaleColor(AbstractColor startColor, AbstractColor endColor) {
		setScaleColor(new LerpedColor(startColor, endColor, () -> convert(getValue(),getMinValue(), getMaxValue(), 0, 1)));
	}

	public float getHandleOffsetRatio() {
		return handleOffsetRatio;
	}

	public void setHandleOffsetRatio(float handleOffsetRatio) {
		if (handleOffsetRatio < MIN_HANDLE_OFFSET_RATIO || handleOffsetRatio > MAX_HANDLE_OFFSET_RATIO) {
			throw new IllegalArgumentException(String.format("Handle offset ratio must be between min(%f) and max(%f) value",MIN_HANDLE_OFFSET_RATIO,MAX_HANDLE_OFFSET_RATIO));
		}
		
		this.handleOffsetRatio = handleOffsetRatio;
	}

	public float getHandleSizeRatio() {
		return handleSizeRatio;
	}

	public void setHandleSizeRatio(float handleSizeRatio) {
		if (handleSizeRatio < MIN_HANDLE_SIZE_RATIO || handleSizeRatio > MAX_HANDLE_SIZE_RATIO) {
			throw new IllegalArgumentException(String.format("Handle size ratio must be between min(%f) and max(%f) value",MIN_HANDLE_SIZE_RATIO,MAX_HANDLE_SIZE_RATIO));
		}
		
		this.handleSizeRatio = handleSizeRatio;
	}

	public Optional<Float> getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(float defaultValue) {
		if (defaultValue < getMinValue() || defaultValue > getMaxValue()) {
			throw new IllegalArgumentException(String.format("Default value must be between min(%f) and max(%f) value",getMinValue(),getMaxValue()));
		}

		this.defaultValue = Optional.of(defaultValue);
	}

	public float getScaleWeightRatio() {
		return scaleWeightRatio;
	}

	public void setScaleWeightRatio(float scaleWeightRatio) {
		if (scaleWeightRatio < MIN_SCALE_WEIGHT_RATIO || scaleWeightRatio > MAX_SCALE_WEIGHT_RATIO) {
			throw new IllegalArgumentException("Scale weight ratio must be between " + MIN_SCALE_WEIGHT_RATIO + " and " + MAX_SCALE_WEIGHT_RATIO);
		}
		this.scaleWeightRatio = scaleWeightRatio;
	}
	
	public AbstractColor getHandleColor() {
		return handleColor;
	}

	public void setHandleColor(AbstractColor handleColor) {
		this.handleColor = requireNonNull(handleColor,"handleColor");
	}

	public float getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(float startAngle) {
		validateAngle(startAngle);
		
		if (startAngle > endAngle) {
			throw new IllegalArgumentException("Start angle cannot be greater than end angle");
		}
		
		this.startAngle = startAngle;
	}

	public float getEndAngle() {
		return endAngle;
	}

	public void setEndAngle(float endAngle) {
		validateAngle(endAngle);
		
		if (endAngle < startAngle) {
			throw new IllegalArgumentException("End angle cannot be lower than start angle");
		}
		
		this.endAngle = endAngle;
	}
	
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
	
	@Override
	public boolean isContentPrepared() {
		if (Environment.isAndroid() && mouseInsideCircle() && !isPressed()) {
			return false;
		}
		
		final var cm = ContainerManager.getInstance();
		
		if (!cm.isDragOwner(this) && cm.isDraggingState()) {
			return false;
		}
		
		return mouseInsideCircle() || draggableState || getInternalScrolling().isScrolling();
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		if (mouseInsideCircle()) {
			getInternalScrolling().init(mouseEvent);
		}
	}
	
	@Override
	protected void render() {
		super.render();
		
		getInternalStroke().apply();
		getBackgroundColor().apply();
		ctx.ellipse(cachedCenterX,cachedCenterY,cachedSize,cachedSize);
		
		scaleOnRender();
		handleOnRender();
		
		appendValue(getInternalScrolling().get());
		
		final var cm = ContainerManager.getInstance();
		
		if (draggableState) {
			if (cm.requestDrag(this)) {
				manualDragging();
			}
		}
	}
	
	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		
		cachedCenterX = getX() + getWidth()/2;
		cachedCenterY = getY() + getHeight()/2;
		
		cachedSize = Math.min(getWidth(),getHeight());
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
		
		scaleColor.applyStroke();
		
		final float sw = mapFromValue(cachedSize * SCALE_START_WEIGHT, cachedSize * SCALE_END_WEIGHT) * scaleWeightRatio;
		ctx.strokeWeight(sw);
		
		ctx.arc(0, 0, cachedSize,cachedSize, startAngle, mapFromValue(startAngle, endAngle));
		ctx.popMatrix();
	}
	
	private void handleOnRender() {
		ctx.pushMatrix();
		ctx.translate(cachedCenterX,cachedCenterY);
		ctx.rotate(HALF_PI + mapFromValue(startAngle, endAngle));
		ctx.noStroke();
		handleColor.apply();
		float size = cachedSize * handleSizeRatio;
		size = draggableState || isPressed() && mouseInsideCircle() ? size * .8f : size;
		ctx.ellipse(cachedSize*handleOffsetRatio, 0, size, size);
		
		ctx.popMatrix();
	}
	
	private void setDefaultValue() {
		if (defaultValue.isEmpty()) {
			return;
		}
		
		setValue(defaultValue.get());
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
	
}
