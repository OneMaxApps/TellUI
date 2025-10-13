package microui.core.effect;

import static java.util.Objects.requireNonNull;
import static processing.core.PConstants.CORNERS;
import static processing.core.PConstants.SQUARE;

import microui.core.base.SpatialView;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import microui.util.MathUtils;

@Deprecated
public final class Shadow extends View {
	private final AbstractColor color;
	private final SpatialView bounds;
	private int leftSize, rightSize, upSize, downSize;
	private byte absoluteSize;

	public Shadow(SpatialView bounds) {
		super();
		this.bounds = requireNonNull(bounds, "bounds cannot be null");
		color = Color.GRAY_32L;
		leftSize = 10;
		upSize = 10;
		rightSize = 10;
		downSize = 20;
		absoluteSize = 10;
		setVisible(true);
	}

	@Override
	public void render() {
		if (bounds.isVisible()) {
			ctx.pushStyle();

			for (int i = 0; i < absoluteSize; i++) {
				ctx.strokeWeight(2);
				ctx.strokeCap(SQUARE);
				color.applyStroke();
				ctx.noFill();
				ctx.rectMode(CORNERS);
				if (bounds instanceof SpatialView) {
					ctx.rect(bounds.getX() - MathUtils.convert(i, 0f, absoluteSize, 0f, leftSize),
							bounds.getY() - MathUtils.convert(i, 0f, absoluteSize, 0f, upSize),
							bounds.getX() + bounds.getWidth() + MathUtils.convert(i, 0f, absoluteSize, 0f, rightSize),
							bounds.getY() + bounds.getHeight() + MathUtils.convert(i, 0f, absoluteSize, 0f, downSize),
							leftSize, upSize, rightSize, downSize);
				}

			}
			ctx.popStyle();
		}
	}

	public final AbstractColor getColor() {
		return color;
	}

	public void set(float left, float up, float right, float down) {
		leftSize = (int) MathUtils.constrain(left, 0, 20);
		upSize = (int) MathUtils.constrain(up, 0, 20);
		rightSize = (int) MathUtils.constrain(right, 0, 20);
		downSize = (int) MathUtils.constrain(down, 0, 20);
	}

	public void set(float angles) {
		if (angles < 0) {
			throw new IllegalArgumentException("angles cannot be less than zero");
		}
		leftSize = upSize = rightSize = downSize = (int) MathUtils.constrain(angles, 0, 20);
	}

	public int[] get() {
		return new int[] { leftSize, upSize, rightSize, downSize };
	}

}