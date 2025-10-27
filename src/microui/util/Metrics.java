package microui.util;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;

//Status: STABLE - Do not modify
//Last Reviewed: 26.09.2025
public final class Metrics {
	private static int totalCreatedObjects;
	private static final Map<String, Integer> metrics = new LinkedHashMap<String, Integer>();

	private Metrics() {
	}

	public static void register(Object object) {
		requireNonNull(object,"object");
		
		String name = object.getClass().getSimpleName();

		if (name.isEmpty()) {
			name = "Anonim class : " + object.getClass().getName();
		}

		metrics.put(name, metrics.getOrDefault(name, 0) + 1);
		
		totalCreatedObjects++;
	}

	public static void printAll() {
		if (metrics.isEmpty()) {
			return;
		}

		System.out.println("\n////////////////////");
		
		System.out.println("Total created objects : "+totalCreatedObjects);
		
		metrics.forEach((k, v) -> {
			System.out.println(k + " : " + v);
		});
		System.out.println("////////////////////\n");
	}

	public static void print(String className) {
		System.out.println(requireNonNull(className,"className") + " : " + metrics.getOrDefault(className, 0));
	}

	public static void clear() {
		metrics.clear();
	}

}
