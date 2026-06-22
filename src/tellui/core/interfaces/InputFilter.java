package tellui.core.interfaces;

/**
 * Defines a filter for character input.
 * <p>
 * Implementations of this interface determine which characters are allowed
 * in text input components such as text fields or text areas. This allows
 * for custom validation rules (e.g., only digits, only letters, or a specific
 * set of allowed characters).
 * </p>
 */
public interface InputFilter {

	/**
	 * Determines whether the given character is allowed.
	 *
	 * @param ch the character to test
	 * @return {@code true} if the character is allowed, {@code false} otherwise
	 */
	boolean allow(char ch);
}