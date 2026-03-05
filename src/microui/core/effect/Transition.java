package microui.core.effect;

import java.util.Objects;

import microui.util.SpatialState;

public final class Transition {
	private SpatialState currentStart, currentEnd, previousStart, previousEnd;

	public final SpatialState getCurrentStart() {
		return currentStart;
	}

	public final void setCurrentStart(SpatialState currentStart) {
		this.currentStart = Objects.requireNonNull(currentStart,"currentStart");
	}

	public final SpatialState getCurrentEnd() {
		return currentEnd;
	}

	public final void setCurrentEnd(SpatialState currentEnd) {
		this.currentEnd = Objects.requireNonNull(currentEnd,"currentEnd");
	}

	public final SpatialState getPreviousStart() {
		return previousStart;
	}

	public final void setPreviousStart(SpatialState previousStart) {
		this.previousStart = Objects.requireNonNull(previousStart,"previousStart");
	}

	public final SpatialState getPreviousEnd() {
		return previousEnd;
	}

	public final void setPreviousEnd(SpatialState previousEnd) {
		this.previousEnd = Objects.requireNonNull(previousEnd,"previousEnd");
	}
	
}