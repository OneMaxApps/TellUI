package microui.layout;

public final class RowLayoutParams extends LinearAxisLayoutParams {

	public RowLayoutParams(float weight, int alignY) {
		super(weight, 0, alignY);
	}

	public RowLayoutParams(float weight) {
		this(weight, 0);
	}

	@Override
	public int getAlignY() {
		return super.getAlignY();
	}

}