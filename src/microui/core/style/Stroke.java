package microui.core.style;

import static microui.MicroUI.getContext;
import static processing.core.PConstants.SQUARE;

import microui.core.style.theme.ThemeManager;
import microui.util.Metrics;
import processing.core.PGraphics;

//Status: STABLE - Do not modify
//Last Reviewed: 13.09.2025
public final class Stroke {
	private static final int DEFAULT_STROKE_WEIGHT = 1;
	private AbstractColor color;
	private float weight;

	public Stroke(AbstractColor color, float weight) {
		if (color == null) {
			throw new NullPointerException("color cannot be null");
		}
		Metrics.register(this);
		setColor(color);
		setWeight(weight);
	}

	public Stroke(AbstractColor color) {
		this(color, DEFAULT_STROKE_WEIGHT);
	}

	public Stroke(float weight) {
		this(ThemeManager.getTheme().getStrokeColor(), weight);
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
		if (pGraphics == null) {
			throw new NullPointerException("pGraphics cannot be null");
		}
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
		if (stroke == null) {
			throw new NullPointerException("stroke cannot be null");
		}

		color = stroke.getColor();
		weight = stroke.getWeight();
	}

	public AbstractColor getColor() {
		return color;
	}

	public void setColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("color cannot be null");
		}
		this.color = color;
	}
}