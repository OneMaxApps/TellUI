package microui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import processing.core.PApplet;

public class MapTest {

	@Test
	void test() {
		assertEquals(MathUtils.map(0,0,0,0,0),PApplet.map(0,0,0,0,0));
		assertEquals(MathUtils.map(0,0,0,100,0),PApplet.map(0,0,0,100,0));
		assertEquals(MathUtils.map(100,0,10,0,1110),PApplet.map(100,0,10,0,1110));
		assertEquals(MathUtils.map(50,0,10,100,220),PApplet.map(50,0,10,100,220));
		
	}
	
}