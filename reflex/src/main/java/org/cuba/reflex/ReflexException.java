package org.cuba.reflex;

public class ReflexException extends RuntimeException {
    private static final long serialVersionUID = -9121480959935548370L;

    public ReflexException() {
        super();
    }

    public ReflexException(String message) {
        super(message);
    }

    public ReflexException(Throwable cause) {
        super(cause);
    }

    public ReflexException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
