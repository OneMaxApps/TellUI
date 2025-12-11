package microui.core.style;

import static java.util.Objects.requireNonNull;
import static microui.MicroUI.getContext;

import microui.util.Metrics;
import processing.core.PGraphics;

/**
 * Abstract base class for color representations in the MicroUI framework.
 * Provides common functionality for applying colors to different drawing contexts
 * and components. All color implementations should extend this class.
 * <p>
 * This class defines the standard interface for colors in MicroUI, including
 * methods for applying colors as fills, strokes, backgrounds, and tints to
 * both the main Processing context and PGraphics objects. It also integrates
 * with the Metrics system for resource tracking.
 * </p>
 * <p>
 * Status: STABLE - Do not modify
 * Last Reviewed: 27.10.2025
 * </p>
 * 
 * @see Color
 * @see PGraphics
 * @see Metrics
 */
public abstract class AbstractColor {
	/** Minimum allowed color component value. */
	public static final int MIN_VALUE = 0;
	/** Maximum allowed color component value. */
	public static final int MAX_VALUE = 255;

	/**
	 * Constructs an AbstractColor and registers it with the Metrics system.
	 */
	public AbstractColor() {
		super();

		Metrics.register(this);
	}

	/**
	 * Applies this color as a fill to the main Processing context.
	 */
	public final void apply() {
		preApply();
		getContext().fill(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Applies this color as a fill to the specified PGraphics context.
	 * 
	 * @param pGraphics the PGraphics context to apply the color to (cannot be null)
	 * @throws NullPointerException if pGraphics is null
	 */
	public final void apply(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.fill(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Applies this color as a stroke to the main Processing context.
	 */
	public final void applyStroke() {
		preApply();
		getContext().stroke(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Applies this color as a stroke to the specified PGraphics context.
	 * 
	 * @param pGraphics the PGraphics context to apply the color to (cannot be null)
	 * @throws NullPointerException if pGraphics is null
	 */
	public final void applyStroke(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.stroke(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Applies this color as a background to the main Processing context.
	 */
	public final void applyBackground() {
		preApply();
		getContext().background(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Applies this color as a background to the specified PGraphics context.
	 * 
	 * @param pGraphics the PGraphics context to apply the color to (cannot be null)
	 * @throws NullPointerException if pGraphics is null
	 */
	public final void applyBackground(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.background(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Applies this color as a tint to the main Processing context.
	 */
	public final void applyTint() {
		preApply();
		getContext().tint(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Applies this color as a tint to the specified PGraphics context.
	 * 
	 * @param pGraphics the PGraphics context to apply the color to (cannot be null)
	 * @throws NullPointerException if pGraphics is null
	 */
	public final void applyTint(PGraphics pGraphics) {
		requireNonNull(pGraphics, "pGraphics");

		preApply();
		pGraphics.tint(getRed(), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Returns the red component of this color.
	 * 
	 * @return the red component value (0-255)
	 */
	public abstract int getRed();

	/**
	 * Returns the green component of this color.
	 * 
	 * @return the green component value (0-255)
	 */
	public abstract int getGreen();

	/**
	 * Returns the blue component of this color.
	 * 
	 * @return the blue component value (0-255)
	 */
	public abstract int getBlue();

	/**
	 * Returns the alpha (transparency) component of this color.
	 * 
	 * @return the alpha component value (0-255, where 0 is fully transparent)
	 */
	public abstract int getAlpha();

	/**
	 * Hook method called before any color application.
	 * Subclasses can override this to perform setup or validation.
	 */
	protected void preApply() {

	}
	
}