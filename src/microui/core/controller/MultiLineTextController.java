package microui.core.controller;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayList;
import java.util.List;

import microui.event.Listener;

public final class MultiLineTextController {
	private static final byte MIN_LINES_COUNT;
	private static final String EMPTY_TEXT;
	private final List<SingleLineTextController> list;
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
		addLine(EMPTY_TEXT);
	}

	// == PUBLIC API ==
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

		getLine(row).insert(getLineLength(row), getLineText(row+1));
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
			requireNonNull(lines[i],"lines[" + i + "]");
			if (i == 0) {
				getLine(0).set(lines[i]);
			} else {
				addLineSilently(lines[i]);
			}
		}
		
		notifyTextChanged();
	}

	public void clear() {
		if (hasOnlyOneLine()) {
			if(!getLine(0).isEmpty()) {
				getLine(0).clear();
			}
			return;
		}
		
		final SingleLineTextController controller = getLine(0);
		list.clear();
		list.add(controller);
		
		if(!controller.isEmpty()) {
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

	// == PRIVATE API ==
	private void addLineSilently(String text) {
		requireNonNull(text,"text");
		
		final SingleLineTextController line = new SingleLineTextController(text);
		list.add(getLinesCount(), line);
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
				adapterSb.append(getLineText(i) + "\n");
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

		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
	}

	private boolean hasOnlyOneLine() {
		return getLinesCount() == 1;
	}
}