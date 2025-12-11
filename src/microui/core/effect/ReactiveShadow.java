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
 * 
 * @author microui.core
 * @version 1.0
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
		// Update weights only when mouse is moving for performance
		if(isMouseMoving()) {
			updateWeights();
		}
		
		// Draw only outline, no fill
		ctx.noFill();
		
		// Get target component bounds
		float x = getTargetX();
		float y = getTargetY();
		float x1 = getTargetX()+getTargetWidth();
		float y1 = getTargetY()+getTargetHeight();
		
		// Set rectangle mode to corner coordinates
		ctx.rectMode(PApplet.CORNERS);
		
		// Draw gradient shadow from full opacity to transparent
		for(int i = 0; i < MAX_WEIGHT; i++) {
			// Calculate alpha value that fades from full to transparent
			ctx.stroke(getColor().getRed(), getColor().getGreen(), getColor().getBlue(),
			          convert(i, 0, MAX_WEIGHT, getColor().getAlpha(), 0));
			
			// Calculate expanded bounds for this gradient level
			float newX = x-convert(i,0,MAX_WEIGHT,0,getWeightLeft());
			float newY = y-convert(i,0,MAX_WEIGHT,0,getWeightTop());
			float newX1 = x1+convert(i,0,MAX_WEIGHT,0,getWeightRight());
			float newY1 = y1+convert(i,0,MAX_WEIGHT,0,getWeightBottom());
			
			// Draw rectangle for this gradient level
			ctx.rect(newX,newY,newX1,newY1);
		}
			
	}
	
	/**
	 * Updates shadow weights based on current mouse position relative to target bounds.
	 * Calculates each side's weight based on mouse proximity:
	 * - Closer mouse = higher weight (darker shadow)
	 * - Farther mouse = lower weight (lighter shadow)
	 */
	private void updateWeights() {
		// Get target component bounds
		final float tx = getTarget().getPadX();
		final float ty = getTarget().getPadY();
		final float tw = getTarget().getPadWidth();
		final float th = getTarget().getPadHeight();
		
		// Calculate left weight based on horizontal mouse position
		float newLeft = 0; 
		if(ctx.mouseX > tx+tw) {
			// Mouse is to the right of target: weight decreases as mouse moves farther right
			newLeft = convert(ctx.mouseX,tx+tw,ctx.width,MAX_WEIGHT,MIN_WEIGHT);
		} else {
			// Mouse is within or to the left of target: weight increases as mouse approaches left side
			newLeft = convert(ctx.mouseX,tx,tx+tw,MIN_WEIGHT,MAX_WEIGHT);
		}
		newLeft = constrain(newLeft,0,MAX_WEIGHT);
		
		// Calculate top weight based on vertical mouse position
		float newTop = 0; 
		if(ctx.mouseY > ty+th) {
			// Mouse is below target: weight decreases as mouse moves farther down
			newTop = convert(ctx.mouseY,ty+th,ctx.height,MAX_WEIGHT,MIN_WEIGHT);
		} else {
			// Mouse is within or above target: weight increases as mouse approaches top
			newTop = convert(ctx.mouseY,ty,ty+th,MIN_WEIGHT,MAX_WEIGHT);
		}
		newTop = constrain(newTop,0,MAX_WEIGHT);
		
		// Calculate right weight based on horizontal mouse position
		float newRight = 0; 
		if(ctx.mouseX < tx) {
			// Mouse is to the left of target: weight increases as mouse moves farther left
			newRight = convert(ctx.mouseX,0,tx,MIN_WEIGHT,MAX_WEIGHT);
		} else {
			// Mouse is within or to the right of target: weight decreases as mouse approaches right side
			newRight = convert(ctx.mouseX,tx,tx+tw,MAX_WEIGHT,MIN_WEIGHT);
		}
		newRight = constrain(newRight,0,MAX_WEIGHT);
		
		// Calculate bottom weight based on vertical mouse position
		float newBottom = 0; 
		if(ctx.mouseY < ty) {
			// Mouse is above target: weight increases as mouse moves farther up
			newBottom = convert(ctx.mouseY,0,ty,MIN_WEIGHT,MAX_WEIGHT);
		} else {
			// Mouse is within or below target: weight decreases as mouse approaches bottom
			newBottom = convert(ctx.mouseY,ty,ty+th,MAX_WEIGHT,MIN_WEIGHT);
		}
		newBottom = constrain(newBottom,0,MAX_WEIGHT);
		
		// Apply calculated weights
		setWeightLeft((int) newLeft);
		setWeightTop((int) newTop);
		setWeightRight((int) newRight);
		setWeightBottom((int) newBottom);
	}
	
	/**
	 * Checks if the mouse is currently moving.
	 * 
	 * @return true if mouse position has changed since last frame, false otherwise
	 */
	private boolean isMouseMoving() {
		return ctx.mouseX != ctx.pmouseX || ctx.mouseY != ctx.pmouseY;
	}
}