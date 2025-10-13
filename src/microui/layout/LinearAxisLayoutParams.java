package microui.layout;

public abstract class LinearAxisLayoutParams implements LayoutParams {
	private final float weight;
	private final int alignX, alignY;

	public LinearAxisLayoutParams(float weight, int alignX, int alignY) {
		super();

		if (weight < 0) {
			throw new IllegalArgumentException("weight cannot be lower than zero");
		}

		if (weight > 1) {
			throw new IllegalArgumentException("weight cannot be greater than 1");
		}

		if (alignX < -1) {
			throw new IllegalArgumentException("alignX cannot be lower than -1");
		}

		if (alignX > 1) {
			throw new IllegalArgumentException("alignX cannot be greater than 1");
		}

		if (alignY < -1) {
			throw new IllegalArgumentException("alignY cannot be lower than -1");
		}

		if (alignY > 1) {
			throw new IllegalArgumentException("alignY cannot be greater than 1");
		}

		this.weight = weight;
		this.alignX = alignX;
		this.alignY = alignY;
	}

	public final float getWeight() {
		return weight;
	}

	protected int getAlignX() {
		return alignX;
	}

	protected int getAlignY() {
		return alignY;
	}
}