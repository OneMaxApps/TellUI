package microui.core.controller;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import microui.core.interfaces.InputFilter;
import microui.event.Listener;

/**
 * Controller for managing multi-line text with line-based operations and
 * undo/redo functionality. Provides comprehensive text manipulation
 * capabilities across multiple lines, including line insertion, removal,
 * splitting, merging, and character-level editing.
 * 
 * @see SingleLineTextController
 * @see Listener
 */
public final class MultiLineTextController {
	private static final String EMPTY_TEXT;
	private static final byte MIN_LINES_COUNT;
	private final List<SingleLineTextController> list;
	private final UndoRedoManager undoRedoManager;
	private InputFilter inputFilter;
	private StringBuilder adapterSb;
	private String cachedText;
	private Listener onTextChangedListener;
	private boolean textChanged;

	static {
		MIN_LINES_COUNT = 1;
		EMPTY_TEXT = "";
	}

	{
		cachedText = EMPTY_TEXT;
	}

	/**
	 * Constructs a new multi‑line text controller with one empty line.
	 * The default input filter allows letters, digits, and common punctuation.
	 */
	public MultiLineTextController() {
		super();
		list = new ArrayList<SingleLineTextController>();
		undoRedoManager = new UndoRedoManager(this);
		setInputFilter((c) -> "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*".indexOf(c) >= 0 || Character.isLetterOrDigit(c));
		addLine(EMPTY_TEXT);
		
	}

	// == PUBLIC API ==
	
	/**
	 * Returns the current input filter used for character validation.
	 *
	 * @return the input filter (never {@code null})
	 */
	public InputFilter getInputFilter() {
		return inputFilter;
	}

	/**
	 * Sets the input filter used for character validation on all lines.
	 *
	 * @param inputFilter the new input filter (must not be {@code null})
	 * @throws NullPointerException if {@code inputFilter} is {@code null}
	 */
	public void setInputFilter(InputFilter inputFilter) {
		if (this.inputFilter == inputFilter) {
			return;
		}
		
		this.inputFilter = requireNonNull(inputFilter,"inputFilter");
		
		for(int i = 0; i < list.size(); i++) {
			list.get(i).setInputFilter(inputFilter);
		}
	}

	/**
	 * Performs an undo operation, reverting to the previous text state.
	 */
	public void undo() {
		undoRedoManager.undo();
	}	

	/**
	 * Performs a redo operation, reapplying the next text state.
	 */
	public void redo() {
		undoRedoManager.redo();
	}

	/**
	 * Returns a string representation of the undo stack for debugging.
	 * 
	 * @return string representation of undo stack contents
	 */
	public String getUndoStack() {
		return undoRedoManager.getUndoStack();
	}

	/**
	 * Returns a string representation of the redo stack for debugging.
	 * 
	 * @return string representation of redo stack contents
	 */
	public String getRedoStack() {
		return undoRedoManager.getRedoStack();
	}

	/**
	 * Checks if the entire text content is blank (contains only whitespace).
	 * 
	 * @return true if there's only one line and it's empty, false otherwise
	 */
	public boolean isBlank() {
		return hasOnlyOneLine() && getLine(0).isEmpty();
	}

	/**
	 * Returns the number of lines in the text.
	 * 
	 * @return the count of lines
	 */
	public int getLinesCount() {
		return list.size();
	}

	/**
	 * Adds a new line with the specified text at the end.
	 * 
	 * @param text the text for the new line (must not be {@code null})
	 * @throws NullPointerException if text is null
	 */
	public void addLine(String text) {
		addLineSilently(text);
		notifyTextChanged();
	}

	/**
	 * Inserts a new line with the specified text at the given index.
	 * 
	 * @param index the position to insert the new line (0‑based, will be constrained to a valid range)
	 * @param text  the text for the new line (must not be {@code null})
	 * @throws NullPointerException if text is null
	 */
	public void insertLine(int index, String text) {
		index = (int) constrain(index, 0, getLinesCount());

		list.add(index, new SingleLineTextController(text,getInputFilter()));
		list.get(index).setOnTextChangedListener(this::notifyTextChanged);

		notifyTextChanged();
	}

	/**
	 * Removes a line at the specified index. If only one line remains, clears its
	 * content instead of removing.
	 * 
	 * @param index the index of the line to remove (0‑based, will be constrained to a valid range)
	 */
	public void removeLine(int index) {
		if (getLinesCount() == MIN_LINES_COUNT) {
			list.get(0).clear();
		} else {
			list.remove(getClampedIndex(index));
		}

		notifyTextChanged();
	}

	/**
	 * Inserts a character at a specific position in a line.
	 * 
	 * @param row    the line index (0‑based, will be constrained to a valid range)
	 * @param column the character position within the line (will be constrained to a valid range)
	 * @param ch     the character to insert
	 */
	public void insertCharForLine(int row, int column, char ch) {
		list.get(getClampedIndex(row)).insert(column, ch);
	}

	/**
	 * Inserts a string at a specific position in a line.
	 * 
	 * @param row    the line index (0‑based, will be constrained to a valid range)
	 * @param column the character position within the line (will be constrained to a valid range)
	 * @param str    the string to insert (filtered according to the input filter)
	 */
	public void insertStringForLine(int row, int column, String str) {
		list.get(getClampedIndex(row)).insert(column, str);
	}

	/**
	 * Removes a character at a specific position in a line.
	 * 
	 * @param row    the line index (0‑based, will be constrained to a valid range)
	 * @param column the character position within the line (will be constrained to a valid range)
	 */
	public void removeCharForLine(int row, int column) {
		list.get(getClampedIndex(row)).removeCharAt(column);
	}

	/**
	 * Removes a substring from a line.
	 * 
	 * @param row         the line index (0‑based, will be constrained to a valid range)
	 * @param columnStart the starting position (inclusive, will be constrained)
	 * @param columnEnd   the ending position (exclusive, will be constrained)
	 */
	public void removeStringForLine(int row, int columnStart, int columnEnd) {
		list.get(getClampedIndex(row)).remove(columnStart, columnEnd);
	}

	/**
	 * Splits a line at the specified column position. Text from the column position
	 * to the end of the line becomes a new line below.
	 * 
	 * @param row    the line index to split (0‑based, will be constrained to a valid range)
	 * @param column the column position to split at (will be constrained to the line length)
	 */
	public void splitLine(int row, int column) {
		row = getClampedIndex(row);

		final String currentRowText = getLineText(row);
		final int clampedColumn = (int) constrain(column, 0, currentRowText.length());

		insertLine(row + 1, currentRowText.substring(clampedColumn));
		getLine(row).remove(clampedColumn, currentRowText.length());
	}

	/**
	 * Merges a line with the line below it.
	 * 
	 * @param row the line index to merge with the next line (0‑based, will be constrained to a valid range)
	 */
	public void mergeLines(int row) {
		row = getClampedIndex(row);

		if (isLastRow(row)) {
			return;
		}

		getLine(row).insert(getLineLength(row), getLineText(row + 1));
		removeLine(row + 1);
	}

	/**
	 * Returns the text controller for a specific line.
	 * 
	 * @param row the line index (0‑based, will be constrained to a valid range)
	 * @return the {@link SingleLineTextController} for the specified line
	 */
	public SingleLineTextController getLine(int row) {

		return list.get(getClampedIndex(row));
	}

	/**
	 * Returns the complete text content as a single string with newline separators.
	 * 
	 * @return the complete text content
	 */
	public String getText() {
		return getCachedText();
	}

	/**
	 * Sets the text content from an array of lines.
	 * 
	 * @param lines the array of line strings to set (each element must not be {@code null})
	 * @throws NullPointerException if the array itself or any element is {@code null}
	 */
	public void setText(String... lines) {
		requireNonNull(lines, "lines");

		clear();

		for (int i = 0; i < lines.length; i++) {
			requireNonNull(lines[i], "lines[" + i + "]");
			if (i == 0) {
				getLine(0).set(lines[i]);

			} else {
				addLineSilently(lines[i]);
			}
		}

		notifyTextChanged();
	}

	/**
	 * Sets the text content from a raw string with newline separators.
	 * 
	 * @param text the raw text string (newlines will be used to split into lines; must not be {@code null})
	 * @throws NullPointerException if text is null
	 */
	public void setRawText(String text) {
		requireNonNull(text, "text");

		if (text.contains("\n")) {
			setText(text.split("\n", -1));
		} else {
			setText(text);
		}

	}

	/**
	 * Clears all text content while maintaining at least one empty line.
	 */
	public void clear() {
		if (hasOnlyOneLine()) {
			if (!getLine(0).isEmpty()) {
				getLine(0).clear();
			}
			return;
		}

		final SingleLineTextController controller = getLine(0);
		list.clear();
		list.add(controller);

		if (!controller.isEmpty()) {
			controller.clear();
		}
	}

	/**
	 * Returns the text content of a specific line.
	 * 
	 * @param row the line index (0‑based, will be constrained to a valid range)
	 * @return the text content of the specified line
	 */
	public String getLineText(int row) {
		row = getClampedIndex(row);
		return getLine(row).getAsString();
	}

	/**
	 * Returns the length (character count) of a specific line.
	 * 
	 * @param row the line index (0‑based, will be constrained to a valid range)
	 * @return the number of characters in the specified line
	 */
	public int getLineLength(int row) {
		row = getClampedIndex(row);
		return getLine(row).length();
	}

	/**
	 * Sets the listener for text change events.
	 * 
	 * @param onTextChangedListener the listener to call when text changes (cannot be {@code null})
	 * @throws NullPointerException if listener is null
	 */
	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
	}

	/**
	 * Returns the current update speed (minimum milliseconds between history updates).
	 * 
	 * @return the update speed in milliseconds
	 */
	public int getSpeedForUpdate() {
		return undoRedoManager.getSpeedForUpdate();
	}

	/**
	 * Sets the update speed for history tracking.
	 * 
	 * @param speedForUpdate the minimum milliseconds between history updates (must be ≥ 0)
	 * @throws IllegalArgumentException if speedForUpdate is less than 0
	 */
	public void setSpeedForUpdate(int speedForUpdate) {
		undoRedoManager.setSpeedForUpdate(speedForUpdate);
	}

	// == PRIVATE API ==

	private void addLineSilently(String text) {
		requireNonNull(text, "text");

		final SingleLineTextController line = new SingleLineTextController(text,getInputFilter());
		list.add(line);
		line.setOnTextChangedListener(this::notifyTextChanged);

	}

	private String getCachedText() {
		if (!textChanged) {
			return cachedText;
		}

		if (adapterSb == null) {
			adapterSb = new StringBuilder();
		} else {
			adapterSb.setLength(0);
		}

		for (int i = 0; i < list.size(); i++) {
			if (isLastRow(i)) {
				adapterSb.append(getLineText(i));
			} else {
				adapterSb.append(getLineText(i)).append('\n');
			}
		}

		textChanged = false;

		return cachedText = adapterSb.toString();
	}

	private int getClampedIndex(int index) {
		return (int) constrain(index, 0, getLinesCount() - 1);
	}

	private boolean isLastRow(int row) {
		return getClampedIndex(row) == getLinesCount() - 1;
	}

	private void notifyTextChanged() {
		textChanged = true;

		undoRedoManager.updateState();

		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
	}

	private boolean hasOnlyOneLine() {
		return getLinesCount() == 1;
	}

	private static final class UndoRedoManager {
		private static final int MIN_MS_FOR_UPDATE = 0;
		private static final int DEFAULT_MS_FOR_UPDATE = 100;
		private final MultiLineTextController controller;
		private final Deque<String> undo, redo;
		private String prevState;
		private long lastUpdateTime;
		private int speedForUpdate;
		private boolean operation;

		public UndoRedoManager(MultiLineTextController controller) {
			super();
			this.controller = requireNonNull(controller, "controller");

			undo = new ArrayDeque<String>();
			redo = new ArrayDeque<String>();

			prevState = controller.getText();

			lastUpdateTime = currentTimeMillis();

			setSpeedForUpdate(DEFAULT_MS_FOR_UPDATE);
		}

		public int getSpeedForUpdate() {
			return speedForUpdate;
		}

		public void setSpeedForUpdate(int speedForUpdate) {
			if (speedForUpdate < MIN_MS_FOR_UPDATE) {
				throw new IllegalArgumentException(
						"Speed for update must be equal or greater than: " + MIN_MS_FOR_UPDATE);
			}

			this.speedForUpdate = speedForUpdate;
		}

		public void undo() {
			operation = true;

			if (canUndo()) {
				prevState = null;
				redo.push(controller.getText());
				controller.setRawText(undo.pop());
			} else {
				controller.setText("");
			}

			operation = false;
		}

		public void redo() {
			operation = true;

			if (canRedo()) {
				undo.push(controller.getText());
				controller.setRawText(redo.pop());
			}

			operation = false;
		}

		public String getUndoStack() {
			return undo.toString();
		}

		public String getRedoStack() {
			return redo.toString();
		}

		public void updateState() {
			if (operation) {
				return;
			}

			final String text = controller.getText();

			final long now = currentTimeMillis();

			if (now - lastUpdateTime < getSpeedForUpdate()) {
				return;
			}

			lastUpdateTime = now;

			if (prevState == null) {
				prevState = text;
			}

			if (canUndo()) {
				if (!undo.peek().equals(prevState)) {
					undo.push(prevState);
				}
			} else {
				undo.push(prevState);
			}

			prevState = text;

			redo.clear();
		}

		private boolean canUndo() {
			return !undo.isEmpty();
		}

		private boolean canRedo() {
			return !redo.isEmpty();
		}
	}
}