package microui.core.controller;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayDeque;
import java.util.Deque;

import microui.event.Listener;
import microui.util.Debugger;

/**
 * Comprehensive controller for managing single-line text input with advanced features.
 * Provides text validation, constraint management, password mode, undo/redo functionality,
 * and event listeners for text manipulation operations.
 * <p>
 * This controller is designed for robust text input management in GUI applications,
 * supporting features like character validation, length constraints, password masking,
 * and history tracking for undo/redo operations.
 * </p>
 * 
 * @author microui.core
 * @version 1.0
 * @see ValidationMode
 * @see Listener
 */
public class FullSingleLineTextController {
	/** Standard characters considered valid in ALL validation mode. */
	private static final String STANDARD_VALIDATION = "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*";
	/** Default maximum character limit. */
	private static final int DEFAULT_MAX_CHARS = 4;
	/** Minimum value for character constraints. */
	private static final int MIN_CONSTRAIN_VALUE = 1;
	/** Capacity threshold for clearing StringBuilder. */
	private static final int MAX_CAPACITY_FOR_CLEAR = 100;
	/** Default character used for password masking. */
	private static final char DEFAULT_PASSWORD_CHAR = '*';

	/** Manager for undo/redo operations. */
	private final UndoRedoManager undoRedoManager;
	/** StringBuilder storing the actual text content. */
	private final StringBuilder sb;
	/** Temporary StringBuilder for validation operations. */
	private StringBuilder adapterSb;
	/** Cached visible text content. */
	private String cachedText, cachedPasswordText;
	/** Listeners for text manipulation events. */
	private Listener onAfterCharInsertListener, onAfterStringInsertListener, onTextChangedListener; 
	/** State flags for controller features. */
	private boolean validationEnabled, constrainEnabled, passwordModeEnabled;
	/** Maximum allowed characters when constraints are enabled. */
	private int maxChars;
	/** Character used for password masking. */
	private char passwordChar;
	/** Current validation mode. */
	private ValidationMode validationMode;

	/**
	 * Constructs a FullSingleLineTextController with initial text.
	 * 
	 * @param text the initial text content (cannot be null)
	 * @throws NullPointerException if text is null
	 */
	public FullSingleLineTextController(final String text) {
		undoRedoManager = new UndoRedoManager(this);
		sb = new StringBuilder(requireNonNull(text,"text"));
		updateCachedStrings();

		validationEnabled = true;
		maxChars = DEFAULT_MAX_CHARS;
		validationMode = ValidationMode.ALL;

		setPasswordChar(DEFAULT_PASSWORD_CHAR);
	}

	/**
	 * Constructs a FullSingleLineTextController with empty initial text.
	 */
	public FullSingleLineTextController() {
		this("");
	}
	
	/**
	 * Sets the listener for after-character-insert events.
	 * 
	 * @param onAfterCharInsertListener the listener to call after character insertion (cannot be null)
	 * @throws NullPointerException if listener is null
	 */
	public void setOnAfterCharInsertListener(Listener onAfterCharInsertListener) {
		this.onAfterCharInsertListener = requireNonNull(onAfterCharInsertListener,"onAfterCharInsertListener");
	}

	/**
	 * Sets the listener for after-string-insert events.
	 * 
	 * @param onAfterStringInsertListener the listener to call after string insertion (cannot be null)
	 * @throws NullPointerException if listener is null
	 */
	public void setOnAfterStringInsertListener(Listener onAfterStringInsertListener) {
		this.onAfterStringInsertListener = requireNonNull(onAfterStringInsertListener,"onAfterStringInsertListener");
	}

	/**
	 * Sets the listener for text-changed events.
	 * 
	 * @param onTextChangedListener the listener to call when text changes (cannot be null)
	 * @throws NullPointerException if listener is null
	 */
	public void setOnTextChangedListener(Listener onTextChangedListener) {
		this.onTextChangedListener = requireNonNull(onTextChangedListener,"onTextChangedListener");
	}
	
	/**
	 * Sets the listener for history-changed events (undo/redo operations).
	 * 
	 * @param onHistoryChangedListener the listener to call when history changes (cannot be null)
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
	 * Returns the current validation mode.
	 * 
	 * @return the current validation mode
	 */
	public final ValidationMode getValidationMode() {
		return validationMode;
	}

	/**
	 * Sets the validation mode for text input.
	 * 
	 * @param validationMode the validation mode to set (cannot be null)
	 * @throws NullPointerException if validationMode is null
	 */
	public final void setValidationMode(ValidationMode validationMode) {
		if (this.validationMode == validationMode) {
			return;
		}

		this.validationMode = requireNonNull(validationMode, "validationMode");
		
		setInternal(getValidatedString(cachedText));
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
	 * @param maxChars the maximum character limit (must be ≥ MIN_CONSTRAIN_VALUE)
	 * @throws IllegalArgumentException if maxChars is less than MIN_CONSTRAIN_VALUE
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
	 * Returns the text content as a string.
	 * Returns password-masked text if password mode is enabled.
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
	 * @throws NumberFormatException if the text cannot be parsed as an integer
	 */
	public final int getDigitsStrict() {
		return Integer.parseInt(cachedText);
	}

	/**
	 * Returns the text content parsed as an integer, or a default value if parsing fails.
	 * 
	 * @param defaultValue the default value to return if parsing fails
	 * @return the parsed integer or defaultValue if parsing fails
	 */
	public int getDigitsOrDefault(int defaultValue) {
		try {
			return Integer.parseInt(cachedText);
		} catch (NumberFormatException e) {
			if (Debugger.isEnabled()) {
				System.err.println("Invalid number format in TextController. Switch validation mode to DIGITS_ONLY.\nInput: " + cachedText);
			}

			return defaultValue;
		}
	}

	/**
	 * Inserts a character at the specified position.
	 * 
	 * @param pos the position to insert at (will be constrained to valid range)
	 * @param ch the character to insert
	 */
	public final void insert(int pos, char ch) {
		insertInternal(pos, ch);
	}

	/**
	 * Inserts a string at the specified position.
	 * 
	 * @param pos the position to insert at (will be constrained to valid range)
	 * @param text the string to insert
	 */
	public final void insert(int pos, String text) {
		insertInternal(pos,text);
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
	 * Sets the text content from a StringBuilder, replacing any existing text.
	 * 
	 * @param text the StringBuilder containing new text content
	 */
	public final void set(StringBuilder text) {
		setInternal(text.toString());
	}

	/**
	 * Clears all text content.
	 * If the internal buffer capacity exceeds MAX_CAPACITY_FOR_CLEAR, trims the buffer.
	 */
	public final void clear() {
		if (isEmpty()) {
			return;
		}

		sb.setLength(0);
		if(sb.capacity() >= MAX_CAPACITY_FOR_CLEAR) {
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
	 * @param lastChar the ending position (exclusive, will be constrained)
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
	 * @param validation true to enable validation, false to disable
	 */
	public final void setValidationEnabled(boolean validation) {
		this.validationEnabled = validation;
	}

	/**
	 * Validates if a character is allowed based on the current validation mode.
	 * 
	 * @param ch the character to validate
	 * @return true if the character is valid, false otherwise
	 */
	public final boolean isValidChar(final char ch) {

		switch (validationMode) {

		case ALL:
			return STANDARD_VALIDATION.indexOf(ch) >= 0 || Character.isLetterOrDigit(ch);

		case ONLY_DIGITS:
			return Character.isDigit(ch);

		case ONLY_LETTERS:
			return Character.isLetter(ch);

		default:
			return STANDARD_VALIDATION.indexOf(ch) >= 0 || Character.isLetterOrDigit(ch);

		}

	}
	
	/**
	 * Notifies the after-character-insert listener if set.
	 */
	private void notifyOnAfterCharInsert() {
		if (onAfterCharInsertListener != null) {
			onAfterCharInsertListener.action();
		}
	}
	
	/**
	 * Notifies the after-string-insert listener if set.
	 */
	private void notifyOnAfterStringInsert() {
		if (onAfterStringInsertListener != null) {
			onAfterStringInsertListener.action();
		}
	}
	
	/**
	 * Notifies the text-changed listener if set.
	 */
	private void notifyOnTextChanged() {
		if (onTextChangedListener != null) {
			onTextChangedListener.action();
		}
	}
	
	/**
	 * Filters a string based on current validation rules.
	 * 
	 * @param src the source string to validate
	 * @return a string containing only valid characters from the source
	 */
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

	/**
	 * Updates cached string representations and notifies listeners.
	 */
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

	/**
	 * Applies character length constraints if enabled.
	 */
	private void updateConstrainedValue() {
		if (constrainEnabled) {
			if (length() > maxChars) {
				sb.setLength(maxChars);
				updateCachedStrings();
			}
		}
	}

	/**
	 * Internal method for character insertion with validation and constraints.
	 * 
	 * @param pos the position to insert at
	 * @param ch the character to insert
	 */
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
	
	/**
	 * Internal method for string insertion with validation and constraints.
	 * 
	 * @param pos the position to insert at
	 * @param str the string to insert
	 * @throws NullPointerException if str is null
	 */
	private void insertInternal(int pos, String str) {
		requireNonNull(str,"str");
		
		if (constrainEnabled && length() == maxChars) {
			return;
		}

		if (validationEnabled) {
			str = getValidatedString(str);
		}

		pos = (int) constrain(pos, 0, length());

		sb.insert(pos, str);
		
		if (constrainEnabled) {
			updateConstrainedValue();
		}
		
		updateCachedStrings();
		notifyOnAfterStringInsert();
	}
	
	/**
	 * Internal method for setting text with validation and constraints.
	 * 
	 * @param text the text to set
	 * @throws NullPointerException if text is null
	 */
	private void setInternal(String text) {
		requireNonNull(text, "text");
		
		if (text.equals(cachedText)) {
			return;
		}

		clear();

		sb.append(validationEnabled ? getValidatedString(text) : text);

		if (constrainEnabled) {
			updateConstrainedValue();
		}

		updateCachedStrings();
	}

	/**
	 * Validation modes for text input.
	 */
	public static enum ValidationMode {
		/** Allow letters, digits, and standard special characters. */
		ALL, 
		/** Allow only digit characters (0-9). */
		ONLY_DIGITS, 
		/** Allow only letter characters (a-z, A-Z). */
		ONLY_LETTERS;
	}
	
	/**
	 * Internal class managing undo/redo functionality.
	 */
	private final static class UndoRedoManager {
		/** Reference to the parent controller. */
		private final FullSingleLineTextController controller;
		/** Stacks for undo and redo operations. */
		private final Deque<String> undo, redo;
		/** Flag to prevent recursive updates during undo/redo operations. */
		private boolean operation;
		/** Listener for history change events. */
		private Listener onHistoryChangedListener;
		
		/**
		 * Constructs an UndoRedoManager for the specified controller.
		 * 
		 * @param controller the text controller to manage (cannot be null)
		 * @throws NullPointerException if controller is null
		 */
		public UndoRedoManager(FullSingleLineTextController controller) {
			this.controller = requireNonNull(controller,"controller");
			undo = new ArrayDeque<String>();
			redo = new ArrayDeque<String>();
		}

		/**
		 * Sets the listener for history change events.
		 * 
		 * @param onHistoryChangedListener the listener to set (cannot be null)
		 * @throws NullPointerException if listener is null
		 */
		public final void setOnHistoryChangedListener(Listener onHistoryChangedListener) {
			this.onHistoryChangedListener = requireNonNull(onHistoryChangedListener,"onHistoryChangedListener");
		}

		/**
		 * Performs an undo operation.
		 */
		public void undo() {
			operation = true;
			
			if (!undo.isEmpty()) {
				redo.push(undo.pop());
				if(!undo.isEmpty()) {
					controller.set(undo.peek());
				} else {
					controller.set("");
				}
				
				if(onHistoryChangedListener != null) {
					onHistoryChangedListener.action();
				}
			}
			
			operation = false;
		}
		
		/**
		 * Performs a redo operation.
		 */
		public void redo() {
			operation = true;
			if (!redo.isEmpty()) {
				undo.push(redo.peek());
				controller.set(redo.pop());
				
				if(onHistoryChangedListener != null) {
					onHistoryChangedListener.action();
				}
			}
			operation = false;
		}
		
		/**
		 * Updates the history state with current text.
		 * Skips update during undo/redo operations to prevent recursion.
		 */
		private void updateState() {
			if (operation) {
				return;
			}
			undo.push(controller.getHiddenText());
			redo.clear();
		}
		
	}
}