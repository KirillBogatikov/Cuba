package org.cuba.log.stream;

/**
 * Writes log record to some storage, other stream or visualize record.
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public abstract class LogStream implements AutoCloseable {
    /**
     * Writes log record.
     * <p>
     * <code>record</code> is not null value, it is always has value<br>
     * and there is no need to check the value of this argument.
     * <p>
     * This method can not throw exceptions, if some error occurred 
     * in this method's implementation, it should be processed by <b>this implementation</b>,
     * not Log or any library class.
     * 
     * @param record log record
     */
    public abstract void write(LogRecord record);
}
