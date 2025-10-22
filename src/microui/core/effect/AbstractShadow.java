package microui.core.effect;

import microui.core.base.ContentView;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.core.style.Color;

public abstract class AbstractShadow extends View {
	public static final int DEFAULT_WEIGHT_LEFT = 0;
	public static final int DEFAULT_WEIGHT_TOP = 0;
	public static final int DEFAULT_WEIGHT_RIGHT = 3;
	public static final int DEFAULT_WEIGHT_BOTTOM = 2;
	public static final int MIN_WEIGHT = 0;
	public static final int MAX_WEIGHT = 10;
	
	private AbstractColor color;
	private ContentView target;
	
	private int weightLeft,weightTop,weightRight,weightBottom;
	
	public AbstractShadow() {
		super();
		setVisible(true);
		setColor(new Color(0,10));
	}

	public ContentView getTarget() {
		return target;
	}

	public AbstractShadow setTarget(ContentView target) {
		if(target == null) {
			throw new NullPointerException("Target for Shadow cannot be null");
		}
		
		this.target = target;
		
		return this;
	}
	
	public AbstractColor getColor() {
		return color;
	}

	public AbstractShadow setColor(AbstractColor color) {
		if(color == null) {
			throw new NullPointerException("Color for Shadow cannot be null");
		}
		
		this.color = color;
		
		return this;
	}
	
	public int getWeightLeft() {
		return weightLeft;
	}

	public AbstractShadow setWeightLeft(int weightLeft) {
		checkWeight(weightLeft);
		this.weightLeft = weightLeft;
		
		return this;
	}

	public int getWeightTop() {
		return weightTop;
	}

	public AbstractShadow setWeightTop(int weightTop) {
		checkWeight(weightTop);
		this.weightTop = weightTop;
		return this;
	}

	public int getWeightRight() {
		return weightRight;
	}

	public AbstractShadow setWeightRight(int weightRight) {
		checkWeight(weightRight);
		this.weightRight = weightRight;
		return this;
	}

	public int getWeightBottom() {
		return weightBottom;
	}

	public AbstractShadow setWeightBottom(int weightBottom) {
		checkWeight(weightBottom);
		this.weightBottom = weightBottom;
		return this;
	}
	
	public AbstractShadow clearAllWeight() {
		weightLeft = weightTop = weightRight = weightBottom = 0;
		return this;
	}
	
	public AbstractShadow setWeight(int weight) {
		checkWeight(weight);
		weightLeft = weightTop = weightRight = weightBottom = weight;
		return this;
	}
	
	public AbstractShadow setWeight(int weightHorizontal, int weightVertical) {
		checkWeight(weightHorizontal);
		checkWeight(weightVertical);
		
		weightLeft = weightRight = weightHorizontal;
		weightTop = weightBottom = weightVertical;
		return this;
	}
	
	public AbstractShadow setWeight(int left, int top, int right, int bottom) {
		checkWeight(left);
		checkWeight(top);
		checkWeight(right);
		checkWeight(bottom);
		
		weightLeft = left;
		weightTop = top;
		weightRight = right;
		weightBottom = bottom;
		return this;
	}
	
	public boolean hasWeightLeft() {
		return weightLeft != 0;
	}
	
	public boolean hasWeightTop() {
		return weightTop != 0;
	}
	
	public boolean hasWeightRight() {
		return weightRight != 0;
	}
	
	public boolean hasWeightBottom() {
		return weightBottom != 0;
	}
	
	public boolean hasWeight() {
		return hasWeightLeft() || hasWeightTop() || hasWeightRight() || hasWeightBottom(); 
	}

	protected float getTargetX() {
		if(target == null) {
			return 0;
		}
		
		return target.getPadX();
	}
	
	protected float getTargetY() {
		if(target == null) {
			return 0;
		}
		
		return target.getPadY();
	}
	
	protected float getTargetWidth() {
		if(target == null) {
			return 0;
		}
		
		return target.getPadWidth();
	}
	
	protected float getTargetHeight() {
		if(target == null) {
			return 0;
		}
		
		return target.getPadHeight();
	}
	
	protected void checkWeight(int weight) {
		if(weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
			throw new IllegalArgumentException("Weight for Shadow must be between: " + MIN_WEIGHT + " and " + MAX_WEIGHT);
		}
	}
}