package microui.core.effect;

import microui.core.base.ContentView;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.core.style.Color;

public abstract class AbstractShadow extends View {
	public static final int MIN_WEIGHT = 0;
	public static final int MAX_WEIGHT = 10;
	
	private AbstractColor color;
	private ContentView target;
	
	public AbstractShadow() {
		super();
		setVisible(true);
		setColor(new Color(0,32));
	}

	public ContentView getTarget() {
		return target;
	}

	public void setTarget(ContentView target) {
		if(target == null) {
			throw new NullPointerException("Target for Shadow cannot be null");
		}
		
		this.target = target;
	}
	
	public AbstractColor getColor() {
		return color;
	}

	public void setColor(AbstractColor color) {
		if(color == null) {
			throw new NullPointerException("Color for Shadow cannot be null");
		}
		
		this.color = color;
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