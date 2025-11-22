package microui.core;

import static microui.constants.Direction.LEFT;
import static microui.constants.Direction.RIGHT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TextEditorModelTest {
	
	@Test
	public void initialization() {
		var model = new TextEditorModel();
		
		assertTrue(model.isBlank());
		assertTrue(model.getText().isEmpty());
		assertTrue(model.getCursorRow() == 0);
		assertTrue(model.getCursorColumn() == 0);
		assertTrue(model.getLinesCount() == 1);
		assertTrue(model.getLineLength(0) == 0);
		assertTrue(model.getLineText(0).isEmpty());
		
	}
	
	@Test
	public void insertChar() {
		var model = new TextEditorModel();
		
		model.insertChar('c');
		assertTrue(model.getText().equals("c"));
		
		model.moveCursorTo(RIGHT);
		model.insertChar('h');
		model.moveCursorTo(RIGHT);
		model.insertChar('a');
		model.moveCursorTo(RIGHT);
		model.insertChar('r');
		
		assertTrue(model.getText().equals("char"));
	}
	
	@Test
	public void insertString() {
		var model = new TextEditorModel();
		
		model.insertString("string");
		assertTrue(model.getText().equals("string"));
	}
	
	@Test
	public void setText() {
		var model = new TextEditorModel();
		
		model.setText("Hello World,Nice to meet you!".split(","));
		assertTrue(model.getText().equals("Hello World" + "\n" + "Nice to meet you!"));
		
	}
	
	@Test
	public void removeChar() {
		var model = new TextEditorModel();
		
		model.setText("word 1,word 2".split(","));
		model.moveCursorToEnd();
		model.removeChar();
		
		assertTrue(model.getText().equals("word 1" + "\n" + "word "));
		
		model.moveCursorTo(LEFT);
		model.removeChar();
		
		assertTrue(model.getText().equals("word 1" + "\n" + "word"));
		
		model.moveCursorToStart();
		model.removeChar();
		
		assertTrue(model.getText().equals("ord 1" + "\n" + "word"));
	}
	
	@Test
	public void getSelectedText() {
		var model = new TextEditorModel();
		
		model.setText("word 1,word 2,word 3".split(","));
		
		model.setSelect(0, 0, 1, 3);
		
		assertTrue(model.getSelectedText().equals("or"));
		
		model.setSelect(0, 2, 1, 1);

		assertTrue(model.getSelectedText().equals("ord 1" + "\n" + "word 2" + "\n" + "w"));
		
	}
	
	@Test
	public void removeSelectedText() {
		var model = new TextEditorModel();
		
		model.setText("word");
		
		model.setSelect(0, 0, 1, 3);
		model.removeSelectedText();
		
		assertTrue(model.getText().equals("wd"));
		
		model.setText("word 1,word 2,word 3".split(","));
		model.setSelect(0, 2, 1, 1);
		model.removeSelectedText();
		
		assertTrue(model.getText().equals("w" + "ord 3"));
		
	}
}