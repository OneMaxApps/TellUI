package microui.core;

import static java.util.Objects.requireNonNull;

import microui.constants.Direction;
import microui.core.controller.MultiLineTextController;
import microui.core.controller.SingleLineTextController;
import microui.event.Listener;
import microui.util.MathUtils;

public final class TextAreaModel {
	private final MultiLineTextController controller;
	private final Cursor cursor;
	private Listener onTextChangedListener;
	
	public TextAreaModel() {
		super();
		controller = new MultiLineTextController();
		controller.setOnTextChangedListener(this::onTextChanged);
		cursor = new Cursor(this);
	}

	public static void main(String[] args) {
		var model = new TextAreaModel();

		model.insertChar('H');
		model.insertChar('e');
		model.insertChar('l');
		model.insertChar('l');
		model.insertChar('o');

		//model.insertString(" World");
		
		//model.addLine("And YOU");
		
		model.moveCursorToEnd();
		
		for(int i = 0; i < model.getText().length(); i++) {
			System.out.println("text length: " + model.getText().length());
			model.removeChar();
		}

		System.out.println(model.getText()); // "Hello World"
	}

	// == PUBLIC API ==
	public void insertChar(char ch) {
		controller.insertCharForLine(cursor.getRow(), cursor.getColumn(), ch);
		cursor.moveToNextColumn();
	}

	public void insertString(String text) {
		controller.insertStringForLine(cursor.getRow(), cursor.getColumn(), text);

		cursor.moveToNextColumn(text.length());
	}

	public void removeChar() {
		System.out.println("cursor row: " + getCursorRow());
		System.out.println("cursor column: " + getCursorColumn());
		
		if(getText().isEmpty()) {
			return;
		}
		
		if (getCurrentLine().isEmpty()) {
			cursor.moveToPrevRow();
			cursor.moveToCurrentLineEnd();
		}
		
		controller.removeCharForLine(cursor.getRow(), cursor.getColumn());

		cursor.moveToPrevColumn();
	}

	public void setText(String... text) {
		controller.setText(text);
		cursor.moveToEnd();
	}

	public String getText() {
		return controller.getText();
	}
	
	public int getCursorColumn() {
		return cursor.getColumn();
	}

	public void setCursorColumn(int column) {
		cursor.setColumn(column);
	}

	public int getCursorRow() {
		return cursor.getRow();
	}

	public void setCursorRow(int row) {
		cursor.setRow(row);
	}

	public void moveCursorTo(Direction direction) {
		cursor.moveTo(direction);
	}

	public void moveCursorTo(int row, int column) {
		cursor.moveTo(row, column);
	}

	public void moveCursorToPrevRow() {
		cursor.moveToPrevRow();
	}

	public void moveCursorToPrevRow(int repeatCount) {
		cursor.moveToPrevRow(repeatCount);
	}

	public void moveCursorToNextRow() {
		cursor.moveToNextRow();
	}

	public void moveCursorToNextRow(int repeatCount) {
		cursor.moveToNextRow(repeatCount);
	}

	public void moveCursorToPrevColumn() {
		cursor.moveToPrevColumn();
	}

	public void moveCursorToPrevColumn(int repeatCount) {
		cursor.moveToPrevColumn(repeatCount);
	}

	public void moveCursorToNextColumn() {
		cursor.moveToNextColumn();
	}

	public void moveCursorToNextColumn(int repeatCount) {
		cursor.moveToNextColumn(repeatCount);
	}

	public void moveCursorToBehind() {
		cursor.moveToBehind();
	}

	public void moveCursorToEnd() {
		cursor.moveToEnd();
	}
	
	public void moveCursorToCurrentLineBehind() {
		cursor.moveToCurrentLineBehind();
	}

