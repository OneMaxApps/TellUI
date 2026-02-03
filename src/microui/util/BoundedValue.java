package microui.util;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import microui.event.Listener;

/**
 * Represents a numeric value bounded between minimum and maximum limits.
 * 
 * <p>
 * This class provides a way to store and manipulate values that must stay
 * within a defined range. It includes change listeners and ensures values
 * remain within bounds through automatic constraint.
 * </p>
 * 
 * <p>
 * Status: STABLE - Do not modify
 * </p>
 * <p>
 * Last reviewed: 03.11.2025
 * </p>
 */
public final class BoundedValue {
	private float min;
	private float max;
	private float value;
	private Listener onChangeValueListener;

	/**
	 * Constructs a BoundedValue with specified minimum, maximum, and initial value.
	 * 
	 * @param min   the minimum allowed value (inclusive)
	 * @param max   the maximum allowed value (inclusive)
	 * @param value the initial value (will be constrained to min-max range)
	 * 
	 * @throws IllegalArgumentException if min is greater than max
	 */
	public BoundedValue(float min, float max, float value) {
		set(min, max, value);
	}

	/**
	 * Constructs a BoundedValue with minimum of 0, specified maximum, and initial
	 * value of 0.
	 * 
	 * @param max the maximum allowed value (inclusive)
	 */
	public BoundedValue(float max) {
		this(0, max, 0);
	}

	/**
	 * Constructs a BoundedValue with default range 0-100 and initial value of 0.
	 */
	public BoundedValue() {
		this(0, 100, 0);
	}

	/**
	 * Returns the minimum allowed value.
	 * 
	 * @return the minimum value
	 */
	public float getMin() {
		return min;
	}

	/**
	 * Sets the minimum allowed value.
	 * 
	 * <p>
	 * If the new minimum is greater than the current maximum, an exception is
	 * thrown. If the new minimum is greater than the current value, the value is
	 * adjusted to the new minimum and change listeners are notified.
	 * </p>
	 * 
	 * @param min the new minimum value
	 * @return this BoundedValue for method chaining
	 * 
	 * @throws IllegalArgumentException if min is greater than current max
	 */
	public BoundedValue setMin(float min) {
		if (min > max) {
			throw new IllegalArgumentException("Min value cannot be greater than max value");
		}

		this.min = min;

		if (min > value) {
			value = min;
			onChangeValueListener();
		}

		return this;
	}

	/**
	 * Returns the maximum allowed value.
	 * 
	 * @return the maximum value
	 */
	public float getMax() {
		return max;
	}

	/**
	 * Sets the maximum allowed value.
	 * 
	 * <p>
	 * If the new maximum is less than the current minimum, an exception is thrown.
	 * If the new maximum is less than the current value, the value is adjusted to
	 * the new maximum and change listeners are notified.
	 * </p>
	 * 
	 * @param max the new maximum value
	 * @return this BoundedValue for method chaining
	 * 
	 * @throws IllegalArgumentException if max is less than current min
	 */
	public BoundedValue setMax(float max) {
		if (max < min) {
			throw new IllegalArgumentException("Max value cannot be lower than min value");
		}

		this.max = max;

		if (max < value) {
			value = max;
			onChangeValueListener();
		}

		return this;
	}

	/**
	 * Returns the current value.
	 * 
	 * @return the current value (guaranteed to be between min and max inclusive)
	 */
	public float get() {
		return value;
	}

	/**
	 * Sets the current value.
	 * 
	 * <p>
	 * The value is constrained to the current min-max range. If the value actually
	 * changes (after constraint), change listeners are notified.
	 * </p>
	 * 
	 * @param value the new value
	 * @return this BoundedValue for method chaining
	 */
	public BoundedValue set(float value) {
		if (this.value == value) {
			return this;
		}

		this.value = constrain(value, getMin(), getMax());

		onChangeValueListener();

		return this;
	}

	/**
	 * Sets all parameters (min, max, and value) at once.
	 * 
	 * <p>
	 * This is more efficient than setting each parameter individually as it only
	 * triggers change listeners once if needed.
	 * </p>
	 * 
	 * @param min   the new minimum value
	 * @param max   the new maximum value
	 * @param value the new value
	 * @return this BoundedValue for method chaining
	 * 
	 * @throws IllegalArgumentException if min is greater than max
	 */
	public BoundedValue set(float min, float max, float value) {
		setMin(min);
		setMax(max);
		set(value);

		return this;
	}

	/**
	 * Sets only the minimum and maximum values, preserving the current value
	 * (adjusted if it falls outside the new range).
	 * 
	 * @param min the new minimum value
	 * @param max the new maximum value
	 * @return this BoundedValue for method chaining
	 * 
	 * @throws IllegalArgumentException if min is greater than max
	 */
	public BoundedValue setMinMax(float min, float max) {
		setMin(min);
		setMax(max);

		return this;
	}

	/**
	 * Appends a value to the current value.
	 * 
	 * <p>
	 * Equivalent to {@code set(get() + append)}.
	 * </p>
	 * 
	 * @param append the value to add to the current value
	 * @return this BoundedValue for method chaining
	 */
	public BoundedValue append(float append) {
		set(get() + append);

		return this;
	}

	/**
	 * Sets the value without triggering change listeners or constraints.
	 * 
	 * <p>
	 * Warning: This method bypasses the min-max constraint and does not notify
	 * change listeners. Use with caution.
	 * </p>
	 * 
	 * @param value the new value
	 * @return this BoundedValue for method chaining
	 */
	public BoundedValue setSilently(float value) {
		this.value = value;

		return this;
	}

	/**
	 * Checks if the minimum and maximum values are equal.
	 * 
	 * @return true if min == max, false otherwise
	 */
	public boolean hasEqualMinMax() {
		return min == max;
	}

	/**
	 * Sets the listener to be notified when the value changes.
	 * 
	 * @param onChangeValueListener the listener to notify on value changes, cannot
	 *                              be null
	 * @return this BoundedValue for method chaining
	 * 
	 * @throws NullPointerException if the listener is null
	 */
	public BoundedValue setOnChangeValueListener(Listener onChangeValueListener) {
		this.onChangeValueListener = requireNonNull(onChangeValueListener, "onChangeValueListener");

		return this;
	}

	private void onChangeValueListener() {
		if (onChangeValueListener != null) {
			onChangeValueListener.action();
		}
	}
}