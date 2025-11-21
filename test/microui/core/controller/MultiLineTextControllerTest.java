package microui.core.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MultiLineTextControllerTest {

	@Test
	public void addLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("\nHello"));
	}
	
	@Test
	public void addSeveralLines() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine(" ");
		controller.addLine("World");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello" + "\n" + " " + "\n" + "World"));
	}
	
	@Test
	public void insertLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		
		controller.insertLine(1, " ");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello" + "\n" + " " + "\n" + "World"));
	}
	
	@Test
	public void removeLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		controller.removeLine(1);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello"));
	}
	
	@Test
	public void insertCharForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		controller.insertCharForLine(0, 2, '*');
		controller.insertCharForLine(0, 6, '*');
		
		final String text = controller.getText();
		
		assertTrue(text.equals("He*llo*" + "\n" + "World"));
	}
	
	@Test
	public void insertStringForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine("World");
		controller.insertStringForLine(0, 5, ", ");
		controller.insertStringForLine(1, 5, "!");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello, " + "\n" + "World!"));
	}
	
	@Test
	public void removeCharForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.removeCharForLine(0, 0);
		controller.removeCharForLine(0, 1);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("elo"));
	}
	
	@Test
	public void removeStringForLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.removeStringForLine(0, 1, 4);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Ho"));
	}
	
	@Test
	public void splitLine() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.splitLine(0, 2);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("He" + "\n" + "llo"));
	}
	
	@Test
	public void mergeLines() {
		var controller = new MultiLineTextController();
		controller.insertStringForLine(0, 0, "Hello");
		controller.addLine(" World");
		
		controller.mergeLines(0);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello World"));
	}
	
	@Test
	public void setText() {
		var controller = new MultiLineTextController();
		final String[] lines = "1,22,333,4444".split(",");
		
		controller.setText(lines);
		
		final String expectedText = "1\n22\n333\n4444";
		
		assertTrue(controller.getText().equals(expectedText));
	}
	
	@Test
	public void clearText() {
		var controller = new MultiLineTextController();
		final String[] lines = "1,22,333,4444".split(",");
		
		controller.setText(lines);
		
		controller.clear();
		
		assertTrue(controller.getText().equals(""));
	}
	
	@Test
	public void initializationState() {
		var controller = new MultiLineTextController();
		
		assertTrue(controller.getLinesCount() == 1);
		assertTrue(controller.getText().equals(""));
	}
}