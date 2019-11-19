package org.cuba.log.stream;

import org.cuba.log.Level;

public class LogRecord {
    private Level level;
    private String processId;
    private String tag;
    private String message;
    private long time;
    private StackTraceElement callerInfo;
    private Throwable error;
    private Object data;
    
    public LogRecord(Level level, String processId, String tag, String message, long time, 
                     StackTraceElement callerInfo, Throwable error, Object data) {
        this.level = level;
        this.processId = processId;
        this.tag = tag;
        this.message = message;
        this.time = time;
        this.callerInfo = callerInfo;
        this.error = error;
        this.data = data;
    }

    public Level getLevel() {
        return level;
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public String getTag() {
        return tag;
    }
    
    public String getMessage() {
        return message;
    }
    
    public long getTime() {
        return time;
    }
    
    public StackTraceElement getCallerInfo() {
        return callerInfo;
    }
    
    public Throwable getError() {
        return error;
    }
    
    public Object getData() {
        return data;
    }
}
