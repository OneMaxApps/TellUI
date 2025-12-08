package microui.core;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import microui.constants.Direction;
import microui.core.controller.MultiLineTextController;
import microui.event.Listener;

public final class TextEditorModel {
	private final MultiLineTextController controller;
	private final Cursor cursor;
	private final Selection selection;
	private final TextFragment textFragment;
	private Listener onTextChangedListener;

	public TextEditorModel() {
		super();
		controller = new MultiLineTextController();
		controller.setOnTextChangedListener(this::onTextChanged);
		cursor = new Cursor(this);
		selection = new Selection(this);
		textFragment = new TextFragment(this);
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

	public String getTextUntilCursor() {
		return textFragment.getTextUntilCursor();
	}

	public String getTextAfterCursor() {
		return textFragment.getTextAfterCursor();
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

	public int getLineCount() {
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

	public void moveCursorToStartOfText() {
		cursor.moveTo(0, 0);
	}

	public void moveCursorToEndOfText() {
		cursor.moveTo(getLineCount() - 1, getLineLength(getLineCount() - 1));
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

	public boolean cursorAtStartOfLines() {
		return cursor.getRow() == 0;
	}

	public boolean cursorAtEndOfLines() {
		return cursor.getRow() == getLineCount() - 1;
	}

	public boolean cursorAtStartOfLine() {
		return cursor.getColumn() == 0;
	}

	public boolean cursorAtEndOfLine() {
		return cursor.getColumn() == getLineLength(getCursorRow());
	}

	public boolean cursorAtStartOfText() {
		return cursorAtStartOfLines() && cursorAtStartOfLine();
	}

	public boolean cursorAtEndOfText() {
		return cursorAtEndOfLines() && cursorAtEndOfLine();
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

	public boolean hasSelection() {
		return !selection.isEmpty();
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

		if (isMultiLineSelected()) {

			final int esr = getSelectEffectiveStartRow();
			final int eer = getSelectEffectiveEndRow();
			final int esc = getSelectEffectiveStartColumn();
			final int eec = getSelectEffectiveEndColumn();

			final StringBuilder sb = new StringBuilder();

			for (int i = esr; i <= eer; i++) {
				if (i == esr) {
					sb.append(getLineText(esr).substring(esc)).append('\n');
				}

				if (i != esr && i != eer) {
					sb.append(getLineText(i)).append('\n');
				}

				if (i == eer) {
					sb.append(getLineText(eer).substring(0, eec));
				}
			}

			return sb.toString();

		} else {
			final int row = getSelectStartRow();
			final int esc = getSelectEffectiveStartColumn();
			final int eec = getSelectEffectiveEndColumn();

			final int startColumn = min(esc, eec);
			final int endColumn = max(esc, eec);

			return getLineText(row).substring(startColumn, endColumn);
		}
	}
	
	public void removeSelectedText() {
		if (isSelectEmpty()) {
			return;
		}

		final int esr = getSelectEffectiveStartRow();
		final int esc = getSelectEffectiveStartColumn();
		final int eec = getSelectEffectiveEndColumn();

		if (!isMultiLineSelected()) {
			controller.getLine(esr).remove(esc, eec);
		} else {
			final String text = textFragment.getTextUntilSelection() + textFragment.getTextAfterSelection();
			setText(text.split("\n"));
		}

		moveCursorTo(esr, min(esc, eec));

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
			set(0, 0, 0, 0);
		}

		public void selectAll() {
			set(0, model.getLineCount() - 1, 0, model.getLineLength(model.getLineCount() - 1));
		}

		public void setOnSelectingListener(Listener onSelectingListener) {
			this.onSelectingListener = requireNonNull(onSelectingListener, "onSelectingListener");
		}

		private void notifyOnSelecting() {
			if (onSelectingListener != null) {
				onSelectingListener.action();
			}
		}

		// == PRIVATE API ==
		private int getClampedRow(int row) {
			return (int) constrain(row, 0, model.getLineCount() - 1);
		}

		private int getClampedColumn(int row, int column) {
			return (int) constrain(column, 0, model.getLineLength(getClampedRow(row)));
		}
	}

	private static final class TextFragment {
		private final TextEditorModel model;
		private final StringBuilder sb;
		
		public TextFragment(TextEditorModel textEditorModel) {
			super();
			this.model = requireNonNull(textEditorModel, "textEditorModel");

			sb = new StringBuilder();
		}

		public String getTextUntilCursor() {
			
			sb.setLength(0);
			final int startRow = 0;
			final int endRow = model.cursor.getRow();

			for (int i = startRow; i <= endRow; i++) {
				if (i != endRow) {
					sb.append(model.getLineText(i)).append('\n');
				} else {
					sb.append(model.getLineText(i).substring(0, model.cursor.getColumn()));
				}
			}

			return sb.toString();

		}

		public String getTextAfterCursor() {
			
			sb.setLength(0);

			final int sr = model.getCursorRow();
			final int er = model.getLineCount();

			for (int i = sr; i < er; i++) {
				final boolean onStartLine = i == sr;
				final boolean onEndLine = i == er - 1;

				if (onStartLine) {
					final int cc = model.getCursorColumn();
					sb.append(model.getLineText(sr).substring(cc));
				} else {
					sb.append(model.getLineText(i));
				}

				if (!onEndLine) {
					sb.append('\n');
				}
			}

			return sb.toString();
		}
		
		public String getTextUntilSelection() {
			if (!model.hasSelection()) {
				return "";
			}
			
			sb.setLength(0);
			
			final int esr = model.getSelectEffectiveStartRow();
			for (int i = 0; i <= esr; i++) {
				final String text = model.getLineText(i);
				
				if (i != esr) {
					sb.append(text).append('\n');
				} else {
					sb.append(text.substring(0, model.getSelectEffectiveStartColumn()));
				}
			}
			
			return sb.toString();
		}
		
		public String getTextAfterSelection() {
			if (!model.hasSelection()) {
				return "";
			}
			
			sb.setLength(0);
			
			final int eer =  model.getSelectEffectiveEndRow();
			final int esc = model.getSelectEffectiveStartColumn();
			final int eec = model.getSelectEffectiveEndColumn();
			
			final int linesCount = model.getLineCount();
			
			for (int i = eer; i < linesCount; i++) {
				final String text = model.getLineText(i);
				
				if (i == eer) {
					if (model.isMultiLineSelected()) {
						sb.append(text.substring(eec));
					} else {
						sb.append(text.substring(max(esc,eec)));
					}
				} else {
					sb.append(text);
				}
				
				if (i != linesCount - 1) {
					sb.append('\n');
				}
			}
			
			return sb.toString();
		}

	}
}