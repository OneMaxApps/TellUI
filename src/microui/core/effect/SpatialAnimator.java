package microui.core.effect;

import java.util.function.BooleanSupplier;

import microui.core.base.SpatialView;
import microui.util.MathUtils;
import microui.util.SpatialState;
import microui.util.Timer;

public final class SpatialAnimator {
	private boolean isEnabled;
	private final SpatialState startSpatialState, endSpatialState;
	private BooleanSupplier condition;
	private final Timer timer;
	private SpatialView targetSpatialView;
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
		
		setEnabled(true);
		setPositionEnabled(true);
		setDimensionsEnabled(true);
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

		if(timer.isComplete()) {
			timer.setIncrementing(condition.getAsBoolean());
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
	
	private float lerp(float start, float end) {
		return MathUtils.convert(timer.getCurrent(), Timer.START, Timer.END, start, end);
	}
}