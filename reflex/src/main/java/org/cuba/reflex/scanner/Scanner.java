package org.cuba.reflex.scanner;

import java.io.IOException;
import java.util.List;

/**
 * Scans specified classpath for all classes
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public interface Scanner {
    /**
     * Specifies list of {@link ClassLoader}s for scanning
     * 
     * @param loaders list of class loaders
     */
    public void use(List<ClassLoader> loaders);
    
    /**
     * Prepares Scanner before scanning by opening classpath source<br>
     * and loading or refreshing necessary resources
     * 
     * @param path classpath item
     * @throws IOException if some error occurred at resource preparing
     */
    public void open(String path) throws IOException;
    
    /**
     * Pre-loads a few classes. It will never load more<br>
     * than the specified number, but it indicates the load limit<br>and classes can be loaded many times less.
     * <p>If found class doesn't loaded by one of specified early {@link ClassLoader}s Scanner should skip this class</p>
     * 
     * @param count limit of loaded classed
     * @return read count of loaded classed
     * @throws IOException if some error occurred at loading
     */
    public int preload(int count) throws IOException;
    
    /**
     * Returns next class or null if it not found
     * <p>If Scanner has preloaded classes, this method returns next class from preloaded list
     * <p>If Scanner has available resources, it loads class and returns it
     * <p>Otherwise - returns null
     * <p>If this method once returns null, it can be regarded as the end of the scanner<br>
     * and the lack of free raw resources
     * <p>Note! If this Scanner was closed, behaviour of this method does not guaranteed
     *  
     * @return next class or null if it can not be loaded
     * @throws IOException if some error occurred at loading
     */
    public Class<?> next() throws IOException;
    
    /**
     * Closes Scanner and releases all resources
     * 
     * @throws IOException
     */
    public void close() throws IOException;
    
    /**
     * Returns true if all resources are prepared and
     * this scanner is ready to scanning classes
     * 
     * @return true if this scanner is ready to scanning classes, false otherwise
     */
    public boolean ready();
}