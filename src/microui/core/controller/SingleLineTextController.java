package microui.core.controller;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import microui.event.Listener;

public final class SingleLineTextController {
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

		final int effectiveFirst = min(firstChar, lastChar);
		final int effectiveLast = max(firstChar, lastChar);
		
		sb.delete(effectiveFirst, effectiveLast);

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