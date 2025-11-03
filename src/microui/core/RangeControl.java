package microui.core;

import microui.core.base.Component;
import microui.core.interfaces.Scrollable;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;
import microui.util.BoundedValue;
import processing.event.MouseEvent;

public abstract class RangeControl extends Component implements Scrollable {
	private final BoundedValue value;
	private final Scrolling scrolling;
	private final Stroke stroke;

	public RangeControl(float x, float y, float width, float height) {
		super(x, y, width, height);

		value = new BoundedValue(0, 100, 0);
		scrolling = new Scrolling(this);
		stroke = new Stroke();
	}

	public final void setValue(float value) {
		this.value.set(value);
	}

	public final void setValue(float min, float max, float value) {
		this.value.set(min, max, value);
	}

	public final float getValue() {
		return value.get();
	}

	public final void setMinValue(float min) {
		this.value.setMin(min);
	}

	public final float getMinValue() {
		return value.getMin();
	}

	public final void setMaxValue(float max) {
		this.value.setMax(max);
	}

	public final float getMaxValue() {
		return value.getMax();
	}

	public final void setMinMaxValue(float min, float max) {
		this.value.setMinMax(min, max);
	}

	public final void appendValue(float value) {
		this.value.append(value);
	}

	public final void setScrollingVelocity(float velocity) {
		scrolling.setVelocity(velocity);
	}

	public final float getStrokeWeight() {
		return stroke.getWeight();
	}

	public final void setStrokeWeight(int weight) {
		stroke.setWeight(weight);
	}

	public final AbstractColor getStrokeColor() {
		return stroke.getColor();
	}

	public final void setStrokeColor(AbstractColor color) {
		stroke.setColor(color);
	}

	protected final boolean hasEqualMinMax() {
		return value.hasEqualMinMax();
	}

	protected final void setValueWithoutActions(float value) {
		this.value.setSilently(value);
	}

	protected final BoundedValue getMutableValue() {
		return value;

	}

	protected final Scrolling getMutableScrolling() {
		return scrolling;
	}

	protected final Stroke getMutableStroke() {
		return stroke;
	}

	protected final class Scrolling {
		private final Component component;
		private float speed, velocity;
		private boolean isScrolling, reverse;

		public Scrolling(Component component) {
			this.component = component;
			velocity = .01f;
		}

		public void init(final MouseEvent e) {
			if (reverse) {
				if (e.getCount() < 0) {
					if (speed > 0) {
						speed = 0;
					}
					speed += e.getCount() * velocity * 10;
				} else {
					if (speed < 0) {
						speed = 0;
					}
					speed += e.getCount() * velocity * 10;
				}
			} else {
				if (e.getCount() > 0) {
					if (speed > 0) {
						speed = 0;
					}
					speed -= e.getCount() * velocity * 10;
				} else {
					if (speed < 0) {
						speed = 0;
					}
					speed -= e.getCount() * velocity * 10;
				}
			}
		}

		public float get() {
			if (speed > .01f) {
				speed -= velocity;
			}
			if (speed < -.01f) {
				speed += velocity;
			}

			isScrolling = (speed < -.01f || speed > .01f);

			if (component.isDragging()) {
				speed = 0;
			}

			if (isScrolling) {
				return speed;
			}
			return 0f;
		}

		public boolean isScrolling() {
			return isScrolling;
		}

		public final float getVelocity() {
			return velocity;
		}

		public final void setVelocity(float velocity) {
			this.velocity = velocity;
		}

		public final boolean isReverse() {
			return reverse;
		}

		public final void setReverse(boolean reverse) {
			this.reverse = reverse;
		}

	}
}