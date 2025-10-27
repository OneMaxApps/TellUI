package microui.core.style;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;

import microui.util.Metrics;
import processing.core.PGraphics;

//Status: STABLE - Do not modify
//Last Reviewed: 27.10.2025
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
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.fill(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyStroke() {
		preApply();
		getContext().stroke(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyStroke(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.stroke(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyBackground() {
		preApply();
		getContext().background(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyBackground(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.background(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyTint() {
		preApply();
		getContext().tint(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public final void applyTint(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.tint(getRed(), getGreen(), getBlue(), getAlpha());
	}

	public abstract int getRed();

	public abstract int getGreen();

	public abstract int getBlue();

	public abstract int getAlpha();

	protected void preApply() {

	}
	
}