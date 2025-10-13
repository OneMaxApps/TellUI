package microui.layout;

public class LinearLayoutParams extends LinearAxisLayoutParams {

	public LinearLayoutParams(float weight, int alignX, int alignY) {
		super(weight, alignX, alignY);
	}

	public LinearLayoutParams(float weight, int align) {
		this(weight, align, align);
	}

	public LinearLayoutParams(float weight) {
		this(weight, 0);
	}

	@Override
	public int getAlignX() {
		return super.getAlignX();
	}

	@Override
	public int getAlignY() {
		return super.getAlignY();
	}

}