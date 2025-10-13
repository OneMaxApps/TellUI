package microui.core;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import microui.core.base.SpatialView;
import processing.core.PGraphics;

public class GraphicsBuffer extends SpatialView {
	private PGraphics graphics;

	public GraphicsBuffer(float x, float y, float width, float height) {
		super(x, y, width, height);
		setVisible(true);
		setConstrainDimensionsEnabled(true);
		setMinMaxSize(1, 1, ctx.width, ctx.height);

		createGraphics();
	}

	public GraphicsBuffer(SpatialView spatialView) {
		this(requireNonNull(spatialView, "spatialView cannot be null").getX(), spatialView.getY(),
				spatialView.getWidth(), spatialView.getHeight());
	}

	public GraphicsBuffer() {
		this(0, 0, 0, 0);
	}

	public void onDraw(PGraphics graphics) {
		// for overriding
	}

	public final PGraphics getGraphics() {
		return graphics;
	}

	@Override
	protected void render() {
		if (graphics != null) {

			graphics.beginDraw();
			onDraw(graphics);
			graphics.endDraw();

			ctx.image(graphics, getX(), getY(), getWidth(), getHeight());
		}
	}

	@Override
	protected void onChangeDimensions() {
		super.onChangeDimensions();

		createGraphics();
	}

	private void createGraphics() {
		graphics = ctx.createGraphics((int) max(1, getWidth()), (int) max(1, getHeight()), ctx.sketchRenderer());
	}

}