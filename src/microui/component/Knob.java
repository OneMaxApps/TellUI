package microui.component;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.convert;
import static processing.core.PApplet.dist;
import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.TWO_PI;

import microui.core.RangeControl;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import processing.event.MouseEvent;

public final class Knob extends RangeControl {
	private static final float DEFAULT_START_ANGLE = HALF_PI/2;
	private static final float DEFAULT_END_ANGLE = TWO_PI - HALF_PI / 2;
	private static final float DEFAULT_SCALE_WEIGHT_RATIO = 1f;
	private static final float MIN_SCALE_WEIGHT_RATIO = 0f;
	private static final float MAX_SCALE_WEIGHT_RATIO = 2f;
	
	private AbstractColor scaleStartColor,scaleEndColor;
	private float scaleWeightRatio;
	
	private float startAngle, endAngle;
	private float cachedCenterX, cachedCenterY, cachedSize;
	private boolean mustDragging;
	
	public Knob(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		startAngle = DEFAULT_START_ANGLE;
		endAngle = DEFAULT_END_ANGLE;
		
		setScaleStartColor(new Color(0,255,0,200));
		setScaleEndColor(new Color(255,128,0,200));
		setScaleWeightRatio(DEFAULT_SCALE_WEIGHT_RATIO);
		
		onDragStart(() -> {
			mustDragging = mouseInsideCircle();
		});
		
		onRelease(() -> {
			mustDragging = false;
		});
	}
	
	public Knob() {
		this(0,0,0,0);
		
		final float x = ctx.width / 2 - getWidth() / 2;
		final float y = ctx.height / 2 - getHeight() / 2;
		
		setPosition(x,y);
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

	public AbstractColor getScaleStartColor() {
		return scaleStartColor;
	}

	public void setScaleStartColor(AbstractColor scaleStartColor) {
		this.scaleStartColor = requireNonNull(scaleStartColor,"scaleStartColor");
	}

	public AbstractColor getScaleEndColor() {
		return scaleEndColor;
	}

	public void setScaleEndColor(AbstractColor scaleEndColor) {
		this.scaleEndColor = requireNonNull(scaleEndColor,"scaleEndColor");
	}
	
	public void setScaleColor(AbstractColor solidColor) {
		setScaleStartColor(solidColor);
		setScaleEndColor(solidColor);
	}
	
	public void setScaleColor(AbstractColor scaleStartColor, AbstractColor scaleEndColor) {
		setScaleStartColor(scaleStartColor);
		setScaleEndColor(scaleEndColor);
	}

	public float getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(float startAngle) {
		if (startAngle < 0 || startAngle > TWO_PI) {
			throw new IllegalArgumentException("Start angle must be between 0 and " + TWO_PI);
		}
		
		if (startAngle > endAngle) {
			throw new IllegalArgumentException("Start angle cannot be greater than end angle");
		}
		
		this.startAngle = startAngle;
	}

	public float getEndAngle() {
		return endAngle;
	}

	public void setEndAngle(float endAngle) {
		if (endAngle < 0 || endAngle > TWO_PI) {
			throw new IllegalArgumentException("End angle must be between 0 and " + TWO_PI);
		}
		
		if (endAngle < startAngle) {
			throw new IllegalArgumentException("End angle cannot be lower than start angle");
		}
		
		this.endAngle = endAngle;
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		if (mouseInsideCircle()) {
			getInternalScrolling().init(mouseEvent);
		}
	}
	
	@Override
	protected void render() {
		getInternalStroke().apply();
		getBackgroundColor().apply();
		
		ctx.ellipse(cachedCenterX,cachedCenterY,cachedSize,cachedSize);
		
		ctx.pushMatrix();
		ctx.translate(cachedCenterX,cachedCenterY);
		ctx.rotate(HALF_PI);
		
		ctx.stroke(mapFromValue(scaleStartColor.getRed(),scaleEndColor.getRed())
				  ,mapFromValue(scaleStartColor.getGreen(),scaleEndColor.getGreen())
				  ,mapFromValue(scaleStartColor.getBlue(),scaleEndColor.getBlue())
				  ,mapFromValue(scaleStartColor.getAlpha(),scaleEndColor.getAlpha()));
		
		final float sw = mapFromValue(cachedSize * .05f, cachedSize * .1f) * scaleWeightRatio;
		ctx.strokeWeight(sw);
		
		ctx.arc(0, 0, cachedSize,cachedSize, startAngle, convert(getValue(),getMinValue(), getMaxValue(), startAngle, endAngle));
		ctx.popMatrix();
		
		getInternalValue().append(getInternalScrolling().get());
		
		if (mustDragging) {
			appendValue(ctx.pmouseY-ctx.mouseY);
		}
	}

	@Override
	protected void onChangePosition() {
		super.onChangePosition();
		
		cachedCenterX = getX() + getWidth()/2;
		cachedCenterY = getY() + getHeight()/2;
	}

	@Override
	protected void onChangeDimensions() {
		super.onChangeDimensions();
		
		cachedSize = Math.min(getWidth(),getHeight());
	}

	private boolean mouseInsideCircle() {
		return dist(cachedCenterX,cachedCenterY,ctx.mouseX,ctx.mouseY) < cachedSize/2;
	}
	
	private float mapFromValue(float start, float end) {
		return convert(getValue(),getMinValue(), getMaxValue(),start, end);
	}
}
