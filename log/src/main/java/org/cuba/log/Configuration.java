package org.cuba.log;

import org.cuba.log.stream.LogStream;

/**
 * Provides {@link LogStream streams} for different log levels.
 * <p>
 * Methods {@link #info()}, {@link #error()}, {@link #warn}, {@link debug}<br>
 * should not returns null. If it returns null, behavior of {@link Log} is not guaranteed.
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0.0
 */
public interface Configuration {
    /**
     * Returns LogStream for {@link Level#INFO} logs level
     * 
     * @return LogStream for {@link Level#INFO} logs level
     */
    public LogStream info();

    /**
     * Returns LogStream for {@link Level#ERROR} logs level
     * 
     * @return LogStream for {@link Level#ERROR} logs level
     */
    public LogStream error();
    
    /**
     * Returns LogStream for {@link Level#WARN} logs level
     * 
     * @return LogStream for {@link Level#WARN} logs level
     */
    public LogStream warn();
    
    /**
     * Returns LogStream for {@link Level#DEBUG} logs level
     * 
     * @return LogStream for {@link Level#DEBUG} logs level
     */
    public LogStream debug();

    /**
     * This method should be implemented for releasing resources<br>
     * which was created or blocked at using time.<br>
     * This method will be automatically called when JVM starts<br>
     * killing of your process.
     * 
     * @throws Exception if some error occurred
     */
    public void dispose() throws Exception;
}
