package org.cuba.reflex;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.StringJoiner;

import org.cuba.utils.TypeUtils;

/**
 * Simple implementation of origin interface {@link java.lang.reflect.ParameterizedType}
 * <p>This class provides info about any ParameterizedType, such as List&lt;T&gt;, Map&lt;K, V&gt;, etc.<br>
 *    You can easily instantiate this class if you have info about raw type and parameters types</p>
 * <p>For example, next code created ParameterizedType for HashMap&lt;String, List&lt;Date&gt;&gt;:
 * <pre>
 *     Type listType = new ParameterizedType(null, List.class, Date.class);
 *     Type mapType = new ParameterizedType(null, HashMap.class, String.class, listType);
 * </pre>
 * <p><b>Warning!</b> Do not use this class for Wildcard types, such as List&lt;?&gt;. This class works <br>
 *    only with strong-parameterized types!
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 */
public class ParameterizedType implements java.lang.reflect.ParameterizedType {
    private Type ownerType;
    private Type rawType;
    private Type[] typeArguments;

    /**
     * Initializes fields of class and verify specified types
     * <p>If at least one stage of verification failed, method throws exception</p>
     * 
     * @see java.lang.reflect.Type
     * @see java.lang.Class
     * @see TypeUtils
     * 
     * @param ownerType type of enclosing class, can be null
     * @param rawType specified type of generic class, can not be null
     * @param typeArguments array (vararg) of parameters types for generic 
     * 
     * @throws NullPointerException is <code>rawType</code> is null
     * @throws IllegalArgumentException if <code>ownerType</code> is null, but <code>rawType</code> has enclosing type
     * @throws IllegalArgumentException if <code>rawType</code> has not enclosing type, but <code>ownerType</code> was specified
     * @throws NullPointerException if at least one Type from <code>typeArguments</code> is null
     * @throws IllegalArgumentException if <code>typeArguments</code> contains primitive type (int, long, etc.)
     */
    public ParameterizedType(Type ownerType, Type rawType, Type... typeArguments) {
        if(rawType == null) {
            throw new NullPointerException("RawType is null");
        }
        
        if(rawType instanceof Class<?>) {
            Class<?> enclosingClass = TypeUtils.getEnclosingClass(rawType);
            if(ownerType == null) {
                if(enclosingClass != null) {
                    throw new IllegalArgumentException("OwnerType is null, but RawType has enclosing class " + enclosingClass);
                }
            } else {
                if(enclosingClass == null) {
                    throw new IllegalArgumentException("RawType has not enclosing class, but OwnerType specified by " + ownerType);
                }
            }
        }
        
        for(Type parameter : typeArguments) {
            if(parameter == null) {
                throw new NullPointerException("null found in TypeArguments array");
            }
            if(TypeUtils.isPrimitive(parameter)) {
                throw new IllegalArgumentException("Primitive type " + parameter + " found in TypeArguments array");
            }
        }

        this.ownerType = ownerType;
        this.rawType = rawType;
        this.typeArguments = typeArguments.clone();
    }

    public ParameterizedType(Type type) {
        if(type instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType)type;
            this.ownerType = paramType.getOwnerType();
            this.rawType = paramType.getRawType();
            this.typeArguments = paramType.getActualTypeArguments();
        } else {
            throw new IllegalArgumentException(type + " is not instance of Java Reflect ParameterizedType");
        }
    }
    
    public Type[] getActualTypeArguments() {
        return typeArguments.clone();
    }

    public Type getRawType() {
        return rawType;
    }

    public Type getOwnerType() {
        return ownerType;
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)other;
            
            if(!ownerType.equals(paramType.ownerType)) {
                return false;
            }
            
            if(!rawType.equals(paramType.rawType)) {
                return false;
            }
            
            if(!typeArguments.equals(paramType.typeArguments)) {
                return false;
            }
            
            return true;
        }
        return false;
    }

    @Override public int hashCode() {
        int hash = Arrays.hashCode(typeArguments);
        hash ^= rawType.hashCode();
        if(ownerType != null) {
            hash ^= ownerType.hashCode();
        }
        
        return hash;
    }
    
    @Override
    public String toString() {
        if(typeArguments.length == 0) {
            return TypeUtils.toString(rawType);
        }            
            
        StringBuilder typeString = new StringBuilder();
        typeString.append(TypeUtils.toString(rawType));
        
        StringJoiner parametersString = new StringJoiner(", ");
        for (Type parameter : typeArguments) {
            parametersString.add(TypeUtils.toString(parameter));
        }
        
        typeString.append("<").append(parametersString).append(">");
        return typeString.toString();
    }
}
