package microui.core.controller;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import microui.event.Listener;

/**
 * Controller for managing single-line text input with validation and event
 * listeners. Provides basic text manipulation operations with character
 * validation and event notifications.
 * <p>
 * This controller manages a single line of text with support for insertion,
 * removal, and validation of allowed characters. It includes event listeners
 * for various text manipulation operations.
 * </p>
 * 
 * @see Listener
 */
public final class SingleLineTextController {
	private static final String STANDARD_VALIDATION = "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*";
	private static final int MAX_CAPACITY_FOR_CLEAR = 100;
	private final StringBuilder sb;
	private StringBuilder adapterSb;
	private String cachedText;
	private Listener onAfterCharInsertListener, onAfterStringInsertListener, onTextChangedListener;

	/**
	 * Constructs a SingleLineTextController with initial text.
	 * 
	 * @param text the initial text content (cannot be null)
	 * @throws NullPointerException if text is null
	 */
	public SingleLineTextController(final String text) {
		sb = new StringBuilder(requireNonNull(text, "text"));
		updateCachedString();
	}

	/**
	 * Constructs a SingleLineTextController with empty initial text.
	 */
	public SingleLineTextController() {
		this("");
	}

	/**
	 * Sets the listener for after-character-insert events.
	 * 
	 * @param onAfterCharInsertListener the listener to call after character
	 *                                  insertion (cannot be null)
	 * @throws NullPointerException if listener is null
	 */
	public void setOnAfterCharInsertListener(Listener onAfterCharInsertListener) {
		this.onAfterCharInsertListener = requireNonNull(onAfterCharInsertListener, "onAfterCharInsertListener");
	}

	/**
	 * Sets the listener for after-string-insert events.
	 * 
	 * @param onAfterStringInsertListener the listener to call after string
	 *                                    insertion (cannot be null)
	 * @throws NullPointerException if listener is null
	 */
	public void setOnAfterStringInsertListener(Listener onAfterStringInsertListener) {
		this.onAfterStringInsertListener = requireNonNull(onAfterStringInsertListener, "onAfterStringInsertListener");
	}

	/**
	 * Sets the listener for text-changed events.
	 * 
	 * @param onTextChangedListener the listener to call when text changes (cannot
	 *                              be null)
	 * @throws NullPointerException if listener is null
	 */
	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener, "onTextChangedListener");
	}

	/**
	 * Returns the text content as a string.
	 * 
	 * @return the current text content
	 */
	public String getAsString() {
		return cachedText;
	}

	/**
	 * Inserts a character at the specified position.
	 * 
	 * @param pos the position to insert at (will be constrained to valid range)
	 * @param ch  the character to insert
	 */
	public void insert(int pos, char ch) {
		insertInternal(pos, ch);
	}

	/**
	 * Inserts a string at the specified position.
	 * 
	 * @param pos  the position to insert at (will be constrained to valid range)
	 * @param text the string to insert
	 */
	public void insert(int pos, String text) {
		insertInternal(pos, text);
	}

	/**
	 * Inserts a number at the specified position.
	 * 
	 * @param pos the position to insert at (will be constrained to valid range)
	 * @param num the number to insert (converted to string)
	 */
	public void insert(int pos, int num) {
		insertInternal(pos, String.valueOf(num));
	}

	/**
	 * Sets the text content, replacing any existing text.
	 * 
	 * @param text the new text content
	 */
	public void set(String text) {
		setInternal(text);
	}

	/**
	 * Sets the text content from a StringBuilder, replacing any existing text.
	 * 
	 * @param text the StringBuilder containing new text content
	 */
	public void set(StringBuilder text) {
		setInternal(text.toString());
	}

	/**
	 * Clears all text content. If the internal buffer capacity exceeds
	 * MAX_CAPACITY_FOR_CLEAR, trims the buffer.
	 */
	public void clear() {
		if (isEmpty()) {
			return;
		}

		sb.setLength(0);
		if (sb.capacity() >= MAX_CAPACITY_FOR_CLEAR) {
			sb.trimToSize();
		}

		updateCachedString();
	}

	/**
	 * Removes a character at the specified position.
	 * 
	 * @param pos the position of the character to remove (will be constrained to
	 *            valid range)
	 */
	public void removeCharAt(final int pos) {
		if (isEmpty()) {
			return;
		}

		sb.deleteCharAt((int) constrain(pos, 0, length() - 1));

		updateCachedString();
	}

	/**
	 * Removes characters between specified positions.
	 * 
	 * @param firstChar the starting position (inclusive, will be constrained)
	 * @param lastChar  the ending position (exclusive, will be constrained)
	 */
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

	/**
	 * Removes the first character.
	 */
	public void removeFirstChar() {
		removeCharAt(0);
	}

	/**
	 * Removes the last character.
	 */
	public void removeLastChar() {
		removeCharAt(length() - 1);
	}

	/**
	 * Returns the length of the text content.
	 * 
	 * @return the number of characters in the text
	 */
	public int length() {
		return sb.length();
	}

	/**
	 * Checks if the text content is empty.
	 * 
	 * @return true if length is 0, false otherwise
	 */
	public boolean isEmpty() {
		return length() == 0;
	}

	/**
	 * Validates if a character is allowed based on standard validation rules. Valid
	 * characters include letters, digits, and standard special characters.
	 * 
	 * @param ch the character to validate
	 * @return true if the character is valid, false otherwise
	 */
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
		requireNonNull(str, "str");

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