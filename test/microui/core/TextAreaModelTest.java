package microui.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TextAreaModelTest {

	@Test
	public void initializationDefaultState() {
		var model = new TextAreaModel();
		
		assertTrue(model.isBlank());
		assertTrue(model.getLinesCount() == 1);
		assertTrue(model.getText().isEmpty());
	}
	
	@Test
	public void insertChar() {
		var model = new TextAreaModel();
		
		model.insertChar('H');
		model.insertChar('e');
		model.insertChar('l');
		model.insertChar('l');
		model.insertChar('o');
		
		assertTrue(model.getText().equals("Hello"));
	}
	
	@Test
	public void insertString() {
		var model = new TextAreaModel();
		
		model.insertString("Hello");
		
		assertTrue(model.getText().equals("Hello"));
	}
	
	@Test
	public void removeChar() {
		var model = new TextAreaModel();
		
		model.insertString("Hello");
		
		model.removeChar();
		assertTrue(model.getText().equals("Hell"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hel"));
		
		model.removeChar();
		assertTrue(model.getText().equals("He"));
		
		model.removeChar();
		assertTrue(model.getText().equals("H"));
		
		model.removeChar();
		assertTrue(model.getText().equals(""));
		
		model.insertChar('H');
		model.insertChar('e');
		model.insertChar('l');
		model.insertChar('l');
		model.insertChar('o');
		
		model.removeChar();
		assertTrue(model.getText().equals("Hell"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hel"));
		
		model.removeChar();
		assertTrue(model.getText().equals("He"));
		
		model.removeChar();
		assertTrue(model.getText().equals("H"));
		
		model.removeChar();
		assertTrue(model.getText().equals(""));
	}
	
	@Test
	public void removeCharFromMultiLine() {
		var model = new TextAreaModel();
		
		model.insertString("Hello");
		model.addLine("World");
		model.moveCursorTo(1, 4);
		
		model.removeChar();
		assertTrue(model.getText().equals("Hello" + "\n" + "Worl"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hello" + "\n" + "Wor"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hello" + "\n" + "Wo"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hello" + "\n" + "W"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hello"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hell"));
		
		model.removeChar();
		assertTrue(model.getText().equals("Hel"));
		
		model.removeChar();
		assertTrue(model.getText().equals("He"));
		
		model.removeChar();
		assertTrue(model.getText().equals("H"));
		
		model.removeChar();
		assertTrue(model.getText().equals(""));
	}
	
	@Test
	public void clear() {
		var model = new TextAreaModel();
		
		model.insertString("Hello");
		model.addLine("World");
		
		model.clear();
		
		assertTrue(model.isBlank());
	}
}