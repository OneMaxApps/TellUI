package microui.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MultiLineTextControllerTest {

	@Test
	public void addLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello"));
	}
	
	@Test
	public void addSeveralLines() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.addLine(" ");
		controller.addLine("World");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello" + "\n" + " " + "\n" + "World"));
	}
	
	@Test
	public void insertLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.addLine("World");
		
		controller.insertLine(1, " ");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello" + "\n" + " " + "\n" + "World"));
	}
	
	@Test
	public void removeLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.addLine("World");
		controller.removeLine(1);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello"));
	}
	
	@Test
	public void insertCharForLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.addLine("World");
		controller.insertCharForLine(0, 2, '*');
		controller.insertCharForLine(0, 6, '*');
		
		final String text = controller.getText();
		
		assertTrue(text.equals("He*llo*" + "\n" + "World"));
	}
	
	@Test
	public void insertStringForLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.addLine("World");
		controller.insertStringForLine(0, 5, ", ");
		controller.insertStringForLine(1, 5, "!");
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello, " + "\n" + "World!"));
	}
	
	@Test
	public void removeCharForLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.removeCharForLine(0, 0);
		controller.removeCharForLine(0, 1);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("elo"));
	}
	
	@Test
	public void removeStringForLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.removeStringForLine(0, 1, 4);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Ho"));
	}
	
	@Test
	public void splitLine() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.splitLine(0, 2);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("He" + "\n" + "llo"));
	}
	
	@Test
	public void mergeLines() {
		var controller = new MultiLineTextController();
		controller.addLine("Hello");
		controller.addLine(" World");
		
		controller.mergeLines(0);
		
		final String text = controller.getText();
		
		assertTrue(text.equals("Hello World"));
	}
}