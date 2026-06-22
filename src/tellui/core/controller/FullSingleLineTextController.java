package tellui.core.controller;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static telluii.util.MathUtils.constrain;

import java.util.ArrayDeque;
import java.util.Deque;

import tellui.core.interfaces.InputFilter;
import tellui.core.interfaces.Listener;
import telluii.util.Debugger;

/**
 * Comprehensive controller for managing single-line text input with advanced
 * features. Provides text validation, constraint management, password mode,
 * undo/redo functionality, and event listeners for text manipulation
 * operations.
 * 
 * @see FilterMode
 * @see Listener
 */
public class FullSingleLineTextController {
	private static final int DEFAULT_MAX_CHARS = 4;
	private static final int MIN_CONSTRAIN_VALUE = 1;
	private static final int MAX_CAPACITY_FOR_CLEAR = 100;
	private static final char DEFAULT_PASSWORD_CHAR = '*';

	private final UndoRedoManager undoRedoManager;
	private final StringBuilder sb;
	private InputFilter inputFilter;
	private StringBuilder adapterSb;
	private String cachedText, cachedPasswordText;
	private Listener onAfterCharInsertListener, onAfterStringInsertListener, onTextChangedListener;
	private boolean validationEnabled, constrainEnabled, passwordModeEnabled;
	private int maxChars;
	private char passwordChar;
	private FilterMode filterMode;

	/**
	 * Constructs a FullSingleLineTextController with initial text.
	 * 
	 * @param text the initial text content (cannot be null)
	 * @throws NullPointerException if text is null
	 */
	public FullSingleLineTextController(final String text) {
		undoRedoManager = new UndoRedoManager(this);
		setInputFilter((c) -> "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*".indexOf(c) >= 0 || Character.isLetterOrDigit(c));
		sb = new StringBuilder(getFilteredString(requireNonNull(text, "text")));
		updateCachedStrings();

		validationEnabled = true;
		maxChars = DEFAULT_MAX_CHARS;
		filterMode = FilterMode.DEFAULT;

		setPasswordChar(DEFAULT_PASSWORD_CHAR);
	}

	/**
	 * Constructs a FullSingleLineTextController with empty initial text.
	 */
	public FullSingleLineTextController() {
		this("");
	}

	/**
	 * Returns the current input filter used for character validation.
	 *
	 * @return the input filter (never {@code null})
	 */
	public InputFilter getInputFilter() {
		return inputFilter;
	}

