package microui.util;

public final class MathUtils {

	private MathUtils() {
	}
	
	public static final float constrain(float value, float min, float max) {
		return value < min ? min : value > max ? max : value;
	}
	
	public static final float map(float value, float min, float max, float newMin, float newMax) {
		return newMin+(newMin-newMax)*(value/(min(min,max)-max(min,max)));
	}
	
	public static final float abs(float value) {
		return value < 0 ? -value : value;
	}
	
	public static final float min(float valueFirst, float valueSecond) {
		return valueFirst < valueSecond ? valueFirst : valueSecond;
	}
	
	public static final float max(float valueFirst, float valueSecond) {
		return valueFirst > valueSecond ? valueFirst : valueSecond;
	}
}