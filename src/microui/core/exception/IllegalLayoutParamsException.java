package microui.core.exception;

import microui.core.interfaces.LayoutParams;

/**
 * Thrown when a layout parameter of an incompatible type is provided
 * to a container.
 * <p>
 * For example, passing {@code RowLayoutParams} to a {@code ColumnLayout}
 * will trigger this exception. The error message indicates the expected
 * parameter type and the actual type that was received.
 * </p>
 *
 * @see LayoutException
 */
@SuppressWarnings("serial")
public class IllegalLayoutParamsException extends LayoutException {

    /**
     * Constructs an {@code IllegalLayoutParamsException} with a message that
     * describes the type mismatch.
     *
     * @param expected the expected {@code LayoutParams} class
     * @param actual the actual layout params object that caused the error
     */
    public IllegalLayoutParamsException(Class<? extends LayoutParams> expected, LayoutParams actual) {
        super("Incorrect layout params: expected " + expected.getSimpleName() +
              " but got " + actual.getClass().getSimpleName());
    }
}