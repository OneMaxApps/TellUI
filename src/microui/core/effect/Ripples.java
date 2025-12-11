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
 * Ripple effect animation that creates expanding circular waves from click/touch points.
 * Provides a visually appealing feedback effect for user interactions with components.
 * <p>
 * The Ripples effect creates circular waves that expand from the point of interaction,
 * fading out as they grow larger. It automatically handles graphics buffer management
 * and animation timing. The effect is triggered by clicks on the associated component.
 * </p>
 * <p>
 * Status: STABLE - Do not modify
 * Last Reviewed: 16.09.2025
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see Component
 * @see View
 * @see AbstractColor
 */
public final class Ripples extends View {
	/** Animation controller for managing ripple properties and rendering. */
	private final Animation animation;
	/** The component this ripple effect is attached to. */
	private final Component component;
	/** Whether the ripple effect is enabled. */
	private boolean isEnabled, isLaunched;
	/** Graphics buffer for rendering the ripple effect. */
	private PGraphics pg;

	/**
	 * Constructs a Ripples effect for the specified component.
	 * Initializes with default theme color and sets up click listener.
	 * 
	 * @param component the component to attach the ripple effect to (cannot be null)
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
	 * Draws the ripple effect if enabled.
	 * Manages the graphics buffer and delegates rendering to the animation controller.
	 */
	@Override
	public void draw() {
		if (!isEnabled()) {
			return;
		}
		super.draw();
	}

	/**
	 * Renders the ripple effect animation.
	 * Draws the ripple animation to a graphics buffer and composites it onto the component.
	 */
	@Override
	protected void render() {
		// Skip rendering if no graphics buffer or animation not launched
		if (pg == null || !isLaunched()) {
			return;
		}

		// Draw ripple animation to graphics buffer
		pg.beginDraw();
		pg.clear();
		pg.pushStyle();
		animation.render(pg);
		pg.popStyle();
		pg.endDraw();

		// Composite graphics buffer onto component
		ctx.image(pg, (int) component.getPadX(), (int) component.getPadY(), (int) component.getPadWidth(),
				(int) component.getPadHeight());
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
	 * Launches a new ripple animation from the current mouse position.
	 * Resets animation state and prepares the starting position.
	 */
	public void launch() {
		isLaunched = true;
		animation.resetState();
		animation.preparePosition();
	}

	/**
	 * Creates a new graphics buffer sized to the component's padded area.
	 * Registers the buffer with the metrics system for resource tracking.
	 */
	private void createGraphics() {
		pg = ctx.createGraphics((int) max(1, component.getPadWidth()), (int) max(1, component.getPadHeight()),
				ctx.sketchRenderer());
		Metrics.register(pg);
	}

	/**
	 * Checks if the component has been resized since the graphics buffer was created.
	 * 
	 * @return true if component size differs from buffer size, false otherwise
	 */
	private boolean isComponentResized() {
		return pg.width != (int) component.getPadWidth() || pg.height != (int) component.getPadHeight();
	}

	/**
	 * Initializes click listener on the component to trigger ripple animations.
	 * Manages graphics buffer creation and resizing when needed.
	 */
	private void initCallbackForComponent() {
		component.onClick(() -> {
			// Create graphics buffer if it doesn't exist
			if (pg == null) {
				createGraphics();
				animation.recalculateMaxRadius();
			}

			// Recreate buffer if component was resized
			if (isComponentResized()) {
				createGraphics();
				animation.recalculateMaxRadius();
			}

			// Launch new ripple animation
			launch();
		});
	}

	/**
	 * Internal class managing the ripple animation properties and rendering logic.
	 */
	private final class Animation {
		/** Color of the ripple effect. */
		private AbstractColor color;
		/** Maximum radius the ripple can expand to. */
		private float maxRadius;
		/** Starting position of the ripple (relative to component). */
		private int startX, startY, radius;
		/** Whether the starting position has been prepared. */
		private boolean isPositionPrepared;

		/**
		 * Constructs an Animation with default theme color.
		 */
		Animation() {
			color = getTheme().getRipplesColor();
		}

		/**
		 * Renders the ripple animation to the specified graphics context.
		 * 
		 * @param pg the graphics context to render to
		 */
		void render(PGraphics pg) {
			if (!isLaunched) {
				return;
			}

			// Configure drawing style
			pg.noStroke();
			color.apply(pg);
			// Fade out alpha as radius increases
			pg.fill(color.getRed(), color.getGreen(), color.getBlue(),
					max(0, 190 - MathUtils.convert(radius, 0, maxRadius, 0, 190)));
			
			// Draw expanding circle
			pg.circle(startX, startY, radius);

			// Increase radius with dynamic speed
			radius += getSpeed();

			// Complete animation when max radius reached
			if (radius > maxRadius) {
				complete();
			}
		}

		/**
		 * Returns the current ripple color.
		 * 
		 * @return the ripple color
		 */
		AbstractColor getColor() {
			return color;
		}

		/**
		 * Sets the ripple color.
		 * 
		 * @param color the color to use for ripples (cannot be null)
		 * @throws NullPointerException if color is null
		 */
		void setColor(AbstractColor color) {
			this.color = requireNonNull(color,"color");
		}

		/**
		 * Prepares the starting position based on current mouse position relative to component.
		 */
		void preparePosition() {
			if (!isPositionPrepared) {
				startX = (int) (ctx.mouseX - component.getPadX());
				startY = (int) (ctx.mouseY - component.getPadY());
				isPositionPrepared = true;
			}
		}

		/**
		 * Resets the animation state to initial values.
		 */
		void resetState() {
			startX = startY = -1;
			isPositionPrepared = false;
			radius = 0;
		}

		/**
		 * Recalculates the maximum radius based on component dimensions.
		 * Ensures ripple expands to cover the entire component area.
		 */
		void recalculateMaxRadius() {
			maxRadius = MathUtils.dist(0, 0, component.getPadWidth(), component.getPadHeight()) * 2;
		}

		/**
		 * Completes the animation and resets launch state.
		 */
		void complete() {
			resetState();
			isLaunched = false;
		}

		/**
		 * Calculates the current animation speed.
		 * Speed increases as radius grows for a more dynamic effect.
		 * 
		 * @return the current animation speed
		 */
		float getSpeed() {
			return ((maxRadius + (radius * .2f)) * .04f);
		}
	}
}