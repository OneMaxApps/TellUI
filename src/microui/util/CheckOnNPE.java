package microui.util;

public final class CheckOnNPE {

	private CheckOnNPE() {
		
	}
	
	public static void checkParam(Object obj, String name) {
		if (obj == null) {
			throw new NullPointerException(String.format("Parameter: \"%s\" cannot be null",name));
		}
	}
	
	public static void check(Object obj, String name) {
		if (obj == null) {
			throw new NullPointerException(String.format("Object \"%s\" cannot be null",name));
		}
	}
}