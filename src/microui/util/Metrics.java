package microui.util;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Metrics collection utility for tracking object creation statistics.
 * 
 * <p>
 * Provides a simple way to monitor and count instances of different object
 * types in the application. Useful for debugging memory usage and object
 * life-cycle.
 * </p>
 */
public final class Metrics {
	private static int totalCreatedObjects;
	private static final Map<String, Integer> metrics = new LinkedHashMap<String, Integer>();

	private Metrics() {
	}

	/**
	 * Registers an object creation in the metrics.
	 * 
	 * <p>
	 * Increments the count for the object's class type. If the class is anonymous,
	 * uses a descriptive name including the full class name.
	 * </p>
	 * 
	 * @param object the object to register, cannot be null
	 * @throws NullPointerException if the object is null
	 */
	public static void register(Object object) {
		requireNonNull(object, "object");

		String name = object.getClass().getSimpleName();

		if (name.isEmpty()) {
			name = "Anonymous class : " + object.getClass().getName();
		}

		metrics.put(name, metrics.getOrDefault(name, 0) + 1);

		totalCreatedObjects++;
	}

	/**
	 * Prints all collected metrics to the console.
	 * 
	 * <p>
	 * Displays the total number of created objects followed by a breakdown by class
	 * name. If no objects have been registered, does nothing.
	 * </p>
	 */
	public static void printAll() {
		if (metrics.isEmpty()) {
			return;
		}
		
		System.out.println("\n////////////////////");

		System.out.println("Total created objects : " + totalCreatedObjects);

		metrics.forEach((k, v) -> {
			System.out.println(k + " : " + v);
		});
		System.out.println("////////////////////\n");
	}

	/**
	 * Prints the count for a specific class name.
	 * 
	 * @param className the class name to query, cannot be null
	 * @throws NullPointerException if className is null
	 */
	public static void print(String className) {
		System.out.println(requireNonNull(className, "className") + " : " + metrics.getOrDefault(className, 0));
	}

	/**
	 * Clears all collected metrics.
	 * 
	 * <p>
	 * Resets both the per-class counts and the total object count to zero.
	 * </p>
	 */
	public static void clear() {
		metrics.clear();
		totalCreatedObjects = 0;
	}
}