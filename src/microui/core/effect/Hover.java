package microui.core.effect;

import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.base.Component;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.util.MathUtils;

//Status: STABLE - Do not modify
//Last Reviewed: 16.09.2025
public final class Hover extends View {
	private final Component component;
	private AbstractColor color;
	private float timer, timerMax, speed;
	private boolean isEnabled;

	public Hover(Component component) {
		super();
		setVisible(true);

		color = getTheme().getHoverColor();

		this.component = requireNonNull(component, "component cannot be null");

		timerMax = 100;

		setSpeed(10);

		setEnabled(true);
	}

	@Override
	public void draw() {
		if (!isEnabled) {
			return;
		}
		super.draw();
	}

	@Override
	protected void render() {
		ctx.noStroke();
		if (component.isHover()) {
			if (timer < timerMax) {
				timer += speed;
			}
		} else {
			if (timer > 0) {
				timer -= speed * 2;
			}
		}

		if (component.isPressed()) {
			ctx.fill(0, getAlpha());
		} else {
			ctx.fill(color.getRed(), color.getGreen(), color.getBlue(), getAlpha());
		}

		ctx.rect(component.getPadX(), component.getPadY(), component.getPadWidth(), component.getPadHeight());

	}

	public AbstractColor getColor() {
		return color;
	}

	public void setColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("the color cannot be null");
		}
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		if (speed <= 0) {
			throw new IllegalArgumentException("speed for hover animation cannot be less or equal to zero");
		}
		this.speed = speed;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	private float getAlpha() {
		return MathUtils.convert(timer, 0, timerMax, 0, color.getAlpha());
	}

}