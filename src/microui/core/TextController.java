package microui.core;

import static java.util.Objects.requireNonNull;
import static processing.core.PApplet.constrain;

import microui.util.Debugger;

public abstract class TextController {
	private static final String STANDARD_VALIDATION = "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:№?*";
	private static final int MIN_CONSTRAIN_VALUE = 1;
	private static final int DEFAULT_MAX_CONSTRAIN_VALUE = 4;
	private static final char DEFAULT_PASSWORD_CHAR = '*';

	private final StringBuilder sb;
	private String cachedText, cachedPasswordText;
	private boolean validationEnabled, constrainEnabled, passwordModeEnabled;
	private int maxChars;
	private char passwordChar;
	private ValidationMode validationMode;

	public TextController(final String text) {
		sb = new StringBuilder(text);
		updateCachedStrings();

		validationEnabled = true;
		maxChars = DEFAULT_MAX_CONSTRAIN_VALUE;
		validationMode = ValidationMode.ALL;

		setPasswordChar(DEFAULT_PASSWORD_CHAR);
	}

	public TextController() {
		this("");
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
		this.validationMode = requireNonNull(validationMode, "validationMode");
	}

	public final boolean isConstrainEnabled() {
		return constrainEnabled;
	}

	public final void setConstrainEnabled(boolean constrainEnabled) {
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

		this.maxChars = maxChars;

		updateConstrainedValue();
	}

	public final String getAsString() {
		return passwordModeEnabled ? cachedPasswordText : cachedText;
	}

	public final int getDigits() {
		try {
			return Integer.parseInt(cachedText);
		} catch (NumberFormatException e) {
			if (Debugger.isDebugModeEnabled()) {
				System.err.println(
						"Invalid number format in TextController. switch validation mode to DIGITS_ONLY.\nInput: "
								+ cachedText);
			}

			return 0;
		}
	}

	public final void insert(int pos, char ch) {
		insertInternal(pos, ch);
	}

	public final void insert(int pos, String text) {
		requireNonNull(text, "text");

		for (int i = 0; i < text.length(); i++) {
			insert(pos, text.charAt(i));
		}
	}

	public final void insert(int pos, int num) {
		insert(pos, String.valueOf(num));
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
		sb.trimToSize();

		updateCachedStrings();
	}

	public final void removeCharAt(final int pos) {
		if (isEmpty()) {
			return;
		}

		sb.deleteCharAt(constrain(pos, 0, length() - 1));

		updateCachedStrings();
	}

	public final void remove(int firstChar, int lastChar) {
		if (isEmpty()) {
			return;
		}
		
		firstChar = constrain(firstChar, 0, length());
		lastChar = constrain(lastChar, 0, length());

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
			return STANDARD_VALIDATION.contains(String.valueOf(ch)) || Character.isLetterOrDigit(ch);

		case ONLY_DIGITS:
			return Character.isDigit(ch);

		case ONLY_LETTERS:
			return Character.isLetter(ch);

		default:
			return STANDARD_VALIDATION.contains(String.valueOf(ch)) || Character.isLetterOrDigit(ch);

		}

	}

	protected void onAfterInsert() {
	}

	private void updateCachedStrings() {
		cachedText = sb.toString();

		if (passwordModeEnabled) {
			cachedPasswordText = String.valueOf(passwordChar).repeat(cachedText.length());
		} else {
			cachedPasswordText = null;
		}

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
		if (constrainEnabled && length() >= maxChars) {
			return;
		}

		pos = constrain(pos, 0, length());

		if (validationEnabled) {
			if (isValidChar(ch)) {
				sb.insert(pos, ch);

				updateCachedStrings();
				onAfterInsert();
			}
		} else {
			sb.insert(pos, ch);

			updateCachedStrings();
			onAfterInsert();
		}

	}

	private void setInternal(String text) {
		requireNonNull(text, "text");

		if(text.equals(cachedText)) {
			return;
		}
		
		clear();

		if (validationEnabled) {
			for (int i = 0; i < text.length(); i++) {
				if (constrainEnabled && length() == maxChars) {
					break;
				}
				if (isValidChar(text.charAt(i))) {
					sb.append(text.charAt(i));
				}
			}
		} else {
			sb.append(text);
			if (constrainEnabled && length() > maxChars) {
				sb.setLength(maxChars);
				sb.trimToSize();
			}
		}
		
		updateCachedStrings();
		onAfterInsert();
	}

	public static enum ValidationMode {
		ALL, ONLY_DIGITS, ONLY_LETTERS;
	}
}