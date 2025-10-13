package microui.core.style;

import static microui.MicroUI.getContext;

import microui.util.Metrics;
import processing.core.PGraphics;

//Status: STABLE - Do not modify
//Last Reviewed: 11.10.2025
public abstract class AbstractColor {
	public static final int MIN_VALUE = 0, MAX_VALUE = 255;

	public AbstractColor() {
		super();

		Metrics.register(this);
	}

	public final void apply() {
		preApply();
		getContext().fill(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void apply(PGraphics pGraphics) {
		if (pGraphics == null) {
			throw new NullPointerException("pGraphics cannot be null");
		}
		preApply();
		pGraphics.fill(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyStroke() {
		preApply();
		getContext().stroke(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyStroke(PGraphics pGraphics) {
		if (pGraphics == null) {
			throw new NullPointerException("pGraphics cannot be null");
		}
		preApply();
		pGraphics.stroke(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyBackground() {
		preApply();
		getContext().background(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyBackground(PGraphics pGraphics) {
		if (pGraphics == null) {
			throw new NullPointerException("pGraphics cannot be null");
		}
		preApply();
		pGraphics.background(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyTint() {
		preApply();
		getContext().tint(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyTint(PGraphics pGraphics) {
		if (pGraphics == null) {
			throw new NullPointerException("pGraphics cannot be null");
		}
		preApply();
		pGraphics.tint(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public abstract int getRed();

	public abstract int getGreen();

	public abstract int getBlue();

	public abstract int getAlpha();

	protected void preApply() {

	}

	protected static int constrain(float value, float min, float max) {
		return (int) (value < min ? min : value > max ? max : value);
	}
}