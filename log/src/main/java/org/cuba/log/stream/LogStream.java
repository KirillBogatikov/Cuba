package org.cuba.log.stream;

public abstract class LogStream implements AutoCloseable {
    public abstract void write(LogRecord record);
}
