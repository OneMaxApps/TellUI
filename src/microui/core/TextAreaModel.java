package microui.core;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayList;
import java.util.List;

import microui.event.Listener;

public final class TextAreaModel {
	private final MultiLineTextController controller;

	public TextAreaModel() {
		super();
		controller = new MultiLineTextController();
	}
	
	public static final class MultiLineTextController {
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
		
		public static final class SingleLineTextController {
			private static final String STANDARD_VALIDATION = "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*";
			private static final int MAX_CAPACITY_FOR_CLEAR = 100;
			private final StringBuilder sb;
			private StringBuilder adapterSb;
			private String cachedText;
			private Listener onAfterCharInsertListener, onAfterStringInsertListener, onTextChangedListener; 
			
			public SingleLineTextController(final String text) {
				sb = new StringBuilder(requireNonNull(text,"text"));
				updateCachedString();
			}

			public SingleLineTextController() {
				this("");
			}
			
			public void setOnAfterCharInsertListener(Listener onAfterCharInsertListener) {
				this.onAfterCharInsertListener = requireNonNull(onAfterCharInsertListener,"onAfterCharInsertListener");
			}

			public void setOnAfterStringInsertListener(Listener onAfterStringInsertListener) {
				this.onAfterStringInsertListener = requireNonNull(onAfterStringInsertListener,"onAfterStringInsertListener");
			}

			public void setOnTextChangedListener(Listener onTextChangedListener) {
				this.onTextChangedListener = requireNonNull(onTextChangedListener,"onTextChangedListener");
			}
			
			public String getAsString() {
				return cachedText;
			}

			public void insert(int pos, char ch) {
				insertInternal(pos, ch);
			}

			public void insert(int pos, String text) {
				insertInternal(pos,text);
			}

			public void insert(int pos, int num) {
				insertInternal(pos, String.valueOf(num));
			}

			public void set(String text) {
				setInternal(text);
			}

			public void set(StringBuilder text) {
				setInternal(text.toString());
			}

			public void clear() {
				if (isEmpty()) {
					return;
				}

				sb.setLength(0);
				if(sb.capacity() >= MAX_CAPACITY_FOR_CLEAR) {
					sb.trimToSize();
				}
				
				updateCachedString();
			}

			public void removeCharAt(final int pos) {
				if (isEmpty()) {
					return;
				}

				sb.deleteCharAt((int) constrain(pos, 0, length() - 1));

				updateCachedString();
			}

			public void remove(int firstChar, int lastChar) {
				if (isEmpty()) {
					return;
				}

				firstChar = (int) constrain(firstChar, 0, length());
				lastChar = (int) constrain(lastChar, 0, length());

				sb.delete(firstChar, lastChar);

				updateCachedString();
			}

			public void removeFirstChar() {
				removeCharAt(0);
			}

			public void removeLastChar() {
				removeCharAt(length() - 1);
			}

			public int length() {
				return sb.length();
			}

			public boolean isEmpty() {
				return length() == 0;
			}

			public boolean isValidChar(final char ch) {
				return STANDARD_VALIDATION.indexOf(ch) >= 0 || Character.isLetterOrDigit(ch);
			}

			private String getValidatedString(String src) {
				if (adapterSb == null) {
					adapterSb = new StringBuilder();
				} else {
					adapterSb.setLength(0);
				}

				for (int i = 0; i < src.length(); i++) {
					if (isValidChar(src.charAt(i))) {
						adapterSb.append(src.charAt(i));
					}
				}

				if (src.length() == adapterSb.length()) {
					return src;
				}

				return adapterSb.toString();
			}

			private void updateCachedString() {
				cachedText = sb.toString();

				notifyOnTextChanged();
			}

			private void insertInternal(int pos, char ch) {
				if (!isValidChar(ch)) {
					return;
				}

				pos = (int) constrain(pos, 0, length());

				sb.insert(pos, ch);
				updateCachedString();
				notifyOnAfterCharInsert();
			}
			
			private void insertInternal(int pos, String str) {
				requireNonNull(str,"str");

				str = getValidatedString(str);
				
				pos = (int) constrain(pos, 0, length());

				sb.insert(pos, str);
				
				updateCachedString();
				notifyOnAfterStringInsert();
			}
			
			private void setInternal(String text) {
				requireNonNull(text, "text");
				
				if (text.equals(cachedText)) {
					return;
				}

				clear();

				sb.append(getValidatedString(text));

				updateCachedString();
			}
			
			private void notifyOnAfterCharInsert() {
				if (onAfterCharInsertListener != null) {
					onAfterCharInsertListener.action();
				}
			}
			
			private void notifyOnAfterStringInsert() {
				if (onAfterStringInsertListener != null) {
					onAfterStringInsertListener.action();
				}
			}
			
			private void notifyOnTextChanged() {
				if (onTextChangedListener != null) {
					onTextChangedListener.action();
				}
			}
		}
	}
}