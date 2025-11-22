package microui.core;

import static java.util.Objects.requireNonNull;
import static microui.constants.Direction.LEFT;
import static microui.constants.Direction.RIGHT;
import static microui.constants.Direction.UP;

import microui.constants.Direction;
import microui.core.controller.MultiLineTextController;
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
	}

	// == PUBLIC API ==
	public void insertChar(char ch) {
		controller.insertCharForLine(cursor.getRow(), cursor.getColumn(), ch);
		cursor.moveTo(RIGHT);
	}

	public void insertString(String text) {
		controller.insertStringForLine(cursor.getRow(), cursor.getColumn(), text);
		cursor.moveTo(RIGHT, text.length());
	}

	public void removeChar() {
		if(getText().isEmpty()) {
			return;
		}
		
		if (cursor.atLineStart()) {
			cursor.moveTo(UP);
			cursor.moveToLineEnd();
			
			if(!controller.getLine(cursor.getRow() + 1).isEmpty()) {
				mergeLines();
			}
		}
		
		controller.removeCharForLine(cursor.getRow(), cursor.getColumn());
		
		cursor.moveTo(LEFT);
	}

	public void setText(String... text) {
		controller.setText(text);
		cursor.moveToEnd();
	}

	public String getText() {
		return controller.getText();
	}

	public void moveCursorTo(Direction direction, int repeat) {
		cursor.moveTo(direction, repeat);
	}
	
	public void moveCursorTo(Direction direction) {
		cursor.moveTo(direction);
	}

	public void moveCursorTo(int row, int column) {
		cursor.moveTo(row, column);
	}

	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
	}
	
	public boolean isBlank() {
		return controller.isBlank();
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
		controller.splitLine(cursor.getRow(), cursor.getColumn());
	}
	
	public void mergeLines() {
		controller.mergeLines(cursor.getRow());
	}

	// == PRIVATE API ==
	private MultiLineTextController getController() {
		return controller;
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

		public void moveTo(Direction direction, int repeat) {
			requireNonNull(direction, "direction");
			
			for (int i = 0; i < repeat; i++) {
				
				switch (direction) {
				case LEFT:
					if (onFirstLine()) {
						if (!atLineStart()) {
							setColumn(getColumn() - 1);
						}
					} else {
						if (atLineStart()) {
							setRow(getRow() - 1);
							moveToLineEnd();
						} else {
							setColumn(getColumn() - 1);
						}
					}
					
					break;
					
				case UP:
					setRow(getRow() - 1);
					break;
				case RIGHT:
					if (onLastLine()) {
						if (!atLineEnd()) {
							setColumn(getColumn() + 1);
						}
					} else {
						if (atLineEnd()) {
							setRow(getRow() + 1);
							moveToLineStart();
						} else {
							setColumn(getColumn() + 1);
						}
					}
					break;
				case DOWN:
					setRow(getRow() + 1);
					break;
				}
			
			}
		}
		
		public void moveTo(Direction direction) {
			moveTo(direction,1);
		}

		public void moveTo(int row, int column) {
			setRow(row);
			setColumn(column);
		}

		public void moveToEnd() {
			moveTo(getRowCount() - 1, getLastLineLength());
		}
		
		public void moveToLineStart() {
			setColumn(0);
		}
		
		public void moveToLineEnd() {
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
			return c.getLine(c.getLinesCount() - 1).length();
		}
		
		private int getCurrentLineLength() {
			return getController().getLine(getRow()).length();
		}
		
		private boolean onFirstLine() {
			return getRow() == 0;
		}
		
		private boolean onLastLine() {
			return getRow() == getRowCount() - 1;
		}
	}
}