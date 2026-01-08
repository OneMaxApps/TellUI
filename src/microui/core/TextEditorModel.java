package microui.core;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import microui.core.controller.MultiLineTextController;
import microui.event.Listener;

/**
 * A comprehensive text editor model that manages multi-line text editing operations,
 * cursor positioning, and text selection. Provides a complete API for text manipulation
 * in a multi-line editing environment.
 * 
 * <p>TextEditorModel handles:
 * <ul>
 *   <li>Multi-line text storage and manipulation</li>
 *   <li>Cursor navigation and positioning</li>
 *   <li>Text selection with single and multi-line support</li>
 *   <li>Undo/redo functionality</li>
 *   <li>Line-based operations (split, merge, insert, remove)</li>
 *   <li>Event notifications for text changes</li>
 * </ul></p>
 * 
 * <p>This model is designed to be used by text editing UI components that need
 * sophisticated text manipulation capabilities.</p>
 */
public final class TextEditorModel {
    private final MultiLineTextController controller;
    private final Cursor cursor;
    private final Selection selection;
    private final TextFragment textFragment;
    private Listener onTextChangedListener;

    /**
     * Constructs a TextEditorModel with empty text content.
     * Initializes the underlying controller, cursor, selection, and text fragment components.
     */
    public TextEditorModel() {
        super();
        controller = new MultiLineTextController();
        controller.setOnTextChangedListener(this::onTextChanged);
        cursor = new Cursor(this);
        selection = new Selection(this);
        textFragment = new TextFragment(this);
    }

    // == TEXT CONTROL ==

    /**
     * Undoes the last text modification.
     */
    public void undo() {
        controller.undo();
    }

    /**
     * Redoes the last undone text modification.
     */
    public void redo() {
        controller.redo();
    }

    /**
     * Gets the complete text content as a single string with line separators.
     *
     * @return the complete text content
     */
    public String getText() {
        return controller.getText();
    }

    /**
     * Gets the text content from the beginning up to the current cursor position.
     *
     * @return the text before the cursor
     */
    public String getTextUntilCursor() {
        return textFragment.getTextUntilCursor();
    }

    /**
     * Gets the text content from the current cursor position to the end.
     *
     * @return the text after the cursor
     */
    public String getTextAfterCursor() {
        return textFragment.getTextAfterCursor();
    }

    /**
     * Sets the text content from an array of strings (each string becomes a line).
     *
     * @param text the lines of text to set
     */
    public void setText(String... text) {
        controller.setText(text);
    }

    /**
     * Inserts a character at the current cursor position.
     *
     * @param ch the character to insert
     */
    public void insertChar(char ch) {
        controller.insertCharForLine(cursor.getRow(), cursor.getColumn(), ch);
    }

    /**
     * Inserts a string at the current cursor position.
     *
     * @param text the string to insert
     */
    public void insertString(String text) {
        controller.insertStringForLine(cursor.getRow(), cursor.getColumn(), text);
    }

    /**
     * Removes the character at the current cursor position.
     * Does nothing if the text is blank.
     */
    public void removeChar() {
        if (isBlank()) {
            return;
        }

        controller.removeCharForLine(cursor.getRow(), cursor.getColumn());
    }

    /**
     * Inserts a new line with the specified text at the current cursor row.
     *
     * @param text the text for the new line
     */
    public void insertLine(String text) {
        controller.insertLine(cursor.getRow(), text);
    }

    /**
     * Adds a new line with the specified text at the end of the text.
     *
     * @param text the text for the new line
     */
    public void addLine(String text) {
        controller.addLine(text);
    }

    /**
     * Clears all text content.
     */
    public void clear() {
        controller.clear();
    }

    /**
     * Checks if the text content is blank (empty).
     *
     * @return true if the text is blank, false otherwise
     */
    public boolean isBlank() {
        return controller.isBlank();
    }

    /**
     * Splits the current line at the cursor position, creating a new line.
     */
    public void splitLine() {
        controller.splitLine(cursor.getRow(), cursor.getColumn());
    }

    /**
     * Merges the current line with the next line.
     * The cursor row must not be the last line.
     */
    public void mergeLines() {
        controller.mergeLines(cursor.getRow());
    }

