package org.cuba.reflex;

public class ValueUtils {
    public static Object box(Object object, Class<?> type) {
        type = TypeUtils.box(type);
        return type.cast(object);
    }
}
