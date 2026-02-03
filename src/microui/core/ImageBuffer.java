package microui.core;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import processing.core.PImage;

/**
 * A spatial view component for displaying and manipulating images with color
 * tinting. ImageBuffer provides functionality for loading, displaying, and
 * applying color tints to PImage objects, with automatic dimension clamping to
 * prevent oversized images.
 * 
 * <p>
 * <strong>Status:</strong> Stable - Do not modify
 * </p>
 * <p>
 * <strong>Last Reviewed:</strong> 13.09.2025
 * </p>
 * 
 * @see SpatialView
 * @see AbstractColor
 */
public class ImageBuffer extends SpatialView {
	private AbstractColor color;
	private PImage image;

	/**
	 * Constructs an ImageBuffer with default properties. The buffer is initially
	 * visible with white tint color and no loaded image.
	 */
	public ImageBuffer() {
		setVisible(true);
		color = Color.WHITE;
	}

	/**
	 * Draws the image buffer if an image is loaded. Overrides the parent draw
	 * method to check for loaded image before rendering.
	 */
	@Override
	public void draw() {
		if (isLoaded()) {
			super.draw();
		}
	}

	/**
	 * Checks if an image is currently loaded in the buffer.
	 *
	 * @return true if an image is loaded, false otherwise
	 */
	public final boolean isLoaded() {
		return image != null;
	}

	/**
	 * Sets the image directly from a PImage object. Clamps the image dimensions to
	 * prevent oversized images.
	 *
	 * @param image the PImage to set
	 * @throws NullPointerException if image is null
	 */
	public final void set(final PImage image) {
		this.image = requireNonNull(image, "image");
		clampDimensions(image);
	}

	/**
	 * Loads an image from a file path. Clamps the image dimensions to prevent
	 * oversized images.
	 *
	 * @param path the file path to load the image from
	 * @throws NullPointerException if path is null
	 */
	public final void load(final String path) {
		image = ctx.loadImage(requireNonNull(path, "path"));
		clampDimensions(image);
	}

	/**
	 * Gets the currently loaded image.
	 *
	 * @return the loaded PImage, or null if no image is loaded
	 */
	public final PImage get() {
		return image;
	}

	/**
	 * Gets the current tint color applied to the image.
	 *
	 * @return the current tint color
	 */
	public final AbstractColor getColor() {
		return color;
	}

	/**
	 * Sets the tint color to apply to the image. The tint color is multiplied with
	 * the image colors during rendering.
	 *
	 * @param color the tint color to apply
	 * @throws NullPointerException if color is null
	 */
	public final void setColor(AbstractColor color) {
		this.color = requireNonNull(color, "color");
	}

	/**
	 * Removes the currently loaded image texture. After calling this, isLoaded()
	 * will return false until a new image is set.
	 */
	public final void removeTexture() {
		image = null;
	}

	/**
	 * Renders the image with applied color tint at the buffer's position and
	 * dimensions. Only called when an image is loaded.
	 */
	@Override
	protected void render() {
		if (isLoaded()) {
			ctx.pushStyle();
			color.applyTint();
			ctx.image(image, (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
			ctx.popStyle();
		}
	}

	private static final void clampDimensions(PImage image) {
		if (image.width > ctx.width || image.height > ctx.height) {
			image.resize((int) max(1, ctx.width), (int) max(1, ctx.height));
		}
	}
}