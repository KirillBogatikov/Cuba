package org.cuba.log;

import org.cuba.log.stream.LogStream;

public interface Configuration {
    public LogStream info();
    public LogStream error();
    public LogStream warn();
    public LogStream debug();
    
    public void dispose() throws Exception;
}
