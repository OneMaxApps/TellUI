package microui.core.effect;

import microui.core.base.ContentView;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.core.style.Color;

public final class Shadow extends View {
	private static final int MAX_WEIGHT = 10;
	private static final int DEFAULT_WEIGHT = 4;
	private final ContentView targetView;
	private AbstractColor color;
	private DirectionMode directionMode;
	private int weight;

	public Shadow(ContentView targetView) {
		super();
		setVisible(true);

		if (targetView == null) {
			throw new NullPointerException("ContentView for Shadow cannot be null");
		}

		this.targetView = targetView;

		setColor(new Color(0, 32));
		setDirectionMode(DirectionMode.LEFT);
		setWeight(DEFAULT_WEIGHT);
	}

	public AbstractColor getColor() {
		return color;
	}

	public void setColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("Color for Shadow cannot be null");
		}
		this.color = color;
	}

	public DirectionMode getDirectionMode() {
		return directionMode;
	}

	public void setDirectionMode(DirectionMode directionMode) {
		if (directionMode == null) {
			throw new NullPointerException("DirectionMode for Shadow cannot be null");
		}

		this.directionMode = directionMode;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		if (weight <= 0) {
			throw new IllegalArgumentException("Weight for Shadow cannot be less or equal zero");
		}

		if (weight > MAX_WEIGHT) {
			throw new IllegalArgumentException("Weight for Shadow cannot be greater than " + MAX_WEIGHT);
		}

		this.weight = weight;
	}

	@Override
	protected void render() {
		color.applyStroke();
		ctx.noFill();
		
		for (int i = 0; i < getWeight(); i++) {
			
			switch (getDirectionMode()) {
				case LEFT:
					shadowFormOnDraw(i, 0, 0, 0);
					break;
					
				case TOP:
					shadowFormOnDraw(0, i, 0, 0);
					break;
					
				case RIGHT:
					shadowFormOnDraw(0, 0, i, 0);
					break;
					
				case BOTTOM:
					shadowFormOnDraw(0, 0, 0, i);
					break;
			}

		}
	}

	private void shadowFormOnDraw(int shiftLeft, int shiftTop, int shiftRight, int shiftBottom) {
		ctx.rect(targetView.getPadX() - shiftLeft, targetView.getPadY() - shiftTop,
				targetView.getPadWidth() + shiftRight, targetView.getPadHeight() + shiftBottom);
	}

	public static enum DirectionMode {
		LEFT, RIGHT, TOP, BOTTOM;
	}
}
