package microui.util;

import java.util.LinkedHashMap;
import java.util.Map;

//Status: STABLE - Do not modify
//Last Reviewed: 26.09.2025
public final class Metrics {
	private static final Map<String, Integer> metrics = new LinkedHashMap<String, Integer>();

	private Metrics() {
	}

	public static void register(Object object) {
		if (object == null) {
			throw new NullPointerException("object for registration in Metrics cannot be null");
		}
		String name = object.getClass().getSimpleName();

		if (name.isEmpty()) {
			name = "Anonim class: " + object.getClass().getName();
		}

		metrics.put(name, metrics.getOrDefault(name, 0) + 1);
	}

	public static void printAll() {
		if (metrics.isEmpty()) {
			return;
		}

		System.out.println("\n////////////////////");
		metrics.forEach((k, v) -> {
			System.out.println(k + " : " + v);
		});
		System.out.println("////////////////////\n");
	}

	public static void print(String className) {

		if (className == null) {
			throw new NullPointerException("the className cannot be null");
		}

		System.out.println(className + " : " + metrics.getOrDefault(className, 0));
	}

	public static void clear() {
		metrics.clear();
	}

}
