package microui.core.effect;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.convert;
import static microui.util.Timer.END;
import static microui.util.Timer.START;

import java.util.function.BooleanSupplier;

import microui.core.base.ContentView;
import microui.core.base.SpatialView;
import microui.util.SpatialState;
import microui.util.Timer;

public final class SpatialAnimator {
	private final SpatialState startSpatialState, endSpatialState;
	private ContentView startContentView, endContentView;
	private final Timer timer;
	private final BooleanSupplier condition;
	private SpatialView targetSpatialView;
	private ReactionMode reactionMode;
	private boolean isEnabled;
	private boolean isPositionEnabled, isDimensionsEnabled;

	public SpatialAnimator(SpatialState startSpatialState, SpatialState endSpatialState, BooleanSupplier condition) {
		super();

		if (startSpatialState == null) {
			throw new NullPointerException("the startSpatialState object cannot be null");
		}

		if (endSpatialState == null) {
			throw new NullPointerException("the endSpatialState object cannot be null");
		}

		if (condition == null) {
			throw new NullPointerException("the condition object cannot be null");
		}

		this.startSpatialState = startSpatialState;
		this.endSpatialState = endSpatialState;
		this.condition = condition;

		timer = new Timer();
		timer.setSpeed(.2f);
		
//		setReactionMode(ReactionMode.TRIGGER);
		setReactionMode(ReactionMode.REACTIVE);
		setEnabled(true);
		setPositionEnabled(true);
		setDimensionsEnabled(true);
	}

	public SpatialAnimator(ContentView start, SpatialState end, BooleanSupplier condition) {
		this(new SpatialState(requireNonNull(start, "SpatialView for SpatialAnimator cannot be null").getX(),
				start.getY(), start.getAbsoluteWidth(), start.getAbsoluteHeight()), end, condition);
		startContentView = start;
	}

	public SpatialAnimator(SpatialState start, ContentView end, BooleanSupplier condition) {
		this(start, new SpatialState(requireNonNull(end, "SpatialView for SpatialAnimator cannot be null").getX(),
				end.getY(), end.getAbsoluteWidth(), end.getAbsoluteHeight()), condition);
		endContentView = end;
	}

	public SpatialAnimator(ContentView start, ContentView end, BooleanSupplier condition) {
		this(start, new SpatialState(requireNonNull(end, "SpatialView for SpatialAnimator cannot be null").getX(),
				end.getY(), end.getAbsoluteWidth(), end.getAbsoluteHeight()), condition);
		startContentView = start;
		endContentView = end;
	}

	public ReactionMode getReactionMode() {
		return reactionMode;
	}

	public SpatialAnimator setReactionMode(ReactionMode reactionMode) {
		if (reactionMode == null) {
			throw new NullPointerException("the reactionMode object cannot be null");
		}
		this.reactionMode = reactionMode;

		return this;
	}

	public boolean isPositionEnabled() {
		return isPositionEnabled;
	}

	public SpatialAnimator setPositionEnabled(boolean isPositionEnabled) {
		this.isPositionEnabled = isPositionEnabled;

		return this;
	}

	public boolean isDimensionsEnabled() {
		return isDimensionsEnabled;
	}

	public SpatialAnimator setDimensionsEnabled(boolean isDimensionsEnabled) {
		this.isDimensionsEnabled = isDimensionsEnabled;

		return this;
	}

	public SpatialView getTargetSpatialView() {
		return targetSpatialView;
	}

	public SpatialAnimator setTargetSpatialView(SpatialView targetSpatialView) {
		if (targetSpatialView == null) {
			throw new NullPointerException("the targetSpatialView object cannot be null");
		}

		this.targetSpatialView = targetSpatialView;

		return this;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public SpatialAnimator setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;

		return this;
	}

	public void update() {
		if (!isEnabled() || targetSpatialView == null) {
			return;
		}

		switch (getReactionMode()) {
		case REACTIVE:
			timer.setIncrementing(condition.getAsBoolean());
			break;

		case TRIGGER:
			if (timer.isComplete()) {
				timer.setIncrementing(condition.getAsBoolean());
			}
			break;
		}

		timer.update();

		if (isPositionEnabled()) {
			final float startX = startContentView != null ? startContentView.getAbsoluteX() : startSpatialState.x();
			final float endX = endContentView != null ? endContentView.getAbsoluteX() : endSpatialState.x();

			final float startY = startContentView != null ? startContentView.getAbsoluteY() : startSpatialState.y();
			final float endY = endContentView != null ? endContentView.getAbsoluteY() : endSpatialState.y();

			targetSpatialView.setX(lerp(startX, endX));
			targetSpatialView.setY(lerp(startY, endY));
		}

		if (isDimensionsEnabled()) {

			final float startWidth = startContentView != null ? startContentView.getAbsoluteWidth() : startSpatialState.width();
			final float endWidth = endContentView != null ? endContentView.getAbsoluteWidth() : endSpatialState.width();

			final float startHeight = startContentView != null ? startContentView.getAbsoluteHeight(): startSpatialState.height();
			final float endHeight = endContentView != null ? endContentView.getAbsoluteHeight() : endSpatialState.height();

			targetSpatialView.setWidth(lerp(startWidth, endWidth));
			targetSpatialView.setHeight(lerp(startHeight, endHeight));
		}

	}

	public static enum ReactionMode {
		REACTIVE, TRIGGER;
	}

	private float lerp(float start, float end) {
		return convert(timer.getCurrent(), START, END, start, end);
	}
}