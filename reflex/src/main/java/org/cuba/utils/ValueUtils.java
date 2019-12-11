package org.cuba.utils;

/**
 * Provides tools to box and unbox values to/from wrappers, such as
 * {@link Integer}, {@link Boolean}, etc.
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 */
public class ValueUtils {
    /**
     * Allows box primitive values to its Object-based wrappers. <br>
     * Example 1:
     * <pre><code>
     *     Object intValue = /*some magic reflection actions*\/;
     *     Integer number = ValueUtils.box(intValue);
     * </code></pre>
     * 
     * @param object value for boxing 
     * @param <T> type of result, should be compatible with real result value
     * @return boxed value
     */
    @SuppressWarnings("unchecked")
    public static <T> T box(Object object) {
        if(object == null) {
            return null;
        }
        
        Class<?> type = object.getClass();
        if(type.isPrimitive()) {
            type = TypeUtils.box(type);
            return (T)type.cast(object);
        }
        
        throw new IllegalArgumentException("Can not box not primitive type");
    }
}
