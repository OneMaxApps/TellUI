package microui.core.effect;

import static microui.util.MathUtils.constrain;
import static microui.util.MathUtils.convert;

import microui.core.base.ContentView;
import microui.core.style.Color;
import processing.core.PApplet;

public class ReactiveShadow extends AbstractShadow {

	public ReactiveShadow() {
		super();
		clearAllWeight();
		setColor(new Color(0,32));
	}
	
	public ReactiveShadow(ContentView target) {
		this();
		setTarget(target);

	}
	
	public void requestUpdateWeights() {
		updateWeights();
	}

	@Override
	protected void render() {
		if(isMouseMoving()) {
			updateWeights();
		}
		
		ctx.noFill();
		
		float x = getTargetX();
		float y = getTargetY();
		float x1 = getTargetX()+getTargetWidth();
		float y1 = getTargetY()+getTargetHeight();
		
		ctx.rectMode(PApplet.CORNERS);
		
		for(int i = 0; i < MAX_WEIGHT; i++) {
			ctx.stroke(getColor().getRed(), getColor().getGreen(), getColor().getBlue(),convert(i, 0, MAX_WEIGHT, getColor().getAlpha(), 0));
			
			float newX = x-convert(i,0,MAX_WEIGHT,0,getWeightLeft());
			float newY = y-convert(i,0,MAX_WEIGHT,0,getWeightTop());
			float newX1 = x1+convert(i,0,MAX_WEIGHT,0,getWeightRight());
			float newY1 = y1+convert(i,0,MAX_WEIGHT,0,getWeightBottom());
			
			ctx.rect(newX,newY,newX1,newY1);
		}
			
	}
	
	private void updateWeights() {
		final float tx = getTarget().getPadX();
		final float ty = getTarget().getPadY();
		final float tw = getTarget().getPadWidth();
		final float th = getTarget().getPadHeight();
		
		float newLeft = 0; 
		if(ctx.mouseX > tx+tw) {
			newLeft = convert(ctx.mouseX,tx+tw,ctx.width,MAX_WEIGHT,MIN_WEIGHT);
		} else {
			newLeft = convert(ctx.mouseX,tx,tx+tw,MIN_WEIGHT,MAX_WEIGHT);
		}
		newLeft = constrain(newLeft,0,MAX_WEIGHT);
		
		float newTop = 0; 
		if(ctx.mouseY > ty+th) {
			newTop = convert(ctx.mouseY,ty+th,ctx.height,MAX_WEIGHT,MIN_WEIGHT);
		} else {
			newTop = convert(ctx.mouseY,ty,ty+th,MIN_WEIGHT,MAX_WEIGHT);
		}
		newTop = constrain(newTop,0,MAX_WEIGHT);
		
		float newRight = 0; 
		if(ctx.mouseX < tx) {
			newRight = convert(ctx.mouseX,0,tx,MIN_WEIGHT,MAX_WEIGHT);
		} else {
			newRight = convert(ctx.mouseX,tx,tx+tw,MAX_WEIGHT,MIN_WEIGHT);
		}
		newRight = constrain(newRight,0,MAX_WEIGHT);
		
		float newBottom = 0; 
		if(ctx.mouseY < ty) {
			newBottom = convert(ctx.mouseY,0,ty,MIN_WEIGHT,MAX_WEIGHT);
		} else {
			newBottom = convert(ctx.mouseY,ty,ty+th,MAX_WEIGHT,MIN_WEIGHT);
		}
		newBottom = constrain(newBottom,0,MAX_WEIGHT);
		
		setWeightLeft((int) newLeft);
		setWeightTop((int) newTop);
		setWeightRight((int) newRight);
		setWeightBottom((int) newBottom);
	}
	
	private boolean isMouseMoving() {
		return ctx.mouseX != ctx.pmouseX || ctx.mouseY != ctx.pmouseY;
	}
}