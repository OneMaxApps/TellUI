package microui.core.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import microui.MicroUI;
import processing.core.PApplet;

class SpatialViewTest {

	@BeforeAll
	static void initMicroUI() {
		MicroUI.setContext(new PApplet());
	}

	@Test
	void initBoundsTest() {
		SpatialView spatialView = new SpatialView(10, 20, 30, 40) {
			@Override
			protected void render() {
			}
		};

		assertEquals(spatialView.getX(), 10);
		assertEquals(spatialView.getY(), 20);
		assertEquals(spatialView.getWidth(), 30);
		assertEquals(spatialView.getHeight(), 40);

	}

	@Test
	void setBoundsTest() {
		SpatialView spatialView = new SpatialView(0, 0, 0, 0) {
			@Override
			protected void render() {
			}
		};

		spatialView.setBounds(10, 20, 30, 40);

		assertEquals(spatialView.getX(), 10);
		assertEquals(spatialView.getY(), 20);
		assertEquals(spatialView.getWidth(), 30);
		assertEquals(spatialView.getHeight(), 40);

	}

	@Test
	void setConstrainDimensionsTest() {
		SpatialView spatialView = new SpatialView(0, 0, 0, 0) {
			@Override
			protected void render() {
			}
		};
		spatialView.setConstrainDimensionsEnabled(true);
		spatialView.setMinMaxSize(1, 5);

		spatialView.setSize(100, 100);

		assertEquals(spatialView.getWidth(), 5);
		assertEquals(spatialView.getHeight(), 5);

		spatialView.setMaxSize(10);

		spatialView.setSize(100, 100);

		assertEquals(spatialView.getWidth(), 10);
		assertEquals(spatialView.getHeight(), 10);

		spatialView.setMinSize(5);

		spatialView.setSize(1, 1);

		assertEquals(spatialView.getWidth(), 5);
		assertEquals(spatialView.getHeight(), 5);

		spatialView.setMinSize(8);

		spatialView.setSize(1, 1);

		assertEquals(spatialView.getWidth(), 8);
		assertEquals(spatialView.getHeight(), 8);

		spatialView.setConstrainDimensionsEnabled(false);

		spatialView.setSize(100, 100);

		assertEquals(spatialView.getWidth(), 100);
		assertEquals(spatialView.getHeight(), 100);

		spatialView.setConstrainDimensionsEnabled(true);
		spatialView.setSize(20);
		spatialView.setMaxSize(10);

		assertEquals(spatialView.getWidth(), 10);
		assertEquals(spatialView.getHeight(), 10);

		spatialView.setNegativeDimensionsEnabled(true);
		spatialView.setMinSize(-20);

		spatialView.setSize(-21);

		assertEquals(spatialView.getWidth(), -20);
		assertEquals(spatialView.getHeight(), -20);
	}

	@Test
	void setNegativeDimensionsTest() {
		SpatialView spatialView = new SpatialView(0, 0, 0, 0) {
			@Override
			protected void render() {
			}
		};

		spatialView.setNegativeDimensionsEnabled(true);

		spatialView.setBounds(10, 20, -30, -40);

		assertEquals(spatialView.getWidth(), -30);
		assertEquals(spatialView.getHeight(), -40);

	}
}
