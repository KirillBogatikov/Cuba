package org.kllbff.cuba.exceptions.parsing;

import java.lang.reflect.Type;

public class NotSupportedException extends CubaParsingException {
    private static final long serialVersionUID = 1023911952944996242L;
    
    public NotSupportedException(Type type) {
        this(type.getTypeName());
    }
    
    public NotSupportedException(String message) {
        super(message);
    }
    
    public NotSupportedException(Throwable cause) {
        super(cause);
    }
}
