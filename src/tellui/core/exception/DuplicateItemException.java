package tellui.core.exception;

/**
 * Exception thrown when attempting to add a duplicate item to a MenuButton or
 * container that does not allow duplicates.
 * <p>
 * This exception is used throughout the TellUI framework to indicate that an
 * operation failed because it would result in duplicate items where uniqueness
 * is required. Commonly used in container and collection management.
 * </p>
 * 
 * @see MicroUIException
 */
@SuppressWarnings("serial")
public class DuplicateItemException extends MicroUIException {

	/**
	 * Constructs a DuplicateItemException with the specified detail message.
	 * 
	 * @param message the detail message explaining the duplicate item violation
	 */
	public DuplicateItemException(String message) {
		super(message);
	}

}