package microui.core.effect;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.base.Component;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.util.Environment;
import microui.util.MathUtils;

/**
 * Visual hover effect for Component elements with smooth fade-in/fade-out
 * animation. Provides a translucent overlay that appears when the component is
 * hovered over, with separate visual states for hover and press interactions.
 * 
 * @see Component
 * @see View
 * @see AbstractColor
 */
public final class Hover extends View {
	private final Component component;
	private AbstractColor color;
	private float timer, timerMax, speed;
	private boolean enabled;

	/**
	 * Constructs a Hover effect for the specified component. Initializes with
	 * default theme color and animation parameters.
	 * 
	 * @param component the component to attach the hover effect to (cannot be null)
	 * @throws NullPointerException if component is null
	 */
	public Hover(Component component) {
		super();
		setVisible(true);

		color = getTheme().getHoverColor();

		this.component = requireNonNull(component, "component");

		timerMax = 100;

		setSpeed(10);

		setEnabled(true);
	}

	/**
	 * Draws the hover effect if enabled. Calls the parent draw method which handles
	 * the rendering.
	 */
	@Override
	public void draw() {
		if (!enabled) {
			return;
		}
		super.draw();
	}

	/**
	 * Renders the hover effect animation. Handles fade-in on hover, fade-out on
	 * hover end, and visual feedback for press state.
	 */
	@Override
	protected void render() {
		final boolean onAndroid = Environment.isAndroid();
		
		ctx.noStroke();

		if (component.isHovered() && ((component.isPressed() && onAndroid) || !onAndroid)) {
			if (timer < timerMax) {
				timer += speed;
			}
		} else {
			if (timer > 0) {
				timer -= speed * 2;
			}
		}
		
		if (onAndroid) {
			ctx.fill(color.getRed(), color.getGreen(), color.getBlue(), getAlpha());
		} else {
			if (component.isPressed()) {
				ctx.fill(0, getAlpha());
			} else {
				ctx.fill(color.getRed(), color.getGreen(), color.getBlue(), getAlpha());
			}
		}
		
		if ((onAndroid && component.isPressed()) || !onAndroid) {
			ctx.rect(component.getPadX(), component.getPadY(), component.getPadWidth(), component.getPadHeight());
		}
		
	}

	/**
	 * Returns the current hover effect color.
	 * 
	 * @return the hover effect color
	 */
	public AbstractColor getColor() {
		return color;
	}

	/**
	 * Sets the hover effect color.
	 * 
	 * @param color the color to use for the hover effect (cannot be null)
	 * @throws NullPointerException if color is null
	 */
	public void setColor(AbstractColor color) {
		this.color = requireNonNull(color, "color");
	}

	/**
	 * Returns the current animation speed.
	 * 
	 * @return the animation speed value
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Sets the animation speed for the hover effect.
	 * 
	 * @param speed the animation speed (must be greater than 0)
	 * @throws IllegalArgumentException if speed is less than or equal to 0
	 */
	public void setSpeed(float speed) {
		if (speed <= 0) {
			throw new IllegalArgumentException("Speed for hover animation cannot be less or equal to zero");
		}
		this.speed = speed;
	}

	/**
	 * Checks if the hover effect is enabled.
	 * 
	 * @return true if enabled, false if disabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables the hover effect.
	 * 
	 * @param enabled true to enable the hover effect, false to disable
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private float getAlpha() {
		return MathUtils.convert(timer, 0, timerMax, 0, color.getAlpha());
	}

}