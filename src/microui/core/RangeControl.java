package microui.core;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import microui.core.base.Component;
import microui.core.interfaces.Scrollable;
import microui.core.interfaces.ValuePreviewSource;
import microui.core.style.AbstractColor;
import microui.core.style.Stroke;
import microui.service.ValueOverlayManager;
import microui.util.BoundedValue;
import processing.event.MouseEvent;

/**
 * Abstract base class for range control components that allow selecting a
 * numeric value within a specified range.
 * 
 * @see Component
 * @see Scrollable
 * @see BoundedValue
 */
public abstract class RangeControl extends Component implements Scrollable, ValuePreviewSource{
	private final BoundedValue value;
	private final Scrolling scrolling;
	private final Stroke stroke;
	private Supplier<String> supplierOverlayText;

	/**
	 * Constructs a RangeControl with specified position and dimensions. Initializes
	 * with default value range (0-100) at 0, and creates scrolling and stroke
	 * objects.
	 *
	 * @param x      the x-coordinate of the control's top-left corner
	 * @param y      the y-coordinate of the control's top-left corner
	 * @param width  the width of the control
	 * @param height the height of the control
	 */
	public RangeControl(float x, float y, float width, float height) {
		super(x, y, width, height);

		value = new BoundedValue(0, 100, 0);
		scrolling = new Scrolling(this);
		stroke = new Stroke();
	}
	
	public void random() {
		setValue(ctx.random(getMinValue(),getMaxValue()));
	}

	public Supplier<String> getSupplierOverlayText() {
		return supplierOverlayText;
	}


	public void setSupplierOverlayText(Supplier<String> supplierOverlayText) {
		this.supplierOverlayText = requireNonNull(supplierOverlayText);
	}

	/**
	 * Sets the current value of the range control.
	 *
	 * @param value the value to set (will be constrained to min/max range)
	 */
	public final void setValue(float value) {
		this.value.set(value);
	}

	/**
	 * Sets the range and current value of the control.
	 *
	 * @param min   the minimum value of the range
	 * @param max   the maximum value of the range
	 * @param value the initial value to set (will be constrained to min/max range)
	 */
	public final void setValue(float min, float max, float value) {
		this.value.set(min, max, value);
	}

	/**
	 * Gets the current value of the range control.
	 *
	 * @return the current value
	 */
	public final float getValue() {
		return value.get();
	}

	/**
	 * Sets the minimum value of the range.
	 *
	 * @param min the minimum value to set
	 */
	public final void setMinValue(float min) {
		this.value.setMin(min);
	}

	/**
	 * Gets the minimum value of the range.
	 *
	 * @return the current minimum value
	 */
	public final float getMinValue() {
		return value.getMin();
	}

	/**
	 * Sets the maximum value of the range.
	 *
	 * @param max the maximum value to set
	 */
	public final void setMaxValue(float max) {
		this.value.setMax(max);
	}

	/**
	 * Gets the maximum value of the range.
	 *
	 * @return the current maximum value
	 */
	public final float getMaxValue() {
		return value.getMax();
	}

	/**
	 * Sets both the minimum and maximum values of the range.
	 *
	 * @param min the minimum value to set
	 * @param max the maximum value to set
	 */
	public final void setMinMaxValue(float min, float max) {
		this.value.setMinMax(min, max);
	}

	/**
	 * Appends (adds) a value to the current value. The result will be constrained
	 * to the min/max range.
	 *
	 * @param value the value to append
	 */
	public final void appendValue(float value) {
		this.value.append(value);
	}

	/**
	 * Sets the scrolling velocity (inertia decay rate). Higher values cause
	 * scrolling to stop faster.
	 *
	 * @param velocity the scrolling velocity to set
	 */
	public final void setScrollingVelocity(float velocity) {
		scrolling.setVelocity(velocity);
	}

	/**
	 * Gets the stroke weight (border thickness) of the control.
	 *
	 * @return the current stroke weight
	 */
	public final float getStrokeWeight() {
		return stroke.getWeight();
	}

