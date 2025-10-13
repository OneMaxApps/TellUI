package microui.constants;

public enum AutoResizeMode {
	FULL(1), BIG(2), MIDDLE(3), SMALL(4), TINY(5);

	private final int value;

	private AutoResizeMode(int value) {
		this.value = value;
	}

	public final int getValue() {
		return value;
	}

}