package microui.util;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import microui.event.Listener;

// Status: STABLE - Do not modify
// Last reviewed: 03.11.2025.

public final class BoundedValue {
	private float min, max, value;
	private Listener onChangeValueListener;

	public BoundedValue(float min, float max, float value) {
		set(min, max, value);
	}

	public BoundedValue(float max) {
		this(0, max, 0);
	}

	public BoundedValue() {
		this(0, 100, 0);
	}

	public float getMin() {
		return min;
	}

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

	public float getMax() {
		return max;
	}

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

	public float get() {
		return value;
	}

	public BoundedValue set(float value) {
		if (this.value == value) {
			return this;
		}
		
		this.value = constrain(value, getMin(), getMax());
		
		onChangeValueListener();

		return this;
	}

	public BoundedValue set(float min, float max, float value) {
		setMin(min);
		setMax(max);
		set(value);

		return this;
	}

	public BoundedValue setMinMax(float min, float max) {
		setMin(min);
		setMax(max);

		return this;
	}

	public BoundedValue append(float append) {
		set(get() + append);

		return this;
	}

	public BoundedValue setSilently(float value) {
		this.value = value;

		return this;
	}

	public boolean hasEqualMinMax() {
		return min == max;
	}

	public BoundedValue setOnChangeValueListener(Listener onChangeValueListener) {
		this.onChangeValueListener = requireNonNull(onChangeValueListener,"onChangeValueListener");

		return this;
	}
	
	private void onChangeValueListener() {
		if (onChangeValueListener != null) {
			onChangeValueListener.action();
		}
	}
}