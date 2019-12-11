package org.cuba.utils;

import java.lang.reflect.Field;

import org.cuba.exception.ReflexException;

/**
 * Provides methods to simple work with Fields by reflection api
 * 
 * @author Kirill Bogatikov
 * @version 1.1
 * @since 1.0.0
 */
public class FieldUtils {
    /**
     * Returns value of field retrieved from exists instance
     * or throws ReflexException if access denied by JVM
     * 
     * @param fieldName name of field, can not be null, field can have any access modificator
     * @param instance object, from which value will be retrieved
     * @param <I> class instance 
     * @param <V> value received from field
     * @return value of specified field from instance object
     * 
     * @throws ReflexException if failed to access to field
     * @throws NullPointerException if field name is null
     */
    public static <I, V> V getValue(String fieldName, I instance) {
        return getValue(fieldName, instance, false);
    }
    
    /**
     * Returns value of field retrieved from exists instance
     * or throws ReflexException if <code>agressive</code> param is <code>false</code>
     * and access denied by JVM
     * 
     * @param fieldName name of field, can not be null, field can have any access modificator
     * @param instance object, from which value will be retrieved
     * @param agressive true, if acess modifiers (private, protected and package-private) should be ignored, false otherwise
     * @param <I> class instance 
     * @param <V> value received from field
     * @return value of specified field from instance object
     * 
     * @throws ReflexException if failed to access to field
     * @throws NullPointerException if field name is null
     */
    public static <I, V> V getValue(String fieldName, I instance, boolean agressive) {
        if(fieldName == null) {
            throw new NullPointerException("Field name is null");
        }
        
        try {
            return getValue(instance.getClass().getDeclaredField(fieldName), instance, agressive);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new ReflexException(e);
        }
    }
    
    /**
     * Returns value of field retrieved from exists instance
     * 
     * @param field already given from class field, can not be null, field can have any access modificator
     * @param instance object, from which value will be retrieved
     * @param <I> class instance 
     * @param <V> value received from field
     * @return value of specified field from instance object
     * 
     * @throws ReflexException if failed to access to field
     * @throws NullPointerException if field is null
     */
    public static <I, V> V getValue(Field field, I instance) {
        return getValue(field, instance, false);
    }
    
    /**
     * Returns value of field retrieved from exists instance
     * 
     * @param field already given from class field, can not be null, field can have any access modificator
     * @param instance object, from which value will be retrieved
     * @param agressive true, if acess modifiers (private, protected and package-private) should be ignored, false otherwise
     * @param <I> class instance 
     * @param <V> value received from field 
     * @return value of specified field from instance object
     * 
     * @throws ReflexException if failed to access to field
     * @throws NullPointerException if field is null
     */
    @SuppressWarnings("unchecked")
    public static <I, V> V getValue(Field field, I instance, boolean agressive) {
        if(field == null) {
            throw new NullPointerException("Field is null");
        }
        
        boolean accessible = field.isAccessible();
        setAccessibleIfNeeded(field, accessible, agressive, true);
        
        Object value;
        try {
            value = field.get(instance);            
            setAccessibleIfNeeded(field, accessible, agressive, false);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            setAccessibleIfNeeded(field, accessible, agressive, false);
            throw new ReflexException(e);
        } 
        
        return (V)value;
    }
    
    private static void setAccessibleIfNeeded(Field field, boolean wasAccessible, boolean agressive, boolean accessible) {
        if(!accessible && agressive) {
            field.setAccessible(accessible);
        }
    }
    
    /**
     * Applies specified value to field in instance
     * 
     * @param fieldName name of field, value of which will be changed
     * @param instance object, in the field of which new value will be applied
     * @param <T> class instance 
     * @param value new value for field
     * 
     * @throws NullPointerException if field name is null
     * @throws ReflexException if failed to get access to field
     */
    public static <T> void setValue(String fieldName, T instance, Object value) {
        setValue(fieldName, instance, value, false);
    }
    
    /**
     * Applies specified value to field in instance
     * 
     * @param fieldName name of field, value of which will be changed
     * @param instance object, in the field of which new value will be applied
     * @param agressive true, if acess modifiers (private, protected and package-private) should be ignored, false otherwise
     * @param <T> class instance 
     * @param value new value for field
     * 
     * @throws NullPointerException if field name is null
     * @throws ReflexException if failed to get access to field
     */
    public static <T> void setValue(String fieldName, T instance, Object value, boolean agressive) {
        if(fieldName == null) {
            throw new NullPointerException("Field name is null");
        }
        
        try {
            setValue(instance.getClass().getDeclaredField(fieldName), instance, value, agressive);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new ReflexException(e);
        }
    }
    
    /**
     * Applies specified value to field in instance
     * 
     * @param field field, value of which will be changed
     * @param instance object, in the field of which new value will be applied
     * @param <I> class instance
     * @param value new value for field
     * 
     * @throws NullPointerException if field is null
     * @throws ReflexException if failed to get access to field 
     */
    public static <I> void setValue(Field field, I instance, Object value) {
        setValue(field, instance, value, false);
    }
    
    /**
     * Applies specified value to field in instance
     * 
     * @param field field, value of which will be changed
     * @param instance object, in the field of which new value will be applied
     * @param <I> class instance
     * @param value new value for field
     * @param agressive true, if acess modifiers (private, protected and package-private) should be ignored, false otherwise
     * 
     * @throws NullPointerException if field is null
     * @throws ReflexException if failed to get access to field 
     */
    public static <I> void setValue(Field field, I instance, Object value, boolean agressive) {
        if(field == null) {
            throw new NullPointerException("Field is null");
        }
        
        boolean accessible = field.isAccessible();
        setAccessibleIfNeeded(field, accessible, agressive, true);
        
        try {
            field.set(instance, value);
            setAccessibleIfNeeded(field, accessible, agressive, false);
        } catch (IllegalAccessException e) {
            setAccessibleIfNeeded(field, accessible, agressive, false);
            throw new ReflexException(e);
        }
    }
}
