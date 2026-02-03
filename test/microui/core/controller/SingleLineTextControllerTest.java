package microui.core.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SingleLineTextControllerTest {

	@Test
	public void insertChar() {
		var controller = new SingleLineTextController();

		controller.insert(0, '1');
		assertEquals("1", controller.getAsString());

		controller.insert(1, '2');
		assertEquals("12", controller.getAsString());

		controller.insert(0, '3');
		assertEquals("312", controller.getAsString());

		controller.insert(0, '4');
		assertEquals("4312", controller.getAsString());
	}

	@Test
	public void insertString() {
		var controller = new SingleLineTextController();

		controller.insert(0, "str");
		assertEquals("str", controller.getAsString());

		controller.insert(3, "ing");
		assertEquals("string", controller.getAsString());

		controller.insert(0, "type ");
		assertEquals("type string", controller.getAsString());
	}

	@Test
	public void insertInt() {
		var controller = new SingleLineTextController();

		controller.insert(0, 1);
		assertEquals("1", controller.getAsString());

		controller.insert(1, 2);
		assertEquals("12", controller.getAsString());

		controller.insert(2, 3);
		assertEquals("123", controller.getAsString());
	}

	@Test
	public void removeChar() {
		var controller = new SingleLineTextController();

		controller.insert(0, "string");

		controller.removeCharAt(2);
		assertEquals("sting", controller.getAsString());

		controller.removeCharAt(4);
		assertEquals("stin", controller.getAsString());
	}

	@Test
	public void removeString() {
		var controller = new SingleLineTextController();

		controller.insert(0, "string");

		controller.remove(0, 3);
		assertEquals("ing", controller.getAsString());

		controller.remove(0, 2);
		assertEquals("g", controller.getAsString());
	}

	@Test
	public void getAsString() {
		var controller = new SingleLineTextController();

		controller.insert(0, "string");
		assertEquals("string", controller.getAsString());
	}

	@Test
	public void clear() {
		var controller = new SingleLineTextController();

		controller.insert(0, "string");
		controller.clear();

		assertTrue(controller.isEmpty());
	}

	@Test
	public void set() {
		var controller = new SingleLineTextController();

		controller.set("string");
		assertEquals("string", controller.getAsString());

		controller.set("other string");
		assertEquals("other string", controller.getAsString());
	}
}