package org.cuba.exception;

/**
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
public class ReflexException extends RuntimeException {
    private static final long serialVersionUID = -794179139780556345L;

    /**
     * 
     */
    public ReflexException() {
        super();
    }

    /**
     * @param message
     */
    public ReflexException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ReflexException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ReflexException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ReflexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
