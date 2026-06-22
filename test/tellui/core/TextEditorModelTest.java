package tellui.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tellui.core.TextEditorModel.Direction;

public class TextEditorModelTest {

	@Test
	public void initialization() {
		var model = new TextEditorModel();

		assertTrue(model.isBlank());
		assertTrue(model.getText().isEmpty());
		assertTrue(model.getCursorRow() == 0);
		assertTrue(model.getCursorColumn() == 0);
		assertTrue(model.getLineCount() == 1);
		assertTrue(model.getLineLength(0) == 0);
		assertTrue(model.getLineText(0).isEmpty());

	}

	@Test
	public void insertChar() {
		var model = new TextEditorModel();

		model.insertChar('c');
		assertEquals("c", model.getText());

		model.moveCursorTo(Direction.RIGHT);
		model.insertChar('h');
		model.moveCursorTo(Direction.RIGHT);
		model.insertChar('a');
		model.moveCursorTo(Direction.RIGHT);
		model.insertChar('r');

		assertEquals("char", model.getText());
	}

	@Test
	public void insertString() {
		var model = new TextEditorModel();

		model.insertString("string");
		assertEquals("string", model.getText());
	}

	@Test
	public void setText() {
		var model = new TextEditorModel();

		model.setText("Hello World,Nice to meet you!".split(","));

		assertEquals("Hello World\nNice to meet you!", model.getText());
	}

	@Test
	public void removeChar() {
		var model = new TextEditorModel();

		model.setText("word 1,word 2".split(","));
		model.moveCursorToEndOfText();
		model.removeChar();

		assertEquals("word 1\nword ", model.getText());

		model.moveCursorTo(Direction.LEFT);
		model.removeChar();

		assertEquals("word 1\nword", model.getText());

		model.moveCursorToStartOfText();
		model.removeChar();

		assertEquals("ord 1\nword", model.getText());
	}

	@Test
	public void getSelectedText() {
		var model = new TextEditorModel();

		model.setText("word 1,word 2,word 3".split(","));

		model.setSelection(0, 0, 1, 3);

		assertEquals("or", model.getSelectedText());

		model.setSelection(0, 2, 1, 1);

		assertEquals("ord 1\nword 2\nw", model.getSelectedText());
	}

	@Test
	public void removeSelectedText() {
		var model = new TextEditorModel();

		model.setText("word");

		model.setSelection(0, 0, 1, 3);
		model.removeSelectedText();

		assertEquals("wd", model.getText());

		model.setText("word 1,word 2,word 3".split(","));
		model.setSelection(0, 2, 1, 1);
		model.removeSelectedText();

		assertEquals("word 3", model.getText());
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