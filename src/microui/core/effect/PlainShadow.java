package microui.core.effect;

import microui.core.base.ContentView;
import processing.core.PApplet;

public class PlainShadow extends AbstractShadow {
	private static final int DEFAULT_WEIGHT_LEFT = 0;
	private static final int DEFAULT_WEIGHT_TOP = 0;
	private static final int DEFAULT_WEIGHT_RIGHT = 3;
	private static final int DEFAULT_WEIGHT_BOTTOM = 2;
	
	private int weightLeft,weightTop,weightRight,weightBottom;
	
	public PlainShadow() {
		super();
		
		setWeight(DEFAULT_WEIGHT_LEFT,DEFAULT_WEIGHT_TOP,DEFAULT_WEIGHT_RIGHT,DEFAULT_WEIGHT_BOTTOM);
	}
	
	public PlainShadow(ContentView target) {
		this();
		setTarget(target);
		
	}
	
	public int getWeightLeft() {
		return weightLeft;
	}

	public PlainShadow setWeightLeft(int weightLeft) {
		checkWeight(weightLeft);
		this.weightLeft = weightLeft;
		
		return this;
	}

	public int getWeightTop() {
		return weightTop;
	}

	public PlainShadow setWeightTop(int weightTop) {
		checkWeight(weightTop);
		this.weightTop = weightTop;
		return this;
	}

	public int getWeightRight() {
		return weightRight;
	}

	public PlainShadow setWeightRight(int weightRight) {
		checkWeight(weightRight);
		this.weightRight = weightRight;
		return this;
	}

	public int getWeightBottom() {
		return weightBottom;
	}

	public PlainShadow setWeightBottom(int weightBottom) {
		checkWeight(weightBottom);
		this.weightBottom = weightBottom;
		return this;
	}
	
	public PlainShadow clearAllWeight() {
		weightLeft = weightTop = weightRight = weightBottom = 0;
		return this;
	}
	
	public PlainShadow setWeight(int weight) {
		checkWeight(weight);
		weightLeft = weightTop = weightRight = weightBottom = weight;
		return this;
	}
	
	public PlainShadow setWeight(int weightHorizontal, int weightVertical) {
		checkWeight(weightHorizontal);
		checkWeight(weightVertical);
		
		weightLeft = weightRight = weightHorizontal;
		weightTop = weightBottom = weightVertical;
		return this;
	}
	
	public PlainShadow setWeight(int left, int top, int right, int bottom) {
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

	@Override
	protected void render() {
		ctx.noStroke();
		getColor().apply();
		
		float x = getTargetX();
		float y = getTargetY();
		float x1 = getTargetX()+getTargetWidth();
		float y1 = getTargetX()+getTargetHeight();
		
		ctx.rectMode(PApplet.CORNERS);
		ctx.rect(x-weightLeft,y-weightTop,x1+weightRight,y1+weightBottom);
	}
	
}