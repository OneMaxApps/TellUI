package microui.core;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import microui.constants.Direction;
import microui.core.controller.MultiLineTextController;
import microui.event.Listener;

public final class TextEditorModel {
	private final MultiLineTextController controller;
	private final Cursor cursor;
	private final Selection selection;
	private Listener onTextChangedListener;

	public TextEditorModel() {
		super();
		controller = new MultiLineTextController();
		controller.setOnTextChangedListener(this::onTextChanged);
		cursor = new Cursor(this);
		selection = new Selection(this);
	}

	// == TEXT CONTROL ==
	
	public void undo() {
		controller.undo();
	}

	public void redo() {
		controller.redo();
	}
	
	public String getText() {
		return controller.getText();
	}
	
	public void setText(String... text) {
		controller.setText(text);
	}

	public void insertChar(char ch) {
		controller.insertCharForLine(cursor.getRow(), cursor.getColumn(), ch);
	}

	public void insertString(String text) {
		controller.insertStringForLine(cursor.getRow(), cursor.getColumn(), text);
	}

	public void removeChar() {
		if (isBlank()) {
			return;
		}

		controller.removeCharForLine(cursor.getRow(), cursor.getColumn());
	}

	public void insertLine(String text) {
		controller.insertLine(cursor.getRow(), text);
	}

	public void addLine(String text) {
		controller.addLine(text);
	}

	public void clear() {
		controller.clear();
	}

	public boolean isBlank() {
		return controller.isBlank();
	}

	public void splitLine() {
		controller.splitLine(cursor.getRow(), cursor.getColumn());
	}

	public void mergeLines() {
		controller.mergeLines(cursor.getRow());
	}

	public String getLineText(int row) {
		return controller.getLineText(row);
	}

	public int getLineLength(int row) {
		return controller.getLineLength(row);
	}

	public int getLinesCount() {
		return controller.getLinesCount();
	}

	// == CURSOR CONTROL ==
	public void moveCursorTo(Direction direction, int repeat) {
		cursor.moveTo(direction, repeat);
	}

	public void moveCursorTo(Direction direction) {
		cursor.moveTo(direction);
	}

	public void moveCursorTo(int row, int column) {
		cursor.moveTo(row, column);
	}

	public void moveCursorToStart() {
		cursor.moveTo(0, 0);
	}

	public void moveCursorToEnd() {
		cursor.moveTo(getLinesCount() - 1, getLineLength(getLinesCount() - 1));
	}

	public void moveCursorToStartOfLine() {
		cursor.setColumn(0);
	}

	public void moveCursorToEndOfLine() {
		cursor.setColumn(getLineLength(getCursorRow()));
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

	// == SELECTION API ==
	
	public int getSelectStartRow() {
		return selection.getStartRow();
	}

	public void setSelectStartRow(int startRow) {
		selection.setStartRow(startRow);
	}

	public int getSelectEndRow() {
		return selection.getEndRow();
	}

	public void setSelectEndRow(int endRow) {
		selection.setEndRow(endRow);
	}

	public int getSelectStartColumn() {
		return selection.getStartColumn();
	}

	public void setSelectStartColumn(int startColumn) {
		selection.setStartColumn(startColumn);
	}

	public int getSelectEndColumn() {
		return selection.getEndColumn();
	}

	public void setSelectEndColumn(int endColumn) {
		selection.setEndColumn(endColumn);
	}
	
	public int getSelectEffectiveStartColumn() {
		return selection.getEffectiveStartColumn();
	}

	public int getSelectEffectiveEndColumn() {
		return selection.getEffectiveEndColumn();
	}

	public boolean isMultiLineSelected() {
		return selection.isMultiLineSelected();
	}

	public int getSelectEffectiveStartRow() {
		return selection.getEffectiveStartRow();
	}

	public int getSelectEffectiveEndRow() {
		return selection.getEffectiveEndRow();
	}

	public void setSelectStart(int row, int column) {
		selection.setStart(row, column);
	}

	public void setSelectEnd(int row, int column) {
		selection.setEnd(row, column);
	}

	public void setSelect(int startRow, int endRow, int startColumn, int endColumn) {
		selection.set(startRow, endRow, startColumn, endColumn);
	}

	public boolean isSelectEmpty() {
		return selection.isEmpty();
	}

	public void selectAll() {
		selection.selectAll();
	}
	
	public void resetSelection() {
		selection.reset();
	}
	
	public void setOnSelectingListener(Listener listener) {
		selection.setOnSelectingListener(listener);
	}
	
	public String getSelectedText() {
		if (isSelectEmpty()) {
			return "";
		}
		
		if (!isMultiLineSelected()) {
			final int minColumn = Math.min(getSelectEffectiveStartColumn(), getSelectEffectiveEndColumn());
			final int maxColumn = Math.max(getSelectEffectiveStartColumn(), getSelectEffectiveEndColumn());
			
			return controller.getLineText(getSelectStartRow()).substring(minColumn,maxColumn);
		} else {
			final StringBuilder sb = new StringBuilder();
			
			final int esr = getSelectEffectiveStartRow();
			final int eer = getSelectEffectiveEndRow();
			final int esc = getSelectEffectiveStartColumn();
			final int eec = getSelectEffectiveEndColumn();
			
			final int minColumn = Math.min(esc,eec);
			final int maxColumn = Math.max(esc,eec);
			
			
			for(int i = esr; i <= eer; i++) {
				if(i == esr) {
					sb.append(getLineText(esr).substring(minColumn));
					sb.append('\n');
				}
				
				if(i == eer) {
					sb.append(getLineText(eer).substring(0,maxColumn));
				}
				
				if(i != esr && i != eer) {
					sb.append(getLineText(i)).append('\n');
				}
			}
			
			return sb.toString();
		}
		
	}
	
	public void removeSelectedText() {
		if (isSelectEmpty()) {
			return;
		}
		
		final int esr = getSelectEffectiveStartRow();
		final int eer = getSelectEffectiveEndRow();
		final int sc = getSelectStartColumn();
		final int ec = getSelectEndColumn();
		
		if (!isMultiLineSelected()) {
			controller.getLine(esr).remove(sc, ec);
		} else {
			controller.getLine(esr).remove(sc, getLineLength(esr));
			controller.getLine(eer).remove(0, ec);
			
			for(int i = eer - 1; i > esr; i--) {
				controller.removeLine(i);
			}
			
			controller.mergeLines(esr);
		}
		
		cursor.moveTo(getSelectEffectiveStartRow(), getSelectStartColumn());
		selection.reset();
	}
	
	// == HOOKS ==
	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
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
		private final TextEditorModel model;
		private int column, row;

		public Cursor(TextEditorModel textAreaModel) {
			super();
			model = requireNonNull(textAreaModel, "textAreaModel");
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
		}

		public void moveTo(Direction direction) {
			moveTo(direction, 1);
		}

		public void moveTo(int row, int column) {
			setRow(row);
			setColumn(column);
		}

		// == PRIVATE API ==
		private MultiLineTextController getController() {
			return model.getController();
		}

		private int getClampedColumn(int column) {
			return (int) constrain(column, 0, getController().getLine(getRow()).length());
		}

		private int getClampedRow(int row) {
			return (int) constrain(row, 0, getController().getLinesCount() - 1);
		}

	}