    /**
     * Gets the text of a specific line.
     *
     * @param row the line index (0-based)
     * @return the text of the specified line
     */
    public String getLineText(int row) {
        return controller.getLineText(row);
    }

    /**
     * Gets the length (number of characters) of a specific line.
     *
     * @param row the line index (0-based)
     * @return the length of the specified line
     */
    public int getLineLength(int row) {
        return controller.getLineLength(row);
    }

    /**
     * Gets the total number of lines in the text.
     *
     * @return the count of lines
     */
    public int getLineCount() {
        return controller.getLinesCount();
    }

    // == CURSOR CONTROL ==

    /**
     * Moves the cursor in the specified direction by the specified number of steps.
     *
     * @param direction the direction to move (LEFT, UP, RIGHT, DOWN)
     * @param repeat the number of steps to move
     */
    public void moveCursorTo(Direction direction, int repeat) {
        cursor.moveTo(direction, repeat);
    }

    /**
     * Moves the cursor one step in the specified direction.
     *
     * @param direction the direction to move (LEFT, UP, RIGHT, DOWN)
     */
    public void moveCursorTo(Direction direction) {
        cursor.moveTo(direction);
    }

    /**
     * Moves the cursor to the specified row and column.
     *
     * @param row the target row (0-based)
     * @param column the target column (0-based)
     */
    public void moveCursorTo(int row, int column) {
        cursor.moveTo(row, column);
    }

    /**
     * Moves the cursor to the start of the text (row 0, column 0).
     */
    public void moveCursorToStartOfText() {
        cursor.moveTo(0, 0);
    }

    /**
     * Moves the cursor to the end of the text (last row, end of last line).
     */
    public void moveCursorToEndOfText() {
        cursor.moveTo(getLineCount() - 1, getLineLength(getLineCount() - 1));
    }

    /**
     * Moves the cursor to the start of the current line (column 0).
     */
    public void moveCursorToStartOfLine() {
        cursor.setColumn(0);
    }

    /**
     * Moves the cursor to the end of the current line.
     */
    public void moveCursorToEndOfLine() {
        cursor.setColumn(getLineLength(getCursorRow()));
    }

    /**
     * Gets the current cursor column (0-based).
     *
     * @return the current column position
     */
    public int getCursorColumn() {
        return cursor.getColumn();
    }

    /**
     * Sets the cursor column position.
     *
     * @param column the column to set (will be constrained to valid range)
     */
    public void setCursorColumn(int column) {
        cursor.setColumn(column);
    }

    /**
     * Gets the current cursor row (0-based).
     *
     * @return the current row position
     */
    public int getCursorRow() {
        return cursor.getRow();
    }

    /**
     * Sets the cursor row position.
     *
     * @param row the row to set (will be constrained to valid range)
     */
    public void setCursorRow(int row) {
        cursor.setRow(row);
    }

    /**
     * Checks if the cursor is at the start of all lines (first row).
     *
     * @return true if cursor is on the first row, false otherwise
     */
    public boolean cursorAtStartOfLines() {
        return cursor.getRow() == 0;
    }

    /**
     * Checks if the cursor is at the end of all lines (last row).
     *
     * @return true if cursor is on the last row, false otherwise
     */
    public boolean cursorAtEndOfLines() {
        return cursor.getRow() == getLineCount() - 1;
    }

    /**
     * Checks if the cursor is at the start of the current line (column 0).
     *
     * @return true if cursor is at column 0, false otherwise
     */
    public boolean cursorAtStartOfLine() {
        return cursor.getColumn() == 0;
    }

    /**
     * Checks if the cursor is at the end of the current line.
     *
     * @return true if cursor is at the line end, false otherwise
     */
    public boolean cursorAtEndOfLine() {
        return cursor.getColumn() == getLineLength(getCursorRow());
    }

    /**
     * Checks if the cursor is at the start of the text (row 0, column 0).
     *
     * @return true if cursor is at the absolute start, false otherwise
     */
    public boolean cursorAtStartOfText() {
        return cursorAtStartOfLines() && cursorAtStartOfLine();
    }

    /**
     * Checks if the cursor is at the end of the text (last row, end of last line).
     *
     * @return true if cursor is at the absolute end, false otherwise
     */
    public boolean cursorAtEndOfText() {
        return cursorAtEndOfLines() && cursorAtEndOfLine();
    }

