package org.cuba.reflex;

import java.util.HashMap;
import java.util.Map;

/**
 * Reperesents a singleton storage for loaded classes.
 * <p>Scanning and loading classes by Java's Reflection API is very cost and slow operation.
 * <p>For time and resources savings this class caches found classes into thread-safe map
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class ClassStorage {
    private static ClassStorage instance;
    
    /**
     * Returns current instance of ClassStorage or creates new and returns it
     * 
     * @return current instance of ClassStorage
     */
    public static ClassStorage getInstance() {
        if(instance == null) {
            instance = new ClassStorage();
        }
        
        return instance;
    }
    
    private volatile Map<String, Class<?>> classes;
    private volatile boolean system;
    
    private ClassStorage() {
        classes = new HashMap<>();
    }
    
    /**
     * Returns true, if no classes loaded into this storage
     * 
     * @return true if this storage is empty, false otherwise
     */
    public boolean isRaw() {
        return classes.isEmpty();
    }
        
    /**
     * Returns true, if this storage contains system classes
     * 
     * @return true, if this storage contains system classes, false otherwise
     */
    public boolean hasSystem() {
        return system;
    }
    
    /**
     * Returns map in which all classes are saved
     * 
     * @return map in which all classes are saved
     */
    public Map<String, Class<?>> map() {
        return classes;
    }
    
    /**
     * Adds class into map
     * 
     * @param clazz class
     * @param system true if clazz is system
     */
    public void add(Class<?> clazz, boolean system) {
        classes.put(clazz.getName(), clazz);
        this.system |= system;
    }
    
    /**
     * Returns class associated with specified name
     * 
     * @param full name name of class
     * @return class found for name, or null if it is not found
     */
    public Class<?> get(String name) {
        return classes.get(name);
    }
}