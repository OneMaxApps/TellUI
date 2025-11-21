package microui.core;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayList;
import java.util.List;

import microui.event.Listener;

public class MultiLineTextController {
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
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public int getLinesCount() {
		return list.size();
	}
	
	public void addLine(String text) {
		final SingleLineTextController line = new SingleLineTextController(text);
		list.add(getLinesCount(), line);
		line.setOnTextChangedListener(this::notifyTextChanged);
		notifyTextChanged();
	}
	
	public void insertLine(int index, String text) {
		index = (int) constrain(index,0,getLinesCount());
		
		list.add(index, new SingleLineTextController(text));
		list.get(index).setOnTextChangedListener(this::notifyTextChanged);
		
		notifyTextChanged();
	}
	
	public void removeLine(int index) {
		if (getLinesCount() == MIN_LINES_COUNT) {
			list.get(0).clear();
			return;
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
		
		if (isEmpty()) {
			return;
		}
		
		final String currentRowText = getLine(row).getAsString();
		final int clampedColumn = (int) constrain(column,0,currentRowText.length());
		
		insertLine(row+1,currentRowText.substring(clampedColumn));
		getLine(row).remove(clampedColumn,currentRowText.length());
	}
	
	public void mergeLines(int row) {
		row = getClampedIndex(row);
		
		if (isLastRow(row)) {
			return;
		}
		
		getLine(row).insert(getLine(row).length(), getLine(row+1).getAsString());
		removeLine(row+1);
	}
	
	public SingleLineTextController getLine(int row) {
		
		return list.get(getClampedIndex(row));
	}
	
	public String getText() {
		return getCachedText();
	}
	
	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener,"onTextChangedListener");
	}

	// == PRIVATE API ==
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
				adapterSb.append(list.get(i).getAsString());
			} else {
				adapterSb.append(list.get(i).getAsString() + "\n");
			}
		}
		
		textChanged = false;
		
		return cachedText = adapterSb.toString();
	}
	
	private int getClampedIndex(int index) {
		return (int) constrain(index, 0, getLinesCount()-1);
	}
	
	private boolean isLastRow(int row) {
		return getClampedIndex(row) == getLinesCount()-1;
	}

	
	private void notifyTextChanged() {
		textChanged = true;
		
		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
	}
}