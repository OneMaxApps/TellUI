package microui.component;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PApplet.dist;

import microui.core.RangeControl;
import microui.core.style.AbstractColor;
import microui.util.MathUtils;
import processing.event.MouseEvent;

public final class Knob extends RangeControl {
	private static final float START = 0, END = (float) (PI * 2);
	private AbstractColor indicatorColor;
	private float centerX, centerY, diameter;
	private boolean isCanDrag;

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

	public Knob() {
		this(0, 0, 0, 0);
		setSize(getMaxWidth(), getMaxHeight());
		setPosition(ctx.width / 2 - getMaxWidth() / 2, ctx.height / 2 - getMaxHeight() / 2);
	}

	@Override
	protected void render() {
		getMutableStroke().apply();
		getBackgroundColor().apply();
		ctx.ellipse(centerX, centerY, diameter, diameter);

		indicatorOnDraw();

		if (!ctx.mousePressed) {
			isCanDrag = false;
		}

		if (isCanDrag) {
			getMutableValue().append(ctx.pmouseY - ctx.mouseY);
		}

		getMutableValue().append(getMutableScrolling().get());
	}

	@Override
	protected void onChangeBounds() {
		super.onChangeBounds();
		recalculateCenter();
		recalculateDiameter();
	}

	@Override
	public void mouseWheel(MouseEvent mouseEvent) {
		if (isMouseInDiameter()) {
			getMutableScrolling().init(mouseEvent);
		}
	}

	public AbstractColor getIndicatorColor() {
		return indicatorColor;
	}

	public void setIndicatorColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("the color cannot be null");
		}
		indicatorColor = color;
	}

	private void recalculateCenter() {
		centerX = getX() + getWidth() / 2;
		centerY = getY() + getHeight() / 2;
	}

	private void recalculateDiameter() {
		diameter = min(getWidth(), getHeight());
	}

	private boolean isMouseInDiameter() {
		return dist(centerX, centerY, ctx.mouseX, ctx.mouseY) < diameter / 2;
	}

	private void indicatorOnDraw() {
		ctx.push();

		ctx.translate(centerX, centerY);
		ctx.rotate((float) PI / 2);
		ctx.noFill();
		ctx.strokeWeight(getIndicatorWeight());

		indicatorColor.apply();
		ctx.arc(0, 0, diameter * .8f, diameter * .8f, START, END);

		indicatorColor.applyStroke();
		ctx.arc(0, 0, diameter * .8f, diameter * .8f, START, MathUtils.convert(getMutableValue().get(),
				getMutableValue().getMin(), getMutableValue().getMax(), START, END));

		if (getMutableValue().get() == getMutableValue().getMax()) {
			ctx.ellipse(0, 0, diameter / 4, diameter / 4);
		}

		ctx.pop();
	}

	private float getIndicatorWeight() {
		return diameter * .1f + abs(getMutableScrolling().get() * 2);
	}
}