    // == SELECTION API ==

    /**
     * Gets the selection start row.
     *
     * @return the selection start row
     */
    public int getSelectionStartRow() {
        return selection.getStartRow();
    }

    /**
     * Sets the selection start row.
     *
     * @param startRow the start row to set (will be constrained to valid range)
     */
    public void setSelectionStartRow(int startRow) {
        selection.setStartRow(startRow);
    }

    /**
     * Gets the selection end row.
     *
     * @return the selection end row
     */
    public int getSelectionEndRow() {
        return selection.getEndRow();
    }

    /**
     * Sets the selection end row.
     *
     * @param endRow the end row to set (will be constrained to valid range)
     */
    public void setSelectionEndRow(int endRow) {
        selection.setEndRow(endRow);
    }

    /**
     * Gets the selection start column.
     *
     * @return the selection start column
     */
    public int getSelectionStartColumn() {
        return selection.getStartColumn();
    }

    /**
     * Sets the selection start column.
     *
     * @param startColumn the start column to set (will be constrained to valid range)
     */
    public void setSelectionStartColumn(int startColumn) {
        selection.setStartColumn(startColumn);
    }

    /**
     * Gets the selection end column.
     *
     * @return the selection end column
     */
    public int getSelectionEndColumn() {
        return selection.getEndColumn();
    }

    /**
     * Sets the selection end column.
     *
     * @param endColumn the end column to set (will be constrained to valid range)
     */
    public void setSelectionEndColumn(int endColumn) {
        selection.setEndColumn(endColumn);
    }

    /**
     * Gets the effective start column (minimum of start and end columns).
     *
     * @return the effective start column
     */
    public int getSelectionEffectiveStartColumn() {
        return selection.getEffectiveStartColumn();
    }

    /**
     * Gets the effective end column (maximum of start and end columns).
     *
     * @return the effective end column
     */
    public int getSelectionEffectiveEndColumn() {
        return selection.getEffectiveEndColumn();
    }

    /**
     * Checks if the selection spans multiple lines.
     *
     * @return true if selection spans multiple lines, false for single-line selection
     */
    public boolean isMultiLineSelected() {
        return selection.isMultiLineSelected();
    }

    /**
     * Gets the effective start row (minimum of start and end rows).
     *
     * @return the effective start row
     */
    public int getSelectionEffectiveStartRow() {
        return selection.getEffectiveStartRow();
    }

    /**
     * Gets the effective end row (maximum of start and end rows).
     *
     * @return the effective end row
     */
    public int getSelectionEffectiveEndRow() {
        return selection.getEffectiveEndRow();
    }

    /**
     * Sets the selection start position.
     *
     * @param row the start row
     * @param column the start column
     */
    public void setSelectionStart(int row, int column) {
        selection.setStart(row, column);
    }

    /**
     * Sets the selection end position.
     *
     * @param row the end row
     * @param column the end column
     */
    public void setSelectionEnd(int row, int column) {
        selection.setEnd(row, column);
    }

    /**
     * Sets the complete selection range.
     *
     * @param startRow the start row
     * @param endRow the end row
     * @param startColumn the start column
     * @param endColumn the end column
     */
    public void setSelection(int startRow, int endRow, int startColumn, int endColumn) {
        selection.set(startRow, endRow, startColumn, endColumn);
    }

    /**
     * Checks if any text is currently selected.
     *
     * @return true if text is selected, false otherwise
     */
    public boolean hasSelection() {
        return !selection.isEmpty();
    }

    /**
     * Selects all text in the document.
     */
    public void selectAll() {
        selection.selectAll();
    }

    /**
     * Clears the current selection (sets to empty selection at position 0,0).
     */
    public void resetSelection() {
        selection.reset();
    }

    /**
     * Sets a listener to be notified when selection changes.
     *
     * @param listener the selection change listener
     */
    public void setOnSelectingListener(Listener listener) {
        selection.setOnSelectingListener(listener);
    }

