package microui.util;

import static java.lang.Math.max;
import static java.lang.Math.min;

import microui.event.Listener;

// Status: STABLE - Do not modify
// Last reviewed: 20.09.2025.
public final class Value {
	private float min, max, value;
	private Listener onChangeValueListener;

	public Value(float min, float max, float value) {
		set(min, max, value);
	}

	public Value(float max) {
		this(0, max, 0);
	}

	public Value() {
		this(0, 100, 0);
	}

	public float getMin() {
		return min;
	}

	public Value setMin(float min) {
		if (min > max) {
			throw new IllegalStateException("min value cannot be greater than max value");
		}

		this.min = min;

		if (min > value) {
			value = min;
			onChangeValue();
		}

		return this;
	}

	public float getMax() {
		return max;
	}

	public Value setMax(float max) {
		if (max < min) {
			throw new IllegalStateException("max value cannot be lower than min value");
		}

		this.max = max;

		if (max < value) {
			value = max;
			onChangeValue();
		}

		return this;
	}

	public float get() {
		return value;
	}

	public Value set(float value) {
		if (this.value == value) {
			return this;
		}
		this.value = constrain(value, getEffectiveMin(), getEffectiveMax());
		onChangeValue();

		return this;
	}

	public Value set(float min, float max, float value) {
		setMin(min);
		setMax(max);
		set(value);

		return this;
	}

	public Value setMinMax(float min, float max) {
		setMin(min);
		setMax(max);

		return this;
	}

	public Value append(float append) {
		set(get() + append);

		return this;
	}

	public Value setSilently(float value) {
		this.value = value;

		return this;
	}

	public boolean hasEqualMinMax() {
		return min == max;
	}

	public Value onChangeValue() {
		if (onChangeValueListener != null) {
			onChangeValueListener.action();
		}

		return this;
	}

	public Value setOnChangeValueListener(Listener onChangeValueListener) {
		if (onChangeValueListener == null) {
			throw new NullPointerException("onChangeValueListener cannot be null");
		}
		this.onChangeValueListener = onChangeValueListener;

		return this;
	}

	public float getEffectiveMin() {
		return min(min, max);
	}

	public float getEffectiveMax() {
		return max(min, max);
	}

	public static float constrain(float value, float min, float max) {
		return value < min ? min : value > max ? max : value;
	}
}