package microui.core;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import microui.core.base.SpatialView;
import processing.core.PGraphics;

public class GraphicsBuffer extends SpatialView {
	private PGraphics graphics;
	private final List<BufferedView> viewList;
	private boolean autoClearBufferEnabled;
	
	public GraphicsBuffer(float x, float y, float width, float height) {
		super(x, y, width, height);
		setVisible(true);
		setConstrainDimensionsEnabled(true);
		setMinMaxSize(1, 1, ctx.width, ctx.height);

		viewList = new ArrayList<BufferedView>();
		
		createGraphics();
	}

	public GraphicsBuffer(SpatialView spatialView) {
		this(requireNonNull(spatialView, "spatialView").getX(), spatialView.getY(),
				spatialView.getWidth(), spatialView.getHeight());
	}

	public GraphicsBuffer() {
		this(0, 0, 0, 0);
	}

	public void onDraw(PGraphics graphics) {
		// for overriding
	}
	
	public boolean isAutoClearBufferEnabled() {
		return autoClearBufferEnabled;
	}

	public void setAutoClearBufferEnabled(boolean autoClearBufferEnabled) {
		this.autoClearBufferEnabled = autoClearBufferEnabled;
	}

	public final PGraphics getGraphics() {
		return graphics;
	}
	
	public final void addBufferedView(BufferedView bufferedView) {
		requireNonNull(bufferedView,"bufferedView");
		
		if (viewList.contains(bufferedView)) {
			throw new IllegalArgumentException("BufferedView cannot be added twice");
		}
		
		viewList.add(bufferedView);
	}
	
	public final void removeBufferedView(BufferedView bufferedView) {
		requireNonNull(bufferedView,"bufferedView");
		
		if (!viewList.contains(bufferedView)) {
			throw new IllegalArgumentException("BufferedView not found");
		}
		
		viewList.remove(bufferedView);
	}

	@Override
	protected void render() {
		if (graphics != null) {

			graphics.beginDraw();
			if (autoClearBufferEnabled) {
				graphics.clear();
			}
			onDraw(graphics);
			listOnDraw(graphics);
			graphics.endDraw();

			ctx.image(graphics, (int) getX(), (int) getY(), (int) getWidth(),(int)  getHeight());
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

	private void listOnDraw(PGraphics pGraphics) {
		if (viewList.isEmpty()) {
			return;
		}
		
		for (int i = viewList.size()-1; i >= 0; i--) {
			final BufferedView v = viewList.get(i);
			v.draw(pGraphics);
		}
	}
	
}