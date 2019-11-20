package org.cuba.reflex;

import java.lang.reflect.Field;

/**
 * Provides methods to simple work with Fields by reflection api
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 */
public class FieldUtils {
    /**
     * Returns value of field retrieved from exists instance
     * 
     * @param fieldName name of field, can not be null, field can have any access modificator
     * @param instance object, from which value will be retrieved
     * @param <T> class instance 
     * @param <E> value received from field
     * @return value of specified field from instance object
     * 
     * @throws ReflexException if failed to access to field
     * @throws NullPointerException if field name is null
     */
    public static <T, E> E getValue(String fieldName, T instance) {
        if(fieldName == null) {
            throw new NullPointerException("Field name is null");
        }
        
        try {
            return getValue(instance.getClass().getDeclaredField(fieldName), instance);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new ReflexException(e);
        }
    }
    
    /**
     * Returns value of field retrieved from exists instance
     * 
     * @param field already given from class field, can not be null, field can have any access modificator
     * @param instance object, from which value will be retrieved
     * @param <T> class instance 
     * @param <E> value received from field
     * @return value of specified field from instance object
     * 
     * @throws ReflexException if failed to access to field
     * @throws NullPointerException if field is null
     */
    @SuppressWarnings("unchecked")
    public static <T, E> E getValue(Field field, T instance) {
        if(field == null) {
            throw new NullPointerException("Field is null");
        }
        
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        
        E value;
        try {
            value = (E)field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new ReflexException(e);
        }
        
        if(!accessible) {
            field.setAccessible(accessible);
        }
        
        return value;
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
        if(fieldName == null) {
            throw new NullPointerException("Field name is null");
        }
        
        try {
            setValue(instance.getClass().getDeclaredField(fieldName), instance, value);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new ReflexException(e);
        }
    }
    
    /**
     * Applies specified value to field in instance
     * 
     * @param field field, value of which will be changed
     * @param instance object, in the field of which new value will be applied
     * @param <T> class instance
     * @param value new value for field
     * 
     * @throws NullPointerException if field is null
     * @throws ReflexException if failed to get access to field 
     */
    public static <T> void setValue(Field field, T instance, Object value) {
        if(field == null) {
            throw new NullPointerException("Field is null");
        }
        
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new ReflexException(e);
        }
        
        if(!accessible) {
            field.setAccessible(accessible);
        }
    }
}
