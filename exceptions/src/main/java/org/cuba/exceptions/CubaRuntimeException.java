package org.cuba.exceptions;

public class CubaRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -8495648321552004966L;

    public CubaRuntimeException() {
        super();
    }

    public CubaRuntimeException(String message) {
        super(message);
    }

    public CubaRuntimeException(Throwable cause) {
        super(cause);
    }

    public CubaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CubaRuntimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
