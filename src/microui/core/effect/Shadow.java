package microui.core.effect;

import static microui.util.MathUtils.convert;
import static processing.core.PConstants.PROJECT;

import microui.core.base.ContentView;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.core.style.Color;

public final class Shadow extends View {
	public static final int MIN_WEIGHT = 0;
	public static final int MAX_WEIGHT = 10;
	public static final int DEFAULT_WEIGHT_LEFT = 2;
	public static final int DEFAULT_WEIGHT_TOP = 1;
	public static final int DEFAULT_WEIGHT_RIGHT = 1;
	public static final int DEFAULT_WEIGHT_BOTTOM = 4;
	private final ContentView targetView;
	private AbstractColor color;
	private int weightLeft, weightTop, weightRight, weightBottom;
	private boolean isAlphaFadeOutEnabled;
	
	public Shadow(ContentView targetView) {
		super();
		setVisible(true);

		if (targetView == null) {
			throw new NullPointerException("ContentView for Shadow cannot be null");
		}

		this.targetView = targetView;

		setColor(new Color(0, 32));
		setWeight(DEFAULT_WEIGHT_LEFT, DEFAULT_WEIGHT_TOP, DEFAULT_WEIGHT_RIGHT, DEFAULT_WEIGHT_BOTTOM);
	}
	
	public boolean isAlphaFadeOutEnabled() {
		return isAlphaFadeOutEnabled;
	}

	public void setAlphaFadeOutEnabled(boolean isAlphaFadeOutEnabled) {
		this.isAlphaFadeOutEnabled = isAlphaFadeOutEnabled;
	}

	public AbstractColor getColor() {
		return color;
	}

	public void setColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("Color for Shadow cannot be null");
		}
		this.color = color;
	}

	public int getWeightLeft() {
		return weightLeft;
	}

	public void setWeightLeft(int weightLeft) {
		checkWeight(weightLeft);

		this.weightLeft = weightLeft;
	}

	public int getWeightTop() {
		return weightTop;
	}

	public void setWeightTop(int weightTop) {
		checkWeight(weightTop);

		this.weightTop = weightTop;
	}

	public int getWeightRight() {
		return weightRight;
	}

	public void setWeightRight(int weightRight) {
		checkWeight(weightRight);
		this.weightRight = weightRight;
	}

	public int getWeightBottom() {
		return weightBottom;
	}

	public void setWeightBottom(int weightBottom) {
		checkWeight(weightBottom);
		this.weightBottom = weightBottom;
	}

	public void setWeight(int weight) {
		checkWeight(weight);
		weightLeft = weightTop = weightRight = weightBottom = weight;
	}
	
	public boolean hasLeftWeight() {
		return weightLeft != 0;
	}
	
	public boolean hasTopWeight() {
		return weightTop != 0;
	}
	
	public boolean hasRightWeight() {
		return weightRight != 0;
	}
	
	public boolean hasBottomWeight() {
		return weightBottom != 0;
	}
	
	public boolean hasWeight() {
		return hasLeftWeight() || hasTopWeight() || hasRightWeight() || hasBottomWeight(); 
	}

	public void setWeight(int left, int top, int right, int bottom) {
		checkWeight(left);
		checkWeight(top);
		checkWeight(right);
		checkWeight(bottom);

		weightLeft = left;
		weightTop = top;
		weightRight = right;
		weightBottom = bottom;
	}

	@Override
	protected void render() {
		if(!hasWeight()) {
			return;
		}
		
		ctx.noFill();
		ctx.strokeCap(PROJECT);

		for (int i = 0; i < MAX_WEIGHT; i++) {
			
			if(isAlphaFadeOutEnabled()) {
				ctx.stroke(color.getRed(), color.getGreen(), color.getBlue(),convert(i, 0, MAX_WEIGHT, color.getAlpha(), 0));
			} else {
				color.applyStroke();
			}
			
			float x = 0;
			float y = 0;
			float x1 = 0;
			float y1 = 0;
			float shift = 0;

			if(hasLeftWeight()) {
				// Left side
				{
					x = targetView.getPadX();
					y = targetView.getPadY();
					x1 = x;
					y1 = y + targetView.getPadHeight();
	
					shift = convert(i, 0, MAX_WEIGHT, 0, weightLeft);
					ctx.line(x - shift, y, x1 - shift, y1);
				}
			}

			if(hasTopWeight()) {
				// Top side
				{
					x = targetView.getPadX();
					y = targetView.getPadY();
					x1 = x + targetView.getPadWidth();
					y1 = y;
	
					shift = convert(i, 0, MAX_WEIGHT, 0, weightTop);
	
					ctx.line(x, y - shift, x1, y1 - shift);
				}
			}
			
			if(hasRightWeight()) {
				// Right side
				{
					x = targetView.getPadX() + targetView.getPadWidth();
					y = targetView.getPadY();
					x1 = x;
					y1 = y + targetView.getPadHeight();
	
					shift = convert(i, 0, MAX_WEIGHT, 0, weightRight);
	
					ctx.line(x + shift, y, x1 + shift, y1);
				}
			}
			
			if(hasTopWeight()) {
				// Bottom side
				{
					x = targetView.getPadX();
					y = targetView.getPadY() + targetView.getPadHeight();
					x1 = x + targetView.getPadWidth();
					y1 = y;
	
					shift = convert(i, 0, MAX_WEIGHT, 0, weightBottom);
	
					ctx.line(x, y + shift, x1, y1 + shift);
				}			
			}
			
		}

	}

	private static void checkWeight(int weight) {
		if(weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
			throw new IllegalArgumentException("Weight must be between " + MIN_WEIGHT + " and " + MAX_WEIGHT);
		}
	}
	
}