	public void moveCursorToCurrentLineEnd() {
		cursor.moveToCurrentLineEnd();
	}

	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
	}
	
	public boolean isEmpty() {
		return controller.isEmpty();
	}

	public int getLinesCount() {
		return controller.getLinesCount();
	}

	public void addLine(String text) {
		controller.addLine(text);
	}

	public void clear() {
		controller.clear();
	}

	public String getLineText(int row) {
		return controller.getLineText(row);
	}

	public int getLineLength(int row) {
		return controller.getLineLength(row);
	}
	
	public void splitLine() {
		controller.splitLine(getCursorRow(), getCursorColumn());
	}
	
	public void mergeLines() {
		controller.mergeLines(getCursorRow());
	}

	// == PRIVATE API ==
	private MultiLineTextController getController() {
		return controller;
	}

	private SingleLineTextController getCurrentLine() {
		return controller.getLine(cursor.getRow());
	}
	
	private void onTextChanged() {
		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
	}

	private static final class Cursor {
		private final TextAreaModel model;
		private int column, row;

		public Cursor(TextAreaModel textAreaModel) {
			super();
			model = requireNonNull(textAreaModel, "textAreaModel");
		}

		public boolean atLineStart() {
			return getColumn() == 0;
		}
		
		public boolean atLineEnd() {
			return getColumn() == getCurrentLineLength();
		}

		public int getColumn() {
			return getClampedColumn(column);
		}

		public void setColumn(int column) {
			this.column = getClampedColumn(column);
		}

		public int getRow() {
			return getClampedRow(row);
		}

		public void setRow(int row) {
			this.row = getClampedRow(row);
		}

		public void moveTo(Direction direction) {
			switch (requireNonNull(direction, "direction")) {
			case LEFT:
				setColumn(getColumn() - 1);
				break;
			case UP:
				setRow(getRow() - 1);
				break;
			case RIGHT:
				setColumn(getColumn() + 1);
				break;
			case DOWN:
				setRow(getRow() + 1);
				break;
			}

		}

		public void moveTo(int row, int column) {
			setRow(row);
			setColumn(column);
		}

		public void moveToPrevRow() {
			setRow(getRow() - 1);
		}
		
		public void moveToPrevRow(int repeatCount) {
			for (int i = 0; i < repeatCount; i++) {
				moveToPrevRow();
			}
		}

		public void moveToNextRow() {
			setRow(getRow() + 1);
		}
		
		public void moveToNextRow(int repeatCount) {
			for (int i = 0; i < repeatCount; i++) {
				moveToNextRow();
			}
		}

		public void moveToPrevColumn() {
			setColumn(getColumn() - 1);
		}
		
		public void moveToPrevColumn(int repeatCount) {
			for (int i = 0; i < repeatCount; i++) {
				moveToPrevColumn();
			}
		}

		public void moveToNextColumn() {
			setColumn(getColumn() + 1);
		}

		public void moveToNextColumn(int repeatCount) {
			for (int i = 0; i < repeatCount; i++) {
				moveToNextColumn();
			}
		}

		public void moveToBehind() {
			moveTo(0, 0);
		}

		public void moveToEnd() {
			moveTo(getRowCount(), getLastLineLength() - 1);
		}
		
		public void moveToCurrentLineBehind() {
			setColumn(0);
		}
		
		public void moveToCurrentLineEnd() {
			setColumn(getCurrentLineLength());
		}

		private MultiLineTextController getController() {
			return model.getController();
		}

		private int getClampedColumn(int column) {
			return (int) MathUtils.constrain(column, 0, getController().getLine(getRow()).length());
		}

		private int getClampedRow(int row) {
			return (int) MathUtils.constrain(row, 0, getController().getLinesCount() - 1);
		}

		private int getRowCount() {
			return getController().getLinesCount();
		}

		private int getLastLineLength() {
			final MultiLineTextController c = getController();
			return c.getLine(c.getLinesCount()).length();
		}
		
		private int getCurrentLineLength() {
			return getController().getLine(getRow()).length();
		}
	}
}