	private static final class Selection {
		private final TextEditorModel model;
		private int startRow, endRow, startColumn, endColumn;
		private Listener onSelectingListener;
		
		public Selection(TextEditorModel model) {
			super();
			this.model = requireNonNull(model, "model");
		}

		public int getStartRow() {
			return getClampedRow(startRow);
		}

		public void setStartRow(int startRow) {
			this.startRow = getClampedRow(startRow);
			notifyOnSelecting();
		}

		public int getEndRow() {
			return getClampedRow(endRow);
		}

		public void setEndRow(int endRow) {
			this.endRow = getClampedRow(endRow);
			notifyOnSelecting();
		}

		public int getStartColumn() {
			return getClampedColumn(getStartRow(), startColumn);
		}
		
		public void setStartColumn(int startColumn) {
			this.startColumn = getClampedColumn(getStartRow(), startColumn);
			notifyOnSelecting();
		}

		public int getEndColumn() {
			return getClampedColumn(getEndRow(), endColumn);
		}

		public void setEndColumn(int endColumn) {
			this.endColumn = getClampedColumn(getEndRow(), endColumn);
			notifyOnSelecting();
		}

		public boolean isMultiLineSelected() {
			return getStartRow() != getEndRow();
		}

		public int getEffectiveStartRow() {
			return Math.min(getStartRow(), getEndRow());
		}

		public int getEffectiveEndRow() {
			return Math.max(getStartRow(), getEndRow());
		}
		
		public int getEffectiveStartColumn() {
			if (getStartRow() <= getEndRow()) {
				return getStartColumn();
			} else {
				return getEndColumn();
			}
		}
		
		public int getEffectiveEndColumn() {
			if (getStartRow() > getEndRow()) {
				return getStartColumn();
			} else {
				return getEndColumn();
			}
		}

		public void setStart(int row, int column) {
			setStartRow(row);
			setStartColumn(column);
		}
		
		public void setEnd(int row, int column) {
			setEndRow(row);
			setEndColumn(column);
		}
		
		public void set(int startRow, int endRow, int startColumn, int endColumn) {
			setStart(startRow, startColumn);
			setEnd(endRow, endColumn);
		}
		
		public boolean isEmpty() {
			return getStartRow() == getEndRow() && getStartColumn() == getEndColumn();
		}
		
		public void reset() {
			set(0,0,0,0);
		}
		
		public void selectAll() {
			set(0,model.getLinesCount() - 1, 0, model.getLineLength(model.getLinesCount() - 1));
		}
		
		public void setOnSelectingListener(Listener onSelectingListener) {
			this.onSelectingListener = requireNonNull(onSelectingListener,"onSelectingListener");
		}
		
		private void notifyOnSelecting() {
			if (onSelectingListener != null) {
				onSelectingListener.action();
			}
		}

		// == PRIVATE API ==
		private int getClampedRow(int row) {
			return (int) constrain(row, 0, model.getLinesCount() - 1);
		}

		private int getClampedColumn(int row, int column) {
			return (int) constrain(column, 0, model.getLineLength(getClampedRow(row)));
		}
	}
}