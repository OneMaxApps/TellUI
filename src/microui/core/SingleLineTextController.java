package microui.core;

import static java.util.Objects.requireNonNull;
import static microui.util.MathUtils.constrain;

public class SingleLineTextController {
	private static final String STANDARD_VALIDATION = "!@#$%^&()_-+=|\\/[]{}<>,. ~\'\";:?*";
	private static final int MAX_CAPACITY_FOR_CLEAR = 100;
	private final StringBuilder sb;
	private StringBuilder adapterSb;
	private String cachedText;

	public SingleLineTextController(final String text) {
		sb = new StringBuilder(requireNonNull(text,"text"));
		updateCachedString();
	}

	public SingleLineTextController() {
		this("");
	}

	public final String getAsString() {
		return cachedText;
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
		
		updateCachedString();
	}

	public final void removeCharAt(final int pos) {
		if (isEmpty()) {
			return;
		}

		sb.deleteCharAt((int) constrain(pos, 0, length() - 1));

		updateCachedString();
	}

	public final void remove(int firstChar, int lastChar) {
		if (isEmpty()) {
			return;
		}

		firstChar = (int) constrain(firstChar, 0, length());
		lastChar = (int) constrain(lastChar, 0, length());

		sb.delete(firstChar, lastChar);

		updateCachedString();
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

	public final boolean isValidChar(final char ch) {
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

	protected void onAfterCharInsert() {
	}
	
	protected void onAfterStringInsert() {
	}

	protected void onTextChanged() {
	}

	private void updateCachedString() {
		cachedText = sb.toString();

		onTextChanged();
	}

	private void insertInternal(int pos, char ch) {
		if (!isValidChar(ch)) {
			return;
		}

		pos = (int) constrain(pos, 0, length());

		sb.insert(pos, ch);
		updateCachedString();
		onAfterCharInsert();
	}
	
	private void insertInternal(int pos, String str) {
		requireNonNull(str,"str");

		str = getValidatedString(str);
		
		pos = (int) constrain(pos, 0, length());

		sb.insert(pos, str);
		
		updateCachedString();
		onAfterStringInsert();
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
}