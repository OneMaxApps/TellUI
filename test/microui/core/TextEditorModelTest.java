package microui.core;

import static microui.constants.Direction.LEFT;
import static microui.constants.Direction.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
		model.moveCursorToEndOfText();
		model.removeChar();
		
		assertTrue(model.getText().equals("word 1" + "\n" + "word "));
		
		model.moveCursorTo(LEFT);
		model.removeChar();
		
		assertTrue(model.getText().equals("word 1" + "\n" + "word"));
		
		model.moveCursorToStartOfText();
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
	
	@Test
	public void getTextUntilCursor() {
		var model = new TextEditorModel();
		model.setText("word");

		model.setCursorColumn(0);
		assertEquals("", model.getTextUntilCursor());
		
		model.setCursorColumn(1);
		assertEquals("w", model.getTextUntilCursor());
		
		model.setCursorColumn(2);
		assertEquals("wo", model.getTextUntilCursor());
		
		model.setCursorColumn(3);
		assertEquals("wor", model.getTextUntilCursor());
		
		model.setCursorColumn(4);
		assertEquals("word", model.getTextUntilCursor());
		
		
		model.setText("Hello 1,Hello 2,Hello 3".split(","));
		
		model.setCursorRow(1);
		model.setCursorColumn(2);
		
		assertEquals("Hello 1\nHe", model.getTextUntilCursor());
		
		model.setCursorRow(0);
		model.setCursorColumn(0);
		
		assertEquals("", model.getTextUntilCursor());
		
		model.setCursorRow(0);
		model.setCursorColumn(7);
		
		assertEquals("Hello 1", model.getTextUntilCursor());
		
		model.setCursorRow(1);
		model.setCursorColumn(0);
		
		assertEquals("Hello 1\n", model.getTextUntilCursor());
		
		model.setCursorRow(2);
		model.setCursorColumn(7);
		
		assertEquals("Hello 1\nHello 2\nHello 3", model.getTextUntilCursor());
		
	}
	
	@Test
	public void getTextAfterCursor() {
		var model = new TextEditorModel();
		model.setText("word");

		model.setCursorColumn(0);
		assertEquals("word", model.getTextAfterCursor());
		
		model.setCursorColumn(1);
		assertEquals("ord", model.getTextAfterCursor());
		
		model.setCursorColumn(2);
		assertEquals("rd", model.getTextAfterCursor());
		
		model.setCursorColumn(3);
		assertEquals("d", model.getTextAfterCursor());
		
		model.setCursorColumn(4);
		assertEquals("", model.getTextAfterCursor());
		
		model.setText("Hello 1,Hello 2,Hello 3".split(","));
		
		model.setCursorRow(2);
		model.setCursorColumn(7);
		
		assertEquals("", model.getTextAfterCursor());
		
		model.setCursorRow(0);
		model.setCursorColumn(0);
		
		assertEquals("Hello 1\nHello 2\nHello 3", model.getTextAfterCursor());
		
		model.setCursorRow(1);
		model.setCursorColumn(0);
		
		assertEquals("Hello 2\nHello 3", model.getTextAfterCursor());
	}
	
}