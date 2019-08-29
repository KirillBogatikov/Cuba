package org.kllbff.cuba.exceptions.parsing;

public class ConverterNotFound extends CubaParsingException {
    private static final long serialVersionUID = 8778367039672596759L;

    public ConverterNotFound() {
        super();
    }

    public ConverterNotFound(String message) {
        super(message);
    }

    public ConverterNotFound(Throwable cause) {
        super(cause);
    }

    public ConverterNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
