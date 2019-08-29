package org.kllbff.cuba.exceptions.parsing;

import org.kllbff.cuba.exceptions.CubaException;

public class CubaParsingException extends CubaException {
    private static final long serialVersionUID = 1591264376748398284L;

    public CubaParsingException() {
        super();
    }

    public CubaParsingException(String message) {
        super(message);
    }

    public CubaParsingException(Throwable cause) {
        super(cause);
    }

    public CubaParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CubaParsingException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
