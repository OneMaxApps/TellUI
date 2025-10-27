package microui.core.style;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;
import static microui.core.style.theme.ThemeManager.getTheme;
import static processing.core.PConstants.SQUARE;

import microui.util.Metrics;
import processing.core.PGraphics;

//Status: STABLE - Do not modify
//Last Reviewed: 27.10.2025
public final class Stroke {
	private static final int DEFAULT_STROKE_WEIGHT = 1;
	private AbstractColor color;
	private float weight;

	public Stroke(AbstractColor color, float weight) {
		setColor(color);
		setWeight(weight);
		Metrics.register(this);
	}

	public Stroke(AbstractColor color) {
		this(color, DEFAULT_STROKE_WEIGHT);
	}

	public Stroke(float weight) {
		this(getTheme().getStrokeColor(), weight);
	}

	public Stroke() {
		this(DEFAULT_STROKE_WEIGHT);
	}

	public void apply() {
		getContext().strokeCap(SQUARE);
		color.applyStroke();
		getContext().strokeWeight(weight);
	}

	public void apply(PGraphics pGraphics) {
		requireNonNull(pGraphics,"pGraphics");
		
		pGraphics.strokeCap(SQUARE);
		color.applyStroke(pGraphics);
		pGraphics.strokeWeight(weight);
	}

	public void setWeight(float weight) {
		if (weight <= 0) {
			throw new IllegalArgumentException("stroke weight cannot be lower than 1");
		}
		this.weight = weight;
	}

	public float getWeight() {
		return weight;
	}

	public void set(Stroke stroke) {
		requireNonNull(stroke,"stroke");

		color = stroke.getColor();
		weight = stroke.getWeight();
	}

	public AbstractColor getColor() {
		return color;
	}

	public void setColor(AbstractColor color) {
		this.color = requireNonNull(color,"color");
	}
}