	/**
	 * Sets the input filter used for character validation.
	 *
	 * @param inputFilter the new input filter (must not be {@code null})
	 * @throws NullPointerException if {@code inputFilter} is {@code null}
	 */
	public void setInputFilter(InputFilter inputFilter) {
		this.inputFilter = requireNonNull(inputFilter,"inputFilter");
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
	 * Sets the listener for history-changed events (undo/redo operations).
	 * 
	 * @param onHistoryChangedListener the listener to call when history changes
	 *                                 (cannot be null)
	 * @throws NullPointerException if listener is null
	 */
	public final void setOnHistoryChangedListener(Listener onHistoryChangedListener) {
		undoRedoManager.setOnHistoryChangedListener(onHistoryChangedListener);
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
	 * Returns the character used for password masking.
	 * 
	 * @return the password masking character
	 */
	public final char getPasswordChar() {
		return passwordChar;
	}

	/**
	 * Sets the character used for password masking.
	 * 
	 * @param passwordChar the character to use for password masking
	 */
	public final void setPasswordChar(char passwordChar) {
		if (this.passwordChar == passwordChar) {
			return;
		}
		this.passwordChar = passwordChar;
		updateCachedStrings();
	}

	/**
	 * Checks if password mode is enabled.
	 * 
	 * @return true if password mode is enabled, false otherwise
	 */
	public final boolean isPasswordModeEnabled() {
		return passwordModeEnabled;
	}

	/**
	 * Enables or disables password mode.
	 * 
	 * @param passwordModeEnabled true to enable password masking, false to disable
	 */
	public final void setPasswordModeEnabled(boolean passwordModeEnabled) {
		if (this.passwordModeEnabled == passwordModeEnabled) {
			return;
		}
		this.passwordModeEnabled = passwordModeEnabled;
		updateCachedStrings();
	}

	/**
	 * Returns the current filter mode used for character validation.
	 *
	 * @return the filter mode (never {@code null})
	 */
	public final FilterMode getFilterMode() {
		return filterMode;
	}

	/**
	 * Sets the filter mode for character validation.
	 *
	 * @param filterMode the new filter mode (must not be {@code null})
	 * @throws NullPointerException if {@code filterMode} is {@code null}
	 */
	public final void setFilterMode(FilterMode filterMode) {
		if (this.filterMode == filterMode) {
			return;
		}

		this.filterMode = requireNonNull(filterMode, "filterMode");

		setInternal(getFilteredString(cachedText));
	}

	/**
	 * Checks if character constraints are enabled.
	 * 
	 * @return true if constraints are enabled, false otherwise
	 */
	public final boolean isConstrainEnabled() {
		return constrainEnabled;
	}

	/**
	 * Enables or disables character length constraints.
	 * 
	 * @param constrainEnabled true to enable length constraints, false to disable
	 */
	public final void setConstrainEnabled(boolean constrainEnabled) {
		if (this.constrainEnabled == constrainEnabled) {
			return;
		}

		this.constrainEnabled = constrainEnabled;
		updateConstrainedValue();
	}

	/**
	 * Returns the maximum allowed characters when constraints are enabled.
	 * 
	 * @return the maximum character limit
	 */
	public final int getMaxChars() {
		return maxChars;
	}

	/**
	 * Sets the maximum allowed characters when constraints are enabled.
	 * 
	 * @param maxChars the maximum character limit (must be ≥ MIN_CONSTRAIN_VALUE {@value #MIN_CONSTRAIN_VALUE})
	 * @throws IllegalArgumentException if maxChars is less than MIN_CONSTRAIN_VALUE {@value #MIN_CONSTRAIN_VALUE}
	 */
	public final void setMaxChars(int maxChars) {
		if (maxChars < MIN_CONSTRAIN_VALUE) {
			throw new IllegalArgumentException("Max chars cannot be less than " + MIN_CONSTRAIN_VALUE);
		}

		if (this.maxChars == maxChars) {
			return;
		}

		this.maxChars = maxChars;

		updateConstrainedValue();
	}

	/**
	 * Returns the text content as a string. Returns password-masked text if
	 * password mode is enabled.
	 * 
	 * @return the text content (masked if in password mode)
	 */
	public final String getAsString() {
		return passwordModeEnabled ? cachedPasswordText : cachedText;
	}

	/**
	 * Returns the hidden (unmasked) text content.
	 * 
	 * @return the actual text content without password masking
	 */
	public final String getHiddenText() {
		return cachedText;
	}

	/**
	 * Returns the text content parsed as an integer.
	 * 
	 * @return the text content as an integer
	 */
	public final int getDigitsStrict() {
		return Integer.parseInt(cachedText);
	}

	/**
	 * Returns the text content parsed as an integer, or a default value if parsing
	 * fails.
	 * 
	 * @param defaultValue the default value to return if parsing fails
	 * @return the parsed integer or defaultValue if parsing fails
	 */
	public int getDigitsOrDefault(int defaultValue) {
		try {
			return Integer.parseInt(cachedText);
		} catch (NumberFormatException e) {
			if (Debugger.isEnabled()) {
				System.err.println(
						"Invalid number format in TextController. Switch validation mode to DIGITS_ONLY.\nInput: "
								+ cachedText);
			}

			return defaultValue;
		}
	}

	/**
	 * Inserts a character at the specified position.
	 * 
	 * @param pos the position to insert at (will be constrained to valid range)
	 * @param ch  the character to insert
	 */
	public final void insert(int pos, char ch) {
		insertInternal(pos, ch);
	}

	/**
	 * Inserts a string at the specified position.
	 * 
	 * @param pos  the position to insert at (will be constrained to valid range)
	 * @param text the string to insert
	 */
	public final void insert(int pos, String text) {
		insertInternal(pos, text);
	}

	/**
	 * Inserts a number at the specified position.
	 * 
	 * @param pos the position to insert at (will be constrained to valid range)
	 * @param num the number to insert (converted to string)
	 */
	public final void insert(int pos, int num) {
		insertInternal(pos, String.valueOf(num));
	}

	/**
	 * Sets the text content, replacing any existing text.
	 * 
	 * @param text the new text content
	 */
	public final void set(String text) {
		setInternal(text);
	}

	/**
	 * Clears all text content. If the internal buffer capacity exceeds
	 * MAX_CAPACITY_FOR_CLEAR ({@value #MAX_CAPACITY_FOR_CLEAR}), trims the buffer.
	 */
	public final void clear() {
		if (isEmpty()) {
			return;
		}

		sb.setLength(0);
		if (sb.capacity() >= MAX_CAPACITY_FOR_CLEAR) {
			sb.trimToSize();
		}

		updateCachedStrings();
	}

	/**
	 * Removes a character at the specified position.
	 * 
	 * @param pos the position of the character to remove (will be constrained to valid range)
	 */
	public final void removeCharAt(final int pos) {
		if (isEmpty()) {
			return;
		}

		sb.deleteCharAt((int) constrain(pos, 0, length() - 1));

		updateCachedStrings();
	}

	/**
	 * Removes characters between specified positions.
	 * 
	 * @param firstChar the starting position (inclusive, will be constrained)
	 * @param lastChar  the ending position (exclusive, will be constrained)
	 */
	public final void remove(int firstChar, int lastChar) {
		if (isEmpty()) {
			return;
		}

		firstChar = (int) constrain(firstChar, 0, length());
		lastChar = (int) constrain(lastChar, 0, length());

		final int effectiveFirst = min(firstChar, lastChar);
		final int effectiveLast = max(firstChar, lastChar);

		sb.delete(effectiveFirst, effectiveLast);

		updateCachedStrings();
	}

	/**
	 * Removes the first character.
	 */
	public final void removeFirstChar() {
		removeCharAt(0);
	}

	/**
	 * Removes the last character.
	 */
	public final void removeLastChar() {
		removeCharAt(length() - 1);
	}

	/**
	 * Returns the length of the text content.
	 * 
	 * @return the number of characters in the text
	 */
	public final int length() {
		return sb.length();
	}

	/**
	 * Checks if the text content is empty.
	 * 
	 * @return true if length is 0, false otherwise
	 */
	public final boolean isEmpty() {
		return length() == 0;
	}

	/**
	 * Checks if text validation is enabled.
	 * 
	 * @return true if validation is enabled, false otherwise
	 */
	public final boolean isValidationEnabled() {
		return validationEnabled;
	}

	/**
	 * Enables or disables text validation.
	 * 
	 * @param enabled true to enable validation, false to disable
	 */
	public final void setValidationEnabled(boolean enabled) {
		this.validationEnabled = enabled;
	}

	/**
	 * Validates if a character is allowed based on the current validation mode.
	 * 
	 * @param ch the character to validate
	 * @return true if the character is valid, false otherwise
	 */
	public final boolean isValidChar(final char ch) {

		switch (filterMode) {

		case DEFAULT:
			return inputFilter.allow(ch);

		case ONLY_DIGITS:
			return Character.isDigit(ch);

		case ONLY_LETTERS:
			return Character.isLetter(ch);

		default:
			return inputFilter.allow(ch);

		}

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

	private String getFilteredString(String src) {
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

	private void updateCachedStrings() {
		cachedText = sb.toString();

		if (passwordModeEnabled) {
			cachedPasswordText = String.valueOf(passwordChar).repeat(cachedText.length());
		} else {
			cachedPasswordText = null;
		}

		notifyOnTextChanged();

		undoRedoManager.updateState();

	}

	private void updateConstrainedValue() {
		if (constrainEnabled) {
			if (length() > maxChars) {
				sb.setLength(maxChars);
				updateCachedStrings();
			}
		}
	}

	private void insertInternal(int pos, char ch) {
		if (constrainEnabled && length() == maxChars) {
			return;
		}

		if (validationEnabled && !isValidChar(ch)) {
			return;
		}

		pos = (int) constrain(pos, 0, length());

		sb.insert(pos, ch);
		updateCachedStrings();
		notifyOnAfterCharInsert();
	}

	private void insertInternal(int pos, String str) {
		requireNonNull(str, "str");

		if (constrainEnabled && length() == maxChars) {
			return;
		}

		if (validationEnabled) {
			str = getFilteredString(str);
		}

		pos = (int) constrain(pos, 0, length());

		sb.insert(pos, str);

		if (constrainEnabled) {
			updateConstrainedValue();
		}

		updateCachedStrings();
		notifyOnAfterStringInsert();
	}

	private void setInternal(String text) {
		requireNonNull(text, "text");

		if (text.equals(cachedText)) {
			return;
		}

		clear();

		sb.append(validationEnabled ? getFilteredString(text) : text);

		if (constrainEnabled) {
			updateConstrainedValue();
		}

		updateCachedStrings();
	}

	/**
	 * Filter modes for text input.
	 */
	public static enum FilterMode {
		/** Allow letters, digits, and standard special characters (and custom). */
		DEFAULT,
		/** Allow only digit characters (0-9). */
		ONLY_DIGITS,
		/** Allow only letter characters (a-z, A-Z). */
		ONLY_LETTERS;
	}

	private final static class UndoRedoManager {
		private final FullSingleLineTextController controller;
		private final Deque<String> undo, redo;
		private boolean operation;
		private Listener onHistoryChangedListener;

		public UndoRedoManager(FullSingleLineTextController controller) {
			this.controller = requireNonNull(controller, "controller");
			undo = new ArrayDeque<String>();
			redo = new ArrayDeque<String>();
		}

		public final void setOnHistoryChangedListener(Listener onHistoryChangedListener) {
			this.onHistoryChangedListener = requireNonNull(onHistoryChangedListener, "onHistoryChangedListener");
		}

		public void undo() {
			operation = true;

			if (!undo.isEmpty()) {
				redo.push(undo.pop());
				if (!undo.isEmpty()) {
					controller.set(undo.peek());
				} else {
					controller.set("");
				}

				if (onHistoryChangedListener != null) {
					onHistoryChangedListener.action();
				}
			}

			operation = false;
		}

		public void redo() {
			operation = true;
			if (!redo.isEmpty()) {
				undo.push(redo.peek());
				controller.set(redo.pop());

				if (onHistoryChangedListener != null) {
					onHistoryChangedListener.action();
				}
			}
			operation = false;
		}

		private void updateState() {
			if (operation) {
				return;
			}
			undo.push(controller.getHiddenText());
			redo.clear();
		}

	}
}