    /**
     * Gets the currently selected text.
     *
     * @return the selected text, or empty string if nothing is selected
     */
    public String getSelectedText() {
        if (!hasSelection()) {
            return "";
        }

        if (isMultiLineSelected()) {
            final int esr = getSelectionEffectiveStartRow();
            final int eer = getSelectionEffectiveEndRow();
            final int esc = getSelectionEffectiveStartColumn();
            final int eec = getSelectionEffectiveEndColumn();

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
            final int row = getSelectionStartRow();
            final int esc = getSelectionEffectiveStartColumn();
            final int eec = getSelectionEffectiveEndColumn();

            final int startColumn = min(esc, eec);
            final int endColumn = max(esc, eec);

            return getLineText(row).substring(startColumn, endColumn);
        }
    }
    
    /**
     * Removes the currently selected text.
     * If no text is selected, does nothing.
     * After removal, the cursor is positioned at the start of the removed selection.
     */
    public void removeSelectedText() {
        if (!hasSelection()) {
            return;
        }

        final int esr = getSelectionEffectiveStartRow();
        final int esc = getSelectionEffectiveStartColumn();
        final int eec = getSelectionEffectiveEndColumn();

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

    /**
     * Sets a listener to be notified when the text content changes.
     *
     * @param onTextChangedListener the text change listener
     * @throws NullPointerException if the listener is null
     */
    public void setOnTextChangedListener(Listener onTextChangedListener) {
        this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
    }

    // == PRIVATE API ==

    /**
     * Gets the underlying text controller.
     *
     * @return the MultiLineTextController instance
     */
    private MultiLineTextController getController() {
        return controller;
    }

    /**
     * Notifies the text change listener when text content changes.
     */
    private void onTextChanged() {
        if (onTextChangedListener != null) {
            onTextChangedListener.action();
        }
    }

    /**
     * Internal class for managing cursor position within the text.
     */
    private static final class Cursor {
        private final TextEditorModel model;
        private int column, row;

        /**
         * Constructs a Cursor for the text editor model.
         *
         * @param textAreaModel the parent text editor model
         * @throws NullPointerException if textAreaModel is null
         */
        public Cursor(TextEditorModel textAreaModel) {
            super();
            model = requireNonNull(textAreaModel, "textAreaModel");
        }

        /**
         * Gets the current column position (clamped to valid range).
         *
         * @return the current column
         */
        public int getColumn() {
            return getClampedColumn(column);
        }

        /**
         * Sets the column position (clamped to valid range).
         *
         * @param column the column to set
         */
        public void setColumn(int column) {
            this.column = getClampedColumn(column);
        }

        /**
         * Gets the current row position (clamped to valid range).
         *
         * @return the current row
         */
        public int getRow() {
            return getClampedRow(row);
        }

        /**
         * Sets the row position (clamped to valid range).
         *
         * @param row the row to set
         */
        public void setRow(int row) {
            this.row = getClampedRow(row);
        }

        /**
         * Moves the cursor in the specified direction by the specified number of steps.
         *
         * @param direction the direction to move
         * @param repeat the number of steps to move
         * @throws NullPointerException if direction is null
         */
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

        /**
         * Moves the cursor one step in the specified direction.
         *
         * @param direction the direction to move
         */
        public void moveTo(Direction direction) {
            moveTo(direction, 1);
        }

        /**
         * Moves the cursor to the specified row and column.
         *
         * @param row the target row
         * @param column the target column
         */
        public void moveTo(int row, int column) {
            setRow(row);
            setColumn(column);
        }

        // == PRIVATE API ==

        /**
         * Gets the underlying text controller.
         *
         * @return the MultiLineTextController instance
         */
        private MultiLineTextController getController() {
            return model.getController();
        }

        /**
         * Clamps a column value to the valid range for the current row.
         *
         * @param column the column value to clamp
         * @return the clamped column value
         */
        private int getClampedColumn(int column) {
            return (int) constrain(column, 0, getController().getLine(getRow()).length());
        }

        /**
         * Clamps a row value to the valid range.
         *
         * @param row the row value to clamp
         * @return the clamped row value
         */
        private int getClampedRow(int row) {
            return (int) constrain(row, 0, getController().getLinesCount() - 1);
        }
    }

    /**
     * Provides constants for cursor direction
     */
    public enum Direction {
        /** Left direction, typically used for moving cursor left or scrolling left. */
        LEFT,
        
        /** Up direction, typically used for moving cursor up or scrolling up. */
        UP,
        
        /** Right direction, typically used for moving cursor right or scrolling right. */
        RIGHT,
        
        /** Down direction, typically used for moving cursor down or scrolling down. */
        DOWN;
    }
    
    /**
     * Internal class for managing text selection within the document.
     */
    private static final class Selection {
        private final TextEditorModel model;
        private int startRow, endRow, startColumn, endColumn;
        private Listener onSelectingListener;

        /**
         * Constructs a Selection for the text editor model.
         *
         * @param model the parent text editor model
         * @throws NullPointerException if model is null
         */
        public Selection(TextEditorModel model) {
            super();
            this.model = requireNonNull(model, "model");
        }

        /**
         * Gets the selection start row (clamped to valid range).
         *
         * @return the start row
         */
        public int getStartRow() {
            return getClampedRow(startRow);
        }

        /**
         * Sets the selection start row (clamped to valid range).
         *
         * @param startRow the start row to set
         */
        public void setStartRow(int startRow) {
            this.startRow = getClampedRow(startRow);
            notifyOnSelecting();
        }

        /**
         * Gets the selection end row (clamped to valid range).
         *
         * @return the end row
         */
        public int getEndRow() {
            return getClampedRow(endRow);
        }

        /**
         * Sets the selection end row (clamped to valid range).
         *
         * @param endRow the end row to set
         */
        public void setEndRow(int endRow) {
            this.endRow = getClampedRow(endRow);
            notifyOnSelecting();
        }

        /**
         * Gets the selection start column (clamped to valid range for the start row).
         *
         * @return the start column
         */
        public int getStartColumn() {
            return getClampedColumn(getStartRow(), startColumn);
        }

        /**
         * Sets the selection start column (clamped to valid range for the start row).
         *
         * @param startColumn the start column to set
         */
        public void setStartColumn(int startColumn) {
            this.startColumn = getClampedColumn(getStartRow(), startColumn);
            notifyOnSelecting();
        }

        /**
         * Gets the selection end column (clamped to valid range for the end row).
         *
         * @return the end column
         */
        public int getEndColumn() {
            return getClampedColumn(getEndRow(), endColumn);
        }

        /**
         * Sets the selection end column (clamped to valid range for the end row).
         *
         * @param endColumn the end column to set
         */
        public void setEndColumn(int endColumn) {
            this.endColumn = getClampedColumn(getEndRow(), endColumn);
            notifyOnSelecting();
        }

        /**
         * Checks if the selection spans multiple lines.
         *
         * @return true if selection spans multiple lines, false otherwise
         */
        public boolean isMultiLineSelected() {
            return getStartRow() != getEndRow();
        }

        /**
         * Gets the effective start row (minimum of start and end rows).
         *
         * @return the effective start row
         */
        public int getEffectiveStartRow() {
            return Math.min(getStartRow(), getEndRow());
        }

        /**
         * Gets the effective end row (maximum of start and end rows).
         *
         * @return the effective end row
         */
        public int getEffectiveEndRow() {
            return Math.max(getStartRow(), getEndRow());
        }

        /**
         * Gets the effective start column.
         * For forward selections (start before end), returns start column.
         * For backward selections (end before start), returns end column.
         *
         * @return the effective start column
         */
        public int getEffectiveStartColumn() {
            if (getStartRow() <= getEndRow()) {
                return getStartColumn();
            } else {
                return getEndColumn();
            }
        }

        /**
         * Gets the effective end column.
         * For forward selections (start before end), returns end column.
         * For backward selections (end before start), returns start column.
         *
         * @return the effective end column
         */
        public int getEffectiveEndColumn() {
            if (getStartRow() > getEndRow()) {
                return getStartColumn();
            } else {
                return getEndColumn();
            }
        }

        /**
         * Sets the selection start position.
         *
         * @param row the start row
         * @param column the start column
         */
        public void setStart(int row, int column) {
            setStartRow(row);
            setStartColumn(column);
        }

        /**
         * Sets the selection end position.
         *
         * @param row the end row
         * @param column the end column
         */
        public void setEnd(int row, int column) {
            setEndRow(row);
            setEndColumn(column);
        }

        /**
         * Sets the complete selection range.
         *
         * @param startRow the start row
         * @param endRow the end row
         * @param startColumn the start column
         * @param endColumn the end column
         */
        public void set(int startRow, int endRow, int startColumn, int endColumn) {
            setStart(startRow, startColumn);
            setEnd(endRow, endColumn);
        }

        /**
         * Checks if the selection is empty (covers no text).
         *
         * @return true if selection is empty, false otherwise
         */
        public boolean isEmpty() {
            return getStartRow() == getEndRow() && getStartColumn() == getEndColumn();
        }

        /**
         * Resets the selection to empty (position 0,0).
         */
        public void reset() {
            set(0, 0, 0, 0);
        }

        /**
         * Selects all text in the document.
         */
        public void selectAll() {
            set(0, model.getLineCount() - 1, 0, model.getLineLength(model.getLineCount() - 1));
        }

        /**
         * Sets a listener to be notified when selection changes.
         *
         * @param onSelectingListener the selection change listener
         * @throws NullPointerException if the listener is null
         */
        public void setOnSelectingListener(Listener onSelectingListener) {
            this.onSelectingListener = requireNonNull(onSelectingListener, "onSelectingListener");
        }

        /**
         * Notifies the selection change listener.
         */
        private void notifyOnSelecting() {
            if (onSelectingListener != null) {
                onSelectingListener.action();
            }
        }

        // == PRIVATE API ==

        /**
         * Clamps a row value to the valid range.
         *
         * @param row the row value to clamp
         * @return the clamped row value
         */
        private int getClampedRow(int row) {
            return (int) constrain(row, 0, model.getLineCount() - 1);
        }

        /**
         * Clamps a column value to the valid range for the specified row.
         *
         * @param row the row for column range validation
         * @param column the column value to clamp
         * @return the clamped column value
         */
        private int getClampedColumn(int row, int column) {
            return (int) constrain(column, 0, model.getLineLength(getClampedRow(row)));
        }
    }

    /**
     * Internal class for extracting text fragments from the document.
     */
    private static final class TextFragment {
        private final TextEditorModel model;
        private final StringBuilder sb;
        
        /**
         * Constructs a TextFragment for the text editor model.
         *
         * @param textEditorModel the parent text editor model
         * @throws NullPointerException if textEditorModel is null
         */
        public TextFragment(TextEditorModel textEditorModel) {
            super();
            this.model = requireNonNull(textEditorModel, "textEditorModel");
            sb = new StringBuilder();
        }

        /**
         * Gets the text from the beginning up to the current cursor position.
         *
         * @return the text before the cursor
         */
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

        /**
         * Gets the text from the current cursor position to the end.
         *
         * @return the text after the cursor
         */
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
        
        /**
         * Gets the text from the beginning up to the start of the current selection.
         *
         * @return the text before the selection, or empty string if no selection
         */
        public String getTextUntilSelection() {
            if (!model.hasSelection()) {
                return "";
            }
            
            sb.setLength(0);
            
            final int esr = model.getSelectionEffectiveStartRow();
            for (int i = 0; i <= esr; i++) {
                final String text = model.getLineText(i);
                
                if (i != esr) {
                    sb.append(text).append('\n');
                } else {
                    sb.append(text.substring(0, model.getSelectionEffectiveStartColumn()));
                }
            }
            
            return sb.toString();
        }
        
        /**
         * Gets the text from the end of the current selection to the end.
         *
         * @return the text after the selection, or empty string if no selection
         */
        public String getTextAfterSelection() {
            if (!model.hasSelection()) {
                return "";
            }
            
            sb.setLength(0);
            
            final int eer = model.getSelectionEffectiveEndRow();
            final int esc = model.getSelectionEffectiveStartColumn();
            final int eec = model.getSelectionEffectiveEndColumn();
            
            final int linesCount = model.getLineCount();
            
            for (int i = eer; i < linesCount; i++) {
                final String text = model.getLineText(i);
                
                if (i == eer) {
                    if (model.isMultiLineSelected()) {
                        sb.append(text.substring(eec));
                    } else {
                        sb.append(text.substring(max(esc, eec)));
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