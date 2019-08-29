package org.kllbff.cuba.exceptions;

public class CubaException extends Exception {
    private static final long serialVersionUID = 3992241928665273762L;

    public CubaException() {
        super();
    }

    public CubaException(String message) {
        super(message);
    }

    public CubaException(Throwable cause) {
        super(cause);
    }

    public CubaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CubaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
