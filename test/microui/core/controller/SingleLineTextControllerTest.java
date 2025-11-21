package microui.core.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SingleLineTextControllerTest {
	
	@Test
	public void insertChar() {
		var controller = new SingleLineTextController();
		
		controller.insert(0, '1');
		assertTrue(controller.getAsString().equals("1"));
		
		controller.insert(1, '2');
		assertTrue(controller.getAsString().equals("12"));
		
		controller.insert(0, '3');
		assertTrue(controller.getAsString().equals("312"));
		
		controller.insert(0, '4');
		assertTrue(controller.getAsString().equals("4312"));
	}
	
	@Test
	public void insertString() {
		var controller = new SingleLineTextController();
		
		controller.insert(0, "str");
		assertTrue(controller.getAsString().equals("str"));
		
		controller.insert(3, "ing");
		assertTrue(controller.getAsString().equals("string"));
		
		controller.insert(0, "type ");
		assertTrue(controller.getAsString().equals("type string"));
	}
	
	@Test
	public void insertInt() {
		var controller = new SingleLineTextController();
		
		controller.insert(0, 1);
		assertTrue(controller.getAsString().equals("1"));
		
		controller.insert(1, 2);
		assertTrue(controller.getAsString().equals("12"));
		
		controller.insert(2, 3);
		assertTrue(controller.getAsString().equals("123"));
	}
	
	@Test
	public void removeChar() {
		var controller = new SingleLineTextController();
		
		controller.insert(0, "string");
		controller.removeCharAt(2);
		
		assertTrue(controller.getAsString().equals("sting"));
		
		controller.removeCharAt(4);
		assertTrue(controller.getAsString().equals("stin"));
		
	}
	
	@Test
	public void removeString() {
		var controller = new SingleLineTextController();
		
		controller.insert(0, "string");
		controller.remove(0,3);
		
		assertTrue(controller.getAsString().equals("ing"));
		
		controller.remove(0,2);
		
		assertTrue(controller.getAsString().equals("g"));
	}
	
	@Test
	public void getAsString() {
		var controller = new SingleLineTextController();
		
		controller.insert(0, "string");
		
		assertTrue(controller.getAsString().equals("string"));
	}
	
	@Test
	public void clear() {
		var controller = new SingleLineTextController();
		
		controller.insert(0, "string");
		controller.clear();
		
		assertTrue(controller.getAsString().isEmpty());
	}
	
	@Test
	public void set() {
		var controller = new SingleLineTextController();
		
		controller.set("string");
		assertTrue(controller.getAsString().equals("string"));
		
		controller.set("other string");
		assertTrue(controller.getAsString().equals("other string"));
	}
}