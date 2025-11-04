package microui.core;

import static java.util.Objects.requireNonNull;
import static processing.core.PApplet.constrain;

import microui.util.Debugger;

public abstract class TextController {
	private static final String STANDART_VALIDATION = "!@#$%^&*()_-+=|\\/[]{}<>,. \'\";:№?*";
	private final StringBuilder sb;
	private boolean validationEnabled, constrainEnabled;
	private int maxChars;
	private ValidationMode validationMode;

	public TextController(final String text) {
		sb = new StringBuilder(text);
		validationEnabled = true;
		maxChars = 10;
		validationMode = ValidationMode.ALL;
	}

	public TextController() {
		this("");
	}

	public void set(final String text) {
		requireNonNull(text, "text");

		onTextSet();

		if (!isEmpty()) {
			clear();
		}
		
		insert(0, text);

	}
	
	public final ValidationMode getValidationMode() {
		return validationMode;
	}

	public final void setValidationMode(ValidationMode validationMode) {
		this.validationMode = requireNonNull(validationMode,"validationMode");
	}

	public final boolean isConstrainEnabled() {
		return constrainEnabled;
	}

	public final void setConstrainEnabled(boolean constrainEnabled) {
		this.constrainEnabled = constrainEnabled;
	}

	public final int getMaxChars() {
		return maxChars;
	}

	public final void setMaxChars(int maxChars) {
		if (maxChars < 1) {
			throw new IllegalArgumentException("Max chars cannot be less than 1");
		}

		this.maxChars = maxChars;
	}

	public final void set(final TextController text) {
		set(text.getAsString());
	}

	public final String getAsString() {
		return sb.toString();
	}

	public final int getDigits() {
		try {
			return Integer.parseInt(sb.toString());
		} catch(NumberFormatException e) {
			if(Debugger.isDebugModeEnabled()) {
				System.err.println("Invalid number format in TextController when validation mode: DIGITS_ONLY.\nInput: "+sb.toString());
			}
			
			return 0;
		}
	}

	public final void insert(final int pos, final char ch) {
		if (pos < 0 || pos > length()) {
			return;
		}
		if (constrainEnabled && length() >= maxChars) {
			return;
		}

		if (validationEnabled) {
			if (isValidChar(ch)) {
				sb.insert(pos, ch);
				onInsert();
			}
		} else {
			sb.insert(pos, ch);
		}
	}

	public final void insert(final int pos, final String text) {
		requireNonNull(text,"text");
		
		for(int i = 0; i < text.length(); i++) {
			insert(pos,text.charAt(i));
		}
	}

	public final void insert(final int pos, final int num) {
		insert(pos,String.valueOf(num));
	}

	public final void clear() {
		if (!isEmpty()) {
			sb.setLength(0);
			sb.trimToSize();
		}
	}

	public final boolean isEmpty() {
		return length() == 0;
	}

	public final void removeCharAt(final int pos) {
		if (isEmpty()) {
			return;
		}
		
		sb.deleteCharAt(constrain(pos, 0, length() - 1));
	}
	
	public final void remove(int firstChar, int lastChar) {
		sb.delete(firstChar, lastChar);
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

	public final boolean isValidationEnabled() {
		return validationEnabled;
	}

	public final void setValidationEnabled(boolean validation) {
		this.validationEnabled = validation;
	}

	public final boolean isValidChar(final char ch) {
		
		switch (validationMode) {
		case ALL:
			return STANDART_VALIDATION.contains(String.valueOf(ch)) || Character.isLetterOrDigit(ch);
		case ONLY_DIGITS:
			return Character.isDigit(ch);
		case ONLY_LETTERS:
			return Character.isLetter(ch);

		default:
			return STANDART_VALIDATION.contains(String.valueOf(ch)) || Character.isLetterOrDigit(ch);
		}

	}

	protected void onInsert() {
	}

	protected void onTextSet() {
	}

	public static enum ValidationMode {
		ALL, ONLY_DIGITS, ONLY_LETTERS;
	}
}