package tellui.core.style;

import static java.util.Objects.requireNonNull;
import static telluii.util.MathUtils.constrain;

/**
 * Concrete implementation of AbstractColor representing a solid RGBA color.
 * Provides a comprehensive set of predefined colors and flexible constructors
 * for creating custom colors with validation and constraint enforcement.
 * <p>
 * This class represents immutable color values with RGBA components. All color
 * values are constrained to the valid range (0-255) and the class includes
 * convenient factory methods and predefined color constants.
 * </p>
 * 
 * @see AbstractColor
 */
public class Color extends AbstractColor {
	/** Pure red color */
	public static final Color RED = new Color(255, 0, 0);
	/** Pure green color */
	public static final Color GREEN = new Color(0, 255, 0);
	/** Pure blue color */
	public static final Color BLUE = new Color(0, 0, 255);
	/** Pure yellow color */
	public static final Color YELLOW = new Color(255, 255, 0);
	/** Pure pink color */
	public static final Color PINK = new Color(255, 0, 255);
	/** Pure sky color */
	public static final Color SKY = new Color(0, 255, 255);

	/** Pure black color */
	public static final Color BLACK = new Color(0);
	/** Gray color 8 shades */
	public static final Color GRAY_8L = new Color(8);
	/** Gray color 16 shades */
	public static final Color GRAY_16L = new Color(16);
	/** Gray color 32 shades */
	public static final Color GRAY_32L = new Color(32);
	/** Gray color 64 shades */
	public static final Color GRAY_64L = new Color(64);
	/** Gray color 128 shades */
	public static final Color GRAY_128L = new Color(128);
	/** Gray color 164 shades */
	public static final Color GRAY_164L = new Color(164);
	/** Gray color 200 shades */
	public static final Color GRAY_200L = new Color(200);
	/** Gray color 232 shades */
	public static final Color GRAY_232L = new Color(232);

	/** Pure white color */
	public static final Color WHITE = new Color(255);

	/** Fully transparent color */
	public static final Color TRANSPARENT = new Color(0, 0);

	private final int red, green, blue, alpha;

	/**
	 * Constructs a Color with specified RGBA components.
	 * 
	 * @param red the red component (0-255, will be constrained)
	 * @param green the green component (0-255, will be constrained)
	 * @param blue the blue component (0-255, will be constrained)
	 * @param alpha the alpha component (0-255, will be constrained)
	 */
	public Color(float red, float green, float blue, float alpha) {
		this.red = (int) constrain(red, MIN_VALUE, MAX_VALUE);
		this.green = (int) constrain(green, MIN_VALUE, MAX_VALUE);
		this.blue = (int) constrain(blue, MIN_VALUE, MAX_VALUE);
		this.alpha = (int) constrain(alpha, MIN_VALUE, MAX_VALUE);
	}

	/**
	 * Constructs a Color with specified RGB components and full opacity.
	 * 
	 * @param red the red component (0-255, will be constrained)
	 * @param green the green component (0-255, will be constrained)
	 * @param blue the blue component (0-255, will be constrained)
	 */
	public Color(float red, float green, float blue) {
		this(red, green, blue, MAX_VALUE);
	}

	/**
	 * Constructs a grayscale Color with specified luminance and alpha.
	 * 
	 * @param gray  the grayscale luminance (0-255, will be constrained)
	 * @param alpha the alpha component (0-255, will be constrained)
	 */
	public Color(float gray, float alpha) {
		this(gray, gray, gray, alpha);
	}

	/**
	 * Constructs a grayscale Color with specified luminance and full opacity.
	 * 
	 * @param gray the grayscale luminance (0-255, will be constrained)
	 */
	public Color(float gray) {
		this(gray, gray, gray, MAX_VALUE);
	}

	/**
	 * Constructs a Color by copying components from another AbstractColor.
	 * 
	 * @param color the color to copy (cannot be null)
	 * @throws NullPointerException if color is null
	 */
	public Color(AbstractColor color) {
		this(requireNonNull(color, "color").getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Constructs a default medium gray color (128 luminance, full opacity).
	 */
	public Color() {
		this(128);
	}

	/**
	 * Returns the red component.
	 * 
	 * @return the red component value (0-255)
	 */
	@Override
	public int getRed() {
		return red;
	}

	/**
	 * Returns the green component.
	 * 
	 * @return the green component value (0-255)
	 */
	@Override
	public int getGreen() {
		return green;
	}

	/**
	 * Returns the blue component.
	 * 
	 * @return the blue component value (0-255)
	 */
	@Override
	public int getBlue() {
		return blue;
	}

	/**
	 * Returns the alpha component.
	 * 
	 * @return the alpha component value (0-255)
	 */
	@Override
	public int getAlpha() {
		return alpha;
	}

	/**
	 * Checks if this color is fully transparent.
	 * 
	 * @return true if alpha is 0, false otherwise
	 */
	public final boolean isTransparent() {
		return alpha == 0;
	}
}