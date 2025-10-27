package microui.core.style;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

//Status: STABLE - Do not modify
//Last Reviewed: 11.10.2025
public class Color extends AbstractColor {
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color PINK = new Color(255, 0, 255);
	public static final Color SKY = new Color(0, 255, 255);

	public static final Color BLACK = new Color(0);
	public static final Color GRAY_8L = new Color(8);
	public static final Color GRAY_16L = new Color(16);
	public static final Color GRAY_32L = new Color(32);
	public static final Color GRAY_64L = new Color(64);
	public static final Color GRAY_128L = new Color(128);
	public static final Color GRAY_164L = new Color(164);
	public static final Color GRAY_200L = new Color(200);
	public static final Color GRAY_232L = new Color(232);
	public static final Color WHITE = new Color(255);

	public static final Color TRANSPARENT = new Color(0, 0);

	private final int red, green, blue, alpha;

	public Color(float red, float green, float blue, float alpha) {
		this.red   = (int) constrain(red, MIN_VALUE, MAX_VALUE);
		this.green = (int) constrain(green, MIN_VALUE, MAX_VALUE);
		this.blue  = (int) constrain(blue, MIN_VALUE, MAX_VALUE);
		this.alpha = (int) constrain(alpha, MIN_VALUE, MAX_VALUE);
	}

	public Color(float red, float green, float blue) {
		this(red, green, blue, MAX_VALUE);
	}

	public Color(float gray, float alpha) {
		this(gray, gray, gray, alpha);
	}

	public Color(float gray) {
		this(gray, gray, gray, MAX_VALUE);
	}

	public Color(AbstractColor color) {
		this(requireNonNull(color, "color").getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha());
	}

	public Color() {
		this(128);
	}

	@Override
	public int getRed() {
		return red;
	}

	@Override
	public int getGreen() {
		return green;
	}

	@Override
	public int getBlue() {
		return blue;
	}

	@Override
	public int getAlpha() {
		return alpha;
	}

	public final boolean isTransparent() {
		return alpha == 0;
	}
}