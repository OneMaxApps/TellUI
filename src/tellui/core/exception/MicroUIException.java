package tellui.core.exception;

/**
 * Base exception class for all TellUI framework exceptions.
 * <p>
 * This class serves as the root exception for all custom exceptions thrown by
 * the TellUI framework. It extends RuntimeException to indicate that most
 * TellUI exceptions are unchecked exceptions.
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