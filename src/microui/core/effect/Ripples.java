package microui.core.effect;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static microui.core.style.theme.ThemeManager.getTheme;

import microui.core.base.Component;
import microui.core.base.View;
import microui.core.style.AbstractColor;
import microui.util.MathUtils;
import microui.util.Metrics;
import processing.core.PGraphics;

/**
 * Ripple effect animation that creates expanding circular waves from
 * click/touch points. Provides a visually appealing feedback effect for user
 * interactions with components.
 * <p>
 * The Ripples effect creates circular waves that expand from the point of
 * interaction, fading out as they grow larger. It automatically handles
 * graphics buffer management and animation timing. The effect is triggered by
 * clicks on the associated component.
 * </p>
 * <p>
 * Status: STABLE - Do not modify Last Reviewed: 16.09.2025
 * </p>
 * 
 * @see Component
 * @see View
 * @see AbstractColor
 */
public final class Ripples extends View {
	private final Animation animation;
	private final Component component;
	private boolean isEnabled, isLaunched;
	private PGraphics pg;

	/**
	 * Constructs a Ripples effect for the specified component. Initializes with
	 * default theme color and sets up click listener.
	 * 
	 * @param component the component to attach the ripple effect to (cannot be
	 *                  null)
	 * @throws NullPointerException if component is null
	 */
	public Ripples(Component component) {
		super();
		setVisible(true);

		animation = new Animation();

		this.component = requireNonNull(component, "component");
		initCallbackForComponent();

		setEnabled(true);
	}

	/**
	 * Draws the ripple effect if enabled. Manages the graphics buffer and delegates
	 * rendering to the animation controller.
	 */
	@Override
	public void draw() {
		if (!isEnabled()) {
			return;
		}
		super.draw();
	}

	/**
	 * Checks if the ripple effect is enabled.
	 * 
	 * @return true if enabled, false if disabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Returns the current ripple color.
	 * 
	 * @return the ripple color
	 */
	public AbstractColor getColor() {
		return animation.getColor();
	}

	/**
	 * Sets the ripple color.
	 * 
	 * @param color the color to use for ripples (cannot be null)
	 * @throws NullPointerException if color is null
	 */
	public void setColor(AbstractColor color) {
		animation.setColor(color);
	}

	/**
	 * Enables or disables the ripple effect.
	 * 
	 * @param isEnabled true to enable the ripple effect, false to disable
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Checks if a ripple animation is currently running.
	 * 
	 * @return true if a ripple is animating, false otherwise
	 */
	public boolean isLaunched() {
		return isLaunched;
	}

	/**
	 * Launches a new ripple animation from the current mouse position. Resets
	 * animation state and prepares the starting position.
	 */
	public void launch() {
		isLaunched = true;
		animation.resetState();
		animation.preparePosition();
	}

	/**
	 * Renders the ripple effect animation. Draws the ripple animation to a graphics
	 * buffer and composites it onto the component.
	 */
	@Override
	protected void render() {
		if (pg == null || !isLaunched()) {
			return;
		}

		pg.beginDraw();
		pg.clear();
		pg.pushStyle();
		animation.render(pg);
		pg.popStyle();
		pg.endDraw();

		ctx.image(pg, (int) component.getPadX(), (int) component.getPadY(), (int) component.getPadWidth(),
				(int) component.getPadHeight());
	}

	private void createGraphics() {
		pg = ctx.createGraphics((int) max(1, component.getPadWidth()), (int) max(1, component.getPadHeight()),
				ctx.sketchRenderer());
		Metrics.register(pg);
	}

	private boolean isComponentResized() {
		return pg.width != (int) component.getPadWidth() || pg.height != (int) component.getPadHeight();
	}

	private void initCallbackForComponent() {
		component.onClick(() -> {
			if (pg == null) {
				createGraphics();
				animation.recalculateMaxRadius();
			}

			if (isComponentResized()) {
				createGraphics();
				animation.recalculateMaxRadius();
			}

			launch();
		});
	}

	private final class Animation {
		private AbstractColor color;
		private float maxRadius;
		private int startX, startY, radius;
		private boolean isPositionPrepared;

		Animation() {
			color = getTheme().getRipplesColor();
		}

		void render(PGraphics pg) {
			if (!isLaunched) {
				return;
			}

			pg.noStroke();
			color.apply(pg);
			pg.fill(color.getRed(), color.getGreen(), color.getBlue(),
					max(0, 190 - MathUtils.convert(radius, 0, maxRadius, 0, 190)));

			pg.circle(startX, startY, radius);

			radius += getSpeed();

			if (radius > maxRadius) {
				complete();
			}
		}

		AbstractColor getColor() {
			return color;
		}

		void setColor(AbstractColor color) {
			this.color = requireNonNull(color, "color");
		}

		void preparePosition() {
			if (!isPositionPrepared) {
				startX = (int) (ctx.mouseX - component.getPadX());
				startY = (int) (ctx.mouseY - component.getPadY());
				isPositionPrepared = true;
			}
		}

		void resetState() {
			startX = startY = -1;
			isPositionPrepared = false;
			radius = 0;
		}

		void recalculateMaxRadius() {
			maxRadius = MathUtils.dist(0, 0, component.getPadWidth(), component.getPadHeight()) * 2;
		}

		void complete() {
			resetState();
			isLaunched = false;
		}

		float getSpeed() {
			return ((maxRadius + (radius * .2f)) * .04f);
		}
	}
}