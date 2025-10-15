package microui.core;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;
import microui.core.style.AbstractColor;
import microui.core.style.Color;
import processing.core.PImage;

// Status: Stable - Do not modify
// Last Reviewed: 13.09.2025
public class ImageBuffer extends SpatialView {
	private AbstractColor color;
	private PImage image;

	public ImageBuffer() {
		setVisible(true);
		color = Color.WHITE;
	}

	@Override
	public void draw() {
		if (isLoaded()) {
			super.draw();
		}
	}

	@Override
	protected void render() {
		if (isLoaded()) {
			ctx.pushStyle();
			color.applyTint();
			ctx.image(image, (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
			ctx.popStyle();
		}
	}

	public final boolean isLoaded() {
		return image != null;
	}

	public final void set(final PImage image) {
		this.image = requireNonNull(image, "image cannot be null");
		updateDimensionsOfImageCorrect(image);
	}

	public final void load(final String path) {
		image = ctx.loadImage(requireNonNull(path, "path cannot be null"));
		updateDimensionsOfImageCorrect(image);
	}

	public final PImage get() {
		return image;
	}

	public final AbstractColor getColor() {
		return color;
	}

	public final void setColor(AbstractColor color) {
		if (color == null) {
			throw new NullPointerException("the color cannot be null");
		}

		this.color = color;

	}

	public final void removeTexture() {
		image = null;
	}

	private static final void updateDimensionsOfImageCorrect(PImage image) {
		if (image.width > ctx.width || image.height > ctx.height) {
			image.resize((int) max(1, ctx.width), (int) max(1, ctx.height));
		}
	}
}