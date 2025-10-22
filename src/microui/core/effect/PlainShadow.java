package microui.core.effect;

import static microui.util.MathUtils.convert;

import microui.core.base.ContentView;
import processing.core.PApplet;

public class PlainShadow extends AbstractShadow {
	private boolean isAlphaFadeOutEnabled;

	public PlainShadow() {
		super();
		setAlphaFadeOutEnabled(true);
	}

	public PlainShadow(ContentView target) {
		this();
		setTarget(target);
	}

	public boolean isAlphaFadeOutEnabled() {
		return isAlphaFadeOutEnabled;
	}

	public PlainShadow setAlphaFadeOutEnabled(boolean isAlphaFadeOutEnabled) {
		this.isAlphaFadeOutEnabled = isAlphaFadeOutEnabled;
		
		return this;
	}
	
	@Override
	public AbstractShadow setWeightLeft(int weightLeft) {
		return super.setWeightLeft(weightLeft);
	}

	@Override
	public AbstractShadow setWeightTop(int weightTop) {
		return super.setWeightTop(weightTop);
	}

	@Override
	public AbstractShadow setWeightRight(int weightRight) {
		return super.setWeightRight(weightRight);
	}

	@Override
	public AbstractShadow setWeightBottom(int weightBottom) {
		return super.setWeightBottom(weightBottom);
	}

	@Override
	public AbstractShadow setWeight(int weight) {
		return super.setWeight(weight);
	}

	@Override
	public AbstractShadow setWeight(int weightHorizontal, int weightVertical) {
		return super.setWeight(weightHorizontal, weightVertical);
	}

	@Override
	public AbstractShadow setWeight(int left, int top, int right, int bottom) {
		return super.setWeight(left, top, right, bottom);
	}
	
	@Override
	protected void render() {
		getColor().apply();
		
		float x = getTargetX();
		float y = getTargetY();
		float x1 = getTargetX()+getTargetWidth();
		float y1 = getTargetY()+getTargetHeight();
		
		ctx.rectMode(PApplet.CORNERS);
		
		if(isAlphaFadeOutEnabled()) {
		
			for(int i = 0; i < MAX_WEIGHT; i++) {
				ctx.stroke(getColor().getRed(), getColor().getGreen(), getColor().getBlue(),convert(i, 0, MAX_WEIGHT, getColor().getAlpha(), 0));
				
				float newX = x-convert(i,0,MAX_WEIGHT,0,getWeightLeft());
				float newY = y-convert(i,0,MAX_WEIGHT,0,getWeightTop());
				float newX1 = x1+convert(i,0,MAX_WEIGHT,0,getWeightRight());
				float newY1 = y1+convert(i,0,MAX_WEIGHT,0,getWeightBottom());
				
				ctx.rect(newX,newY,newX1,newY1);
			}
			
		} else {
			getColor().applyStroke();
			ctx.rect(x-getWeightLeft(),y-getWeightTop(),x1+getWeightRight(),y1+getWeightBottom());
		}
		
	}

}