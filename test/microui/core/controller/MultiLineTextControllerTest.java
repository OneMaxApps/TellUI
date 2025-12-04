package microui.core.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MultiLineTextControllerTest {

	@Test
	public void addLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");

		assertEquals("\nHello", controller.getText());
	}
	
	@Test
	public void addSeveralLines() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine(" ");
		controller.addLine("World");
		
		assertEquals("Hello\n \nWorld", controller.getText());
	}
	
	@Test
	public void insertLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		
		controller.insertLine(1, " ");

		assertEquals("Hello\n \nWorld", controller.getText());
	}
	
	@Test
	public void removeLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		controller.removeLine(1);
		
		assertEquals("Hello", controller.getText());
	}
	
	@Test
	public void insertCharForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		controller.insertCharForLine(0, 2, '*');
		controller.insertCharForLine(0, 6, '*');
	
		assertEquals("He*llo*\nWorld", controller.getText());
	}
	
	@Test
	public void insertStringForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		controller.insertStringForLine(0, 5, ", ");
		controller.insertStringForLine(1, 5, "!");

		assertEquals("Hello, \nWorld!", controller.getText());
	}
	
	@Test
	public void removeCharForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.removeCharForLine(0, 0);
		controller.removeCharForLine(0, 1);
		
		assertEquals("elo", controller.getText());
	}
	
	@Test
	public void removeStringForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.removeStringForLine(0, 1, 4);
		
		assertEquals("Ho", controller.getText());
	}
	
	@Test
	public void splitLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.splitLine(0, 2);
		
		assertEquals("He\nllo", controller.getText());
	}
	
	@Test
	public void mergeLines() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine(" World");
		
		controller.mergeLines(0);
		
		assertEquals("Hello World", controller.getText());
	}
	
	@Test
	public void setText() {
		var controller = new MultiLineTextController();
		final String[] lines = "1,22,333,4444".split(",");
		
		controller.setText(lines);
		
		assertEquals("1\n22\n333\n4444", controller.getText());
	}
	
	@Test
	public void clearText() {
		var controller = new MultiLineTextController();
		final String[] lines = "1,22,333,4444".split(",");
		
		controller.setText(lines);
		
		controller.clear();
		
		assertTrue(controller.getText().isEmpty());
	}
	
	@Test
	public void initializationState() {
		var controller = new MultiLineTextController();
		
		assertTrue(controller.getLinesCount() == 1);
		assertTrue(controller.getText().isEmpty());
	}
	
	@Test
	public void undo() {
		var controller = new MultiLineTextController();
		controller.setSpeedForUpdate(0);
		
		controller.insertStringForLine(0,0,"Hello ");
		controller.insertStringForLine(0,6,"World");
		controller.insertStringForLine(0,11,"!");
		
		assertEquals("Hello World!", controller.getText());
		
		controller.undo();
		assertEquals("Hello World", controller.getText());
				
		controller.undo();
		assertEquals("Hello ", controller.getText());
		
		controller.undo();
		assertEquals("", controller.getText());
		
		controller.insertStringForLine(0,0,"Hello");
		controller.addLine("World");
		
		assertEquals("Hello" + "\n" + "World", controller.getText());

		controller.undo();
		assertEquals("Hello", controller.getText());
		
		controller.undo();
		assertEquals("",controller.getText());
	}
	
	@Test
	public void redo() {
		var controller = new MultiLineTextController();
		controller.setSpeedForUpdate(0);
		
		controller.insertStringForLine(0,0,"Hello ");
		controller.insertStringForLine(0,6,"World");
		controller.insertStringForLine(0,11,"!");
		
		assertEquals("Hello World!", controller.getText());
		
		controller.undo();
		assertEquals("Hello World", controller.getText());

		controller.redo();
		assertEquals("Hello World!", controller.getText());
		
		controller.undo();
		assertEquals("Hello World", controller.getText());
		
		controller.undo();
		assertEquals("Hello ", controller.getText());
		
		controller.redo();
		assertEquals("Hello World", controller.getText());
		
		controller.redo();
		assertEquals("Hello World!", controller.getText());
		
		controller.redo();
		assertEquals("Hello World!", controller.getText());
		
		controller.undo();
		assertEquals("Hello World", controller.getText());
		
		controller.undo();
		assertEquals("Hello ", controller.getText());
		
		controller.undo();
		assertEquals("", controller.getText());
		
		controller.undo();
		assertEquals("", controller.getText());
		
		controller.redo();
		assertEquals("Hello ", controller.getText());
		
		controller.redo();
		assertEquals("Hello World", controller.getText());
		
		controller.redo();
		assertEquals("Hello World!", controller.getText());
		
		controller.redo();
		assertEquals("Hello World!", controller.getText());
		
		controller.undo();
		assertEquals("Hello World", controller.getText());
	}
	
	@Test
	public void complexUndoRedoScenario() {
	    var controller = new MultiLineTextController();
	    controller.setSpeedForUpdate(0);
	    
	    controller.insertStringForLine(0, 0, "Start ");
	    controller.addLine("Middle");
	    controller.insertStringForLine(1, 6, " Line");
	    controller.splitLine(0, 3);
	    controller.mergeLines(0);
	    controller.removeLine(1);
	    
	    final String finalState = controller.getText();
	    
	    controller.undo();
	    controller.undo();
	    controller.undo();
	    controller.undo();
	    controller.undo();
	    controller.undo();
	    controller.undo();
	    controller.undo();
	    
	    assertEquals("", controller.getText());
	    
	    controller.redo();
	    controller.redo();
	    controller.redo();
	    controller.redo();
	    controller.redo();
	    controller.redo();
	    controller.redo();
	    controller.redo();
	    
	    assertEquals(finalState, controller.getText());
	}
}