	/**
	 * Sets the stroke weight (border thickness) of the control.
	 *
	 * @param weight the stroke weight to set
	 */
	public final void setStrokeWeight(float weight) {
		stroke.setWeight(weight);
	}

	/**
	 * Gets the stroke color (border color) of the control.
	 *
	 * @return the current stroke color
	 */
	public final AbstractColor getStrokeColor() {
		return stroke.getColor();
	}

	/**
	 * Sets the stroke color (border color) of the control.
	 *
	 * @param color the stroke color to set
	 */
	public final void setStrokeColor(AbstractColor color) {
		stroke.setColor(color);
	}
	
	@Override
	public String getSource() {
		if (getSupplierOverlayText() == null) {
			return String.valueOf((int) getValue());
		}
		
		return getSupplierOverlayText().get();
	}


	@Override
	protected void render() {
		if (isContentPrepared()) {
			ValueOverlayManager.getInstance().setSource(this);
		}
	}

	/**
	 * Checks if the minimum and maximum values are equal. Useful for detecting
	 * degenerate ranges where no selection is possible.
	 *
	 * @return true if min == max, false otherwise
	 */
	protected final boolean hasEqualMinMax() {
		return value.hasEqualMinMax();
	}

	/**
	 * Sets the value without triggering any change listeners. Useful for
	 * programmatic updates that shouldn't fire events.
	 *
	 * @param value the value to set silently
	 */
	protected final void setValueWithoutActions(float value) {
		this.value.setSilently(value);
	}

	/**
	 * Gets the internal BoundedValue instance for internal manipulation.
	 *
	 * @return the internal BoundedValue instance
	 */
	protected final BoundedValue getInternalValue() {
		return value;
	}

	/**
	 * Gets the internal Scrolling instance for internal manipulation.
	 *
	 * @return the internal Scrolling instance
	 */
	protected final Scrolling getInternalScrolling() {
		return scrolling;
	}

	/**
	 * Gets the internal Stroke instance for internal manipulation.
	 *
	 * @return the internal Stroke instance
	 */
	protected final Stroke getInternalStroke() {
		return stroke;
	}

	/**
	 * Internal class for handling scrolling inertia and mouse wheel interaction.
	 * Provides smooth scrolling with configurable velocity and reverse scrolling
	 * options.
	 */
	protected final class Scrolling {
		private final Component component;
		private float speed, velocity;
		private boolean isScrolling, reverse;

		/**
		 * Constructs a Scrolling instance for the parent component.
		 *
		 * @param component the parent component that owns this scrolling
		 */
		public Scrolling(Component component) {
			this.component = component;
			velocity = .01f;
		}

		/**
		 * Initializes scrolling based on a mouse wheel event. Accumulates speed based
		 * on the wheel direction and count.
		 *
		 * @param e the mouse wheel event
		 */
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

		/**
		 * Gets the current scrolling delta and updates scrolling state. Applies
		 * velocity-based decay to the scrolling speed.
		 *
		 * @return the current scrolling delta, or 0 if not scrolling
		 */
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

		/**
		 * Checks if scrolling is currently active.
		 *
		 * @return true if scrolling is active, false otherwise
		 */
		public boolean isScrolling() {
			return isScrolling;
		}

		/**
		 * Gets the scrolling velocity (decay rate).
		 *
		 * @return the current scrolling velocity
		 */
		public final float getVelocity() {
			return velocity;
		}

		/**
		 * Sets the scrolling velocity (decay rate). Higher values cause scrolling to
		 * stop faster.
		 *
		 * @param velocity the scrolling velocity to set
		 */
		public final void setVelocity(float velocity) {
			this.velocity = velocity;
		}

		/**
		 * Checks if scrolling direction is reversed.
		 *
		 * @return true if scrolling is reversed, false for normal scrolling
		 */
		public final boolean isReverse() {
			return reverse;
		}

		/**
		 * Sets whether scrolling direction is reversed.
		 *
		 * @param reverse true to reverse scrolling direction, false for normal
		 *                scrolling
		 */
		public final void setReverse(boolean reverse) {
			this.reverse = reverse;
		}
	}
}