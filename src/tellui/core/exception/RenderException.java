package tellui.core.exception;

/**
 * Exception thrown when rendering operations encounter errors or invalid
 * states.
 * <p>
 * This exception is used to indicate problems during the rendering process,
 * such as attempting to draw outside of valid contexts, invalid render states,
 * or other rendering-related errors in the TellUI framework.
 * </p>
 */
@SuppressWarnings("serial")
public class RenderException extends MicroUIException {

	/**
	 * Constructs a RenderException with the specified detail message.
	 * 
	 * @param message the detail message explaining the rendering error
	 */
	public RenderException(String message) {
		super(message);
	}

}