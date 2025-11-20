package microui.core;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

import java.util.ArrayDeque;
import java.util.Deque;

import microui.event.Listener;
import microui.util.Debugger;

public class FullSingleLineTextController {
	private static final String STANDARD_VALIDATION = "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*";
	private static final int DEFAULT_MAX_CHARS = 4;
	private static final int MIN_CONSTRAIN_VALUE = 1;
	private static final int MAX_CAPACITY_FOR_CLEAR = 100;
	private static final char DEFAULT_PASSWORD_CHAR = '*';

	private final UndoRedoManager undoRedoManager;
	private final StringBuilder sb;
	private StringBuilder adapterSb;
	private String cachedText, cachedPasswordText;
	private Listener onAfterCharInsertListener, onAfterStringInsertListener, onTextChangedListener; 
	private boolean validationEnabled, constrainEnabled, passwordModeEnabled;
	private int maxChars;
	private char passwordChar;
	private ValidationMode validationMode;

	public FullSingleLineTextController(final String text) {
		undoRedoManager = new UndoRedoManager(this);
		sb = new StringBuilder(requireNonNull(text,"text"));
		updateCachedStrings();

		validationEnabled = true;
		maxChars = DEFAULT_MAX_CHARS;
		validationMode = ValidationMode.ALL;

		setPasswordChar(DEFAULT_PASSWORD_CHAR);
	}

	public FullSingleLineTextController() {
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
	
	public final void setOnHistoryChangedListener(Listener onHistoryChangedListener) {
		undoRedoManager.setOnHistoryChangedListener(onHistoryChangedListener);
	}

	public void undo() {
		undoRedoManager.undo();
	}

	public void redo() {
		undoRedoManager.redo();
	}

	public final char getPasswordChar() {
		return passwordChar;
	}

	public final void setPasswordChar(char passwordChar) {
		if (this.passwordChar == passwordChar) {
			return;
		}
		this.passwordChar = passwordChar;
		updateCachedStrings();
	}

	public final boolean isPasswordModeEnabled() {
		return passwordModeEnabled;
	}

	public final void setPasswordModeEnabled(boolean passwordModeEnabled) {
		if (this.passwordModeEnabled == passwordModeEnabled) {
			return;
		}
		this.passwordModeEnabled = passwordModeEnabled;
		updateCachedStrings();
	}

	public final ValidationMode getValidationMode() {
		return validationMode;
	}

	public final void setValidationMode(ValidationMode validationMode) {
		if (this.validationMode == validationMode) {
			return;
		}

		this.validationMode = requireNonNull(validationMode, "validationMode");
		
		setInternal(getValidatedString(cachedText));
	}

	public final boolean isConstrainEnabled() {
		return constrainEnabled;
	}

	public final void setConstrainEnabled(boolean constrainEnabled) {
		if (this.constrainEnabled == constrainEnabled) {
			return;
		}

		this.constrainEnabled = constrainEnabled;
		updateConstrainedValue();
	}

	public final int getMaxChars() {
		return maxChars;
	}

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

	public final String getAsString() {
		return passwordModeEnabled ? cachedPasswordText : cachedText;
	}
	
	public final String getHiddenText() {
		return cachedText;
	}

	public final int getDigitsStrict() {
		return Integer.parseInt(cachedText);
	}

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

	public final void insert(int pos, char ch) {
		insertInternal(pos, ch);
	}

	public final void insert(int pos, String text) {
		insertInternal(pos,text);
	}

	public final void insert(int pos, int num) {
		insertInternal(pos, String.valueOf(num));
	}

	public final void set(String text) {
		setInternal(text);
	}

	public final void set(StringBuilder text) {
		setInternal(text.toString());
	}

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

	public final void removeCharAt(final int pos) {
		if (isEmpty()) {
			return;
		}

		sb.deleteCharAt((int) constrain(pos, 0, length() - 1));

		updateCachedStrings();
	}

	public final void remove(int firstChar, int lastChar) {
		if (isEmpty()) {
			return;
		}

		firstChar = (int) constrain(firstChar, 0, length());
		lastChar = (int) constrain(lastChar, 0, length());

		sb.delete(firstChar, lastChar);

		updateCachedStrings();
	}

	public final void removeFirstChar() {
		removeCharAt(0);
	}

	public final void removeLastChar() {
		removeCharAt(length() - 1);
	}

	public final int length() {
		return sb.length();
	}

	public final boolean isEmpty() {
		return length() == 0;
	}

	public final boolean isValidationEnabled() {
		return validationEnabled;
	}

	public final void setValidationEnabled(boolean validation) {
		this.validationEnabled = validation;
	}

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

	public static enum ValidationMode {
		ALL, ONLY_DIGITS, ONLY_LETTERS;
	}
	
	private final static class UndoRedoManager {
		private final FullSingleLineTextController controller;
		private final Deque<String> undo, redo;
		private boolean operation;
		private Listener onHistoryChangedListener;
		
		public UndoRedoManager(FullSingleLineTextController controller) {
			this.controller = requireNonNull(controller,"controller");
			undo = new ArrayDeque<String>();
			redo = new ArrayDeque<String>();
		}

		public final void setOnHistoryChangedListener(Listener onHistoryChangedListener) {
			this.onHistoryChangedListener = requireNonNull(onHistoryChangedListener,"onHistoryChangedListener");
		}

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
		
		private void updateState() {
			if (operation) {
				return;
			}
			undo.push(controller.getHiddenText());
			redo.clear();
		}
		
	}
}