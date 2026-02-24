package microui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import processing.core.PApplet;

public class ConvertTest {

	@Test
	void test() {
		
		for (int i = 0; i < 1000; i++) {
			assertEquals(MathUtils.convert(i, i / 2, i / 3, i / 4, i / 5), PApplet.map(i, i / 2, i / 3, i / 4, i / 5));
			assertEquals(MathUtils.convert(-i, i / 2, i / 3, i / 4, i / 5),
					PApplet.map(-i, i / 2, i / 3, i / 4, i / 5));
			assertEquals(MathUtils.convert(i, -i / 2, i / 3, i / 4, i / 5),
					PApplet.map(i, -i / 2, i / 3, i / 4, i / 5));
			assertEquals(MathUtils.convert(-i, i / 2, -i / 3, i / 4, -i / 5),
					PApplet.map(-i, i / 2, -i / 3, i / 4, -i / 5));
		}
	}

}