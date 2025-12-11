package microui.core.exception;

/**
 * Base exception class for all MicroUI framework exceptions.
 * <p>
 * This class serves as the root exception for all custom exceptions thrown
 * by the MicroUI framework. It extends RuntimeException to indicate that
 * most MicroUI exceptions are unchecked exceptions.
 * </p>
 */
@SuppressWarnings("serial")
public class MicroUIException extends RuntimeException {

	/**
	 * Constructs a MicroUIException with the specified detail message.
	 * 
	 * @param message the detail message explaining the exception
	 */
	public MicroUIException(String message) {
		super(message);
	}

}