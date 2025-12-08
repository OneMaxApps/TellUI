package microui.core.controller;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import microui.event.Listener;

public final class MultiLineTextController {
	private static final String EMPTY_TEXT;
	private static final byte MIN_LINES_COUNT;
	private final List<SingleLineTextController> list;
	private final UndoRedoManager undoRedoManager;
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

	public MultiLineTextController() {
		super();
		list = new ArrayList<SingleLineTextController>();
		undoRedoManager = new UndoRedoManager(this);
		addLine(EMPTY_TEXT);
	}

	// == PUBLIC API ==

	public void undo() {
		undoRedoManager.undo();
	}

	public void redo() {
		undoRedoManager.redo();
	}

	public String getUndoStack() {
		return undoRedoManager.getUndoStack();
	}

	public String getRedoStack() {
		return undoRedoManager.getRedoStack();
	}

	public boolean isBlank() {
		return hasOnlyOneLine() && getLine(0).isEmpty();
	}

	public int getLinesCount() {
		return list.size();
	}

	public void addLine(String text) {
		addLineSilently(text);
		notifyTextChanged();
	}

	public void insertLine(int index, String text) {
		index = (int) constrain(index, 0, getLinesCount());

		list.add(index, new SingleLineTextController(text));
		list.get(index).setOnTextChangedListener(this::notifyTextChanged);

		notifyTextChanged();
	}

	public void removeLine(int index) {
		if (getLinesCount() == MIN_LINES_COUNT) {
			list.get(0).clear();
		} else {
			list.remove(getClampedIndex(index));
		}

		notifyTextChanged();
	}

	public void insertCharForLine(int row, int column, char ch) {
		list.get(getClampedIndex(row)).insert(column, ch);
	}

	public void insertStringForLine(int row, int column, String str) {
		list.get(getClampedIndex(row)).insert(column, str);
	}

	public void removeCharForLine(int row, int column) {
		list.get(getClampedIndex(row)).removeCharAt(column);
	}

	public void removeStringForLine(int row, int columnStart, int columnEnd) {
		list.get(getClampedIndex(row)).remove(columnStart, columnEnd);
	}

	public void splitLine(int row, int column) {
		row = getClampedIndex(row);

		final String currentRowText = getLineText(row);
		final int clampedColumn = (int) constrain(column, 0, currentRowText.length());

		insertLine(row + 1, currentRowText.substring(clampedColumn));
		getLine(row).remove(clampedColumn, currentRowText.length());
	}

	public void mergeLines(int row) {
		row = getClampedIndex(row);

		if (isLastRow(row)) {
			return;
		}

		getLine(row).insert(getLineLength(row), getLineText(row + 1));
		removeLine(row + 1);
	}

	public SingleLineTextController getLine(int row) {

		return list.get(getClampedIndex(row));
	}

	public String getText() {
		return getCachedText();
	}

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
	
	public void setRawText(String text) {
		requireNonNull(text,"text");
		
		if (text.contains("\n")) {
			setText(text.split("\n", -1));
		} else {
			setText(text);
		}
		
	}

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

	public String getLineText(int row) {
		row = getClampedIndex(row);
		return getLine(row).getAsString();
	}

	public int getLineLength(int row) {
		row = getClampedIndex(row);
		return getLine(row).length();
	}

	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
	}
	
	public int getSpeedForUpdate() {
		return undoRedoManager.getSpeedForUpdate();
	}

	public void setSpeedForUpdate(int speedForUpdate) {
		undoRedoManager.setSpeedForUpdate(speedForUpdate);
	}

	// == PRIVATE API ==
	private void addLineSilently(String text) {
		requireNonNull(text, "text");

		final SingleLineTextController line = new SingleLineTextController(text);
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
				throw new IllegalArgumentException("Speed for update must be equal or greater than: " + MIN_MS_FOR_UPDATE);
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