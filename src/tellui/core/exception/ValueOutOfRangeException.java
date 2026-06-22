package tellui.core.exception;

/**
 * Thrown to indicate that a value is outside its allowed range.
 * <p>
 * This exception is typically used by components and internal logic to reject
 * invalid values before they are applied. The error message includes the name
 * of the value, the offending value itself, and the expected bounds.
 * </p>
 *
 * @see TellUIException
 */
@SuppressWarnings("serial")
public class ValueOutOfRangeException extends TellUIException {

    /**
     * Constructs a {@code ValueOutOfRangeException} with a detailed message
     * that describes the violation.
     *
     * @param name  the human-readable name of the value
     * @param value the actual value that was rejected
     * @param min   the lower bound of the allowed range (inclusive)
     * @param max   the upper bound of the allowed range (inclusive)
     */
    public ValueOutOfRangeException(String name, float value, float min, float max) {
        super(String.format("Value \"%s\" (%f) is out of range: (%f : %f)", name, value, min, max));
    }
}