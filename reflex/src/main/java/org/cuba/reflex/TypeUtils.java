package org.cuba.reflex;

import java.lang.reflect.Type;

/**
 * Provides some easy-to-use methods to simplify work with reflection with Types in Java 
 * 
 * @see java.lang.reflect.Type
 * @see java.lang.Class
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 */
public class TypeUtils {
    /**
     * Returns stringified content of specified <code>type</code>
     * <p>For example, for Integer.class this methods returns 'class java.lang.Integer', but
     *    for ArrayList&lt;Integer&gt; it will return 'class ArrayList&lt;Integer&gt;'
     * 
     * @param type specified Type
     * @return stringified content of specified <code>type</code>
     */
    public static String toString(Type type) {
        if(type == null) {
            throw new NullPointerException("Type is null");
        }
        if(type instanceof Class) {
            return ((Class<?>)type).getName();
        }
        return type.toString();
    }
    
    /**
     * Returns 'owner' enclosing class for specified <code>type</code>
     * <pre>Example: <code>
     *      class MyClass {
     *          class Builder {
     *          
     *          }
     *      }
     * </code>
     * For MyClass.Builder.class this method will return MyClass.class</pre>
     * 
     * @param type spcified Type
     * @return 'owner' enclosing class for specified <code>type</code>
     */
    public static Class<?> getEnclosingClass(Type type) {
        if(type == null) {
            throw new NullPointerException("Type is null");
        }
        if(type instanceof Class) {
            return ((Class<?>)type).getEnclosingClass();
        }
        return null;
    }
    
    /**
     * Returns true if specified <code>type</code> represents any of primitive types,<br>
     * such as int, long, boolean, etc.
     * 
     * @param type specified Type
     * @return true if specified <code>type</code> represents any of primitive types, false otherwise
     */
    public static boolean isPrimitive(Type type) {
        if(type == null) {
            throw new NullPointerException("Type is null");
        }
        if(type instanceof Class) {
            return ((Class<?>)type).isPrimitive();
        }
        return false;
    }
    
    /**
     * Returns true if specified Class <code>subclass</code> represents a subclass of <code>target</code> class
     * 
     * @param sub subclass candidate
     * @param target specified 'parent' class
     * @return true if <code>subclass</code> is subclass of <code>target</code>
     */
    public static boolean isSubclass(Class<?> sub, Class<?> target) {
        if(sub == null) {
            throw new NullPointerException("Subclass is null");
        }
        if(target == null) {
            throw new NullPointerException("Target class is null");
        }
        try {
            sub.asSubclass(target);
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }
    
    /**
     * Returns class of wrapper for specified primitive type
     * <p>This method supports all Java 8 primitive types:<br>
     * {@link Integer} - int<br>
     * {@link Float} = float<br>
     * {@link Long} = long<br>
     * {@link Double} = double<br>
     * {@link Short} = short<br>
     * {@link Byte} = byte<br>
     * {@link Boolean} = boolean<br>
     * {@link Character} = char<br>
     * 
     * @param primitiveType one of Java 8's primitive type, e.g int.class
     * @return class of wrapper for specified primitive type
     * @throws NullPointerException if <code>primitiveType</code> is null
     * @throws IllegalArgumentException if specified type is not primitive
     */
    public static Class<?> box(Class<?> primitiveType) {
        if(primitiveType == null) {
            throw new NullPointerException("Type is null");
        }
        if(!primitiveType.isPrimitive()) {
            throw new IllegalArgumentException("Type must be primitive, e.g. int, short, boolean, etc.");
        }
        
        if(primitiveType.equals(int.class)) {
            return Integer.class;
        }
        if(primitiveType.equals(float.class)) {
            return Float.class;
        }
        if(primitiveType.equals(long.class)) {
            return Long.class;
        }
        if(primitiveType.equals(double.class)) {
            return Double.class;
        }
        if(primitiveType.equals(short.class)) {
            return Short.class;
        }
        if(primitiveType.equals(byte.class)) {
            return Byte.class;
        }
        if(primitiveType.equals(boolean.class)) {
            return Boolean.class;
        }
        if(primitiveType.equals(char.class)) {
            return Character.class;
        }
        /* never or on new platforms with new types */
        return null;
    }
}
