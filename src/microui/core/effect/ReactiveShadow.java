package microui.core.effect;

import static microui.util.MathUtils.constrain;
import static microui.util.MathUtils.convert;

import microui.core.base.ContentView;
import microui.core.style.Color;
import processing.core.PApplet;

/**
 * A reactive shadow effect that dynamically adjusts its intensity based on mouse position.
 * Creates an interactive shadow that responds to mouse movement, with shadow weights
 * calculated relative to the mouse's position relative to the target component.
 * <p>
 * The ReactiveShadow creates a gradient shadow effect where each side's intensity
 * is determined by the mouse's proximity to that side of the target component.
 * The shadow dynamically updates as the mouse moves, creating an interactive
 * visual feedback effect.
 * </p>
 * @see AbstractShadow
 * @see ContentView
 */
public class ReactiveShadow extends AbstractShadow {

	/**
	 * Constructs a ReactiveShadow with default properties.
	 * Initializes with all weights cleared and a semi-transparent black color.
	 */
	public ReactiveShadow() {
		super();
		clearAllWeight();
		setColor(new Color(0,32));
	}
	
	/**
	 * Constructs a ReactiveShadow with a specified target ContentView.
	 * 
	 * @param target the ContentView to apply the reactive shadow to
	 */
	public ReactiveShadow(ContentView target) {
		this();
		setTarget(target);

	}
	
	/**
	 * Requests an immediate update of shadow weights based on current mouse position.
	 * Useful for forcing a weight recalculation when needed.
	 */
	public void requestUpdateWeights() {
		updateWeights();
	}

	/**
	 * Renders the reactive shadow effect.
	 * Automatically updates shadow weights when the mouse is moving,
	 * then draws the gradient shadow based on current weight values.
	 */
	@Override
	protected void render() {
		if(isMouseMoving()) {
			updateWeights();
		}
		
		ctx.noFill();
		
		final float x = getTargetX();
		final float y = getTargetY();
		final float x1 = getTargetX()+getTargetWidth();
		final float y1 = getTargetY()+getTargetHeight();
		
		ctx.rectMode(PApplet.CORNERS);
		
		for(int i = 0; i < MAX_WEIGHT; i++) {
			ctx.stroke(getColor().getRed(), getColor().getGreen(), getColor().getBlue(),
			          convert(i, 0, MAX_WEIGHT, getColor().getAlpha(), 0));
			
			final float newX = x-convert(i,0,MAX_WEIGHT,0,getWeightLeft());
			final float newY = y-convert(i,0,MAX_WEIGHT,0,getWeightTop());
			final float newX1 = x1+convert(i,0,MAX_WEIGHT,0,getWeightRight());
			final float newY1 = y1+convert(i,0,MAX_WEIGHT,0,getWeightBottom());
			
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