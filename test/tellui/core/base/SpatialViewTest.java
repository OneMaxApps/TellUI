package tellui.core.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import tellui.TellUI;

class SpatialViewTest {

	@BeforeAll
	static void initTellUI() {
		TellUI.init(new PApplet());
	}

	@Test
	void initBounds() {
		SpatialView spatialView = new SpatialView(10, 20, 30, 40) {
			@Override
			protected void render() {
			}
		};

		assertEquals(10, spatialView.getX());
		assertEquals(20, spatialView.getY());
		assertEquals(30, spatialView.getWidth());
		assertEquals(40, spatialView.getHeight());
	}

	@Test
	void setBounds() {
		SpatialView spatialView = new SpatialView(0, 0, 0, 0) {
			@Override
			protected void render() {
			}
		};

		spatialView.setBounds(10, 20, 30, 40);

		assertEquals(10, spatialView.getX());
		assertEquals(20, spatialView.getY());
		assertEquals(30, spatialView.getWidth());
		assertEquals(40, spatialView.getHeight());

	}

	@Test
	void setConstrainDimensions() {
		SpatialView spatialView = new SpatialView(0, 0, 0, 0) {
			@Override
			protected void render() {
			}
		};
		spatialView.setConstrainDimensionsEnabled(true);
		spatialView.setMinMaxSize(1, 5);

		spatialView.setSize(100, 100);

		assertEquals(5, spatialView.getWidth());
		assertEquals(5, spatialView.getHeight());

		spatialView.setMaxSize(10);

		spatialView.setSize(100, 100);

		assertEquals(10, spatialView.getWidth());
		assertEquals(10, spatialView.getHeight());

		spatialView.setMinSize(5);

		spatialView.setSize(1, 1);

		assertEquals(5, spatialView.getWidth());
		assertEquals(5, spatialView.getHeight());

		spatialView.setMinSize(8);

		spatialView.setSize(1, 1);

		assertEquals(8, spatialView.getWidth());
		assertEquals(8, spatialView.getHeight());

		spatialView.setConstrainDimensionsEnabled(false);

		spatialView.setSize(100, 100);

		assertEquals(100, spatialView.getWidth());
		assertEquals(100, spatialView.getHeight());

		spatialView.setConstrainDimensionsEnabled(true);
		spatialView.setSize(20);
		spatialView.setMaxSize(10);

		assertEquals(10, spatialView.getWidth());
		assertEquals(10, spatialView.getHeight());

		spatialView.setNegativeDimensionsEnabled(true);
		spatialView.setMinSize(-20);

		spatialView.setSize(-21);

		assertEquals(-20, spatialView.getWidth());
		assertEquals(-20, spatialView.getHeight());
	}

	@Test
	void setNegativeDimensions() {
		SpatialView spatialView = new SpatialView(0, 0, 0, 0) {
			@Override
			protected void render() {
			}
		};

		spatialView.setNegativeDimensionsEnabled(true);

		spatialView.setBounds(10, 20, -30, -40);

		assertEquals(-30, spatialView.getWidth());
		assertEquals(-40, spatialView.getHeight());

	}
}