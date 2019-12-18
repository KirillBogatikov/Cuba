package org.cuba.exception;

/**
 * Represents 'abstract' reflex exception, occurred at operation<br>
 * with Java reflection API or Cuba Reflex API
 * 
 * @author Kirill Bogatikov
 * @version 1.0.1
 * @since 1.0
 */
public class ReflexException extends RuntimeException {
    private static final long serialVersionUID = -794179139780556345L;

    /**
     * Default constructor. Creates empty exception without message, but with stack trace
     */
    public ReflexException() {
        super();
    }

    /**
     * Creates exception with specified message and stack trace
     * 
     * @param message string message for exception
     */
    public ReflexException(String message) {
        super(message);
    }

    /**
     * Creates exception caused by specified {@link Throwable}
     * 
     * @param cause 'parent' exception of this
     */
    public ReflexException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates exception caused by specified {@link Throwable} with<br>
     * specified message
     * 
     * @param message string message for exception
     * @param cause 'parent' exception of this
     */
    public ReflexException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates exception caused by specified {@link Throwable} with<br>
     * specified message
     * 
     * @param message string message for exception
     * @param cause 'parent' exception of this
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public ReflexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
