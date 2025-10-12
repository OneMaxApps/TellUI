package microui.core.effect;

import static microui.util.MathUtils.convert;
import static microui.util.Timer.END;
import static microui.util.Timer.START;

import java.util.function.BooleanSupplier;

import microui.core.base.SpatialView;
import microui.util.SpatialState;
import microui.util.Timer;

public final class SpatialAnimator {
	private final SpatialState startSpatialState, endSpatialState;
	private final Timer timer;
	private final BooleanSupplier condition;
	private SpatialView targetSpatialView;
	private ReactionMode reactionMode;
	private boolean isEnabled;
	private boolean isPositionEnabled,isDimensionsEnabled;
	
	public SpatialAnimator(SpatialState startSpatialState, SpatialState endSpatialState, BooleanSupplier condition) {
		super();
		
		if(startSpatialState == null) {
			throw new NullPointerException("the startSpatialState object cannot be null");
		}
		
		if(endSpatialState == null) {
			throw new NullPointerException("the endSpatialState object cannot be null");
		}
		
		if(condition == null) {
			throw new NullPointerException("the condition object cannot be null");
		}
		
		this.startSpatialState = startSpatialState;
		this.endSpatialState = endSpatialState;
		this.condition = condition;
		
		timer = new Timer();
		
//		setReactionMode(ReactionMode.TRIGGER);
		setReactionMode(ReactionMode.REACTIVE);
		setEnabled(true);
		setPositionEnabled(true);
		setDimensionsEnabled(true);
	}

	public ReactionMode getReactionMode() {
		return reactionMode;
	}
	
	public void setReactionMode(ReactionMode reactionMode) {
		if(reactionMode == null) {
			throw new NullPointerException("the reactionMode object cannot be null");
		}
		this.reactionMode = reactionMode;
	}

	public boolean isPositionEnabled() {
		return isPositionEnabled;
	}

	public void setPositionEnabled(boolean isPositionEnabled) {
		this.isPositionEnabled = isPositionEnabled;
	}

	public boolean isDimensionsEnabled() {
		return isDimensionsEnabled;
	}

	public void setDimensionsEnabled(boolean isDimensionsEnabled) {
		this.isDimensionsEnabled = isDimensionsEnabled;
	}

	public SpatialView getTargetSpatialView() {
		return targetSpatialView;
	}
	
	public void setTargetSpatialView(SpatialView targetSpatialView) {
		if(targetSpatialView == null) {
			throw new NullPointerException("the targetSpatialView object cannot be null");
		}
		
		this.targetSpatialView = targetSpatialView;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public void update() {
		if(!isEnabled() || targetSpatialView == null) { return; }

		switch(getReactionMode()) {
			case REACTIVE:
				timer.setIncrementing(condition.getAsBoolean());
				break;
				
			case TRIGGER:
				if(timer.isComplete()) {
					timer.setIncrementing(condition.getAsBoolean());
				}
				break;
		}
		
		timer.update();

		if(isPositionEnabled()) {
			targetSpatialView.setX(lerp(startSpatialState.x(),endSpatialState.x()));
			targetSpatialView.setY(lerp(startSpatialState.y(),endSpatialState.y()));
		}
		
		if(isDimensionsEnabled()) {
			targetSpatialView.setWidth(lerp(startSpatialState.width(),endSpatialState.width()));
			targetSpatialView.setHeight(lerp(startSpatialState.height(),endSpatialState.height()));
		}
		
	}
	
	public static enum ReactionMode {
		REACTIVE,
		TRIGGER;
	}
	
	private float lerp(float start, float end) {
		return convert(timer.getCurrent(), START, END, start, end);
	}
}