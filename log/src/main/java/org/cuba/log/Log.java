package org.cuba.log;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cuba.log.stream.LogRecord;
import org.cuba.log.stream.LogStream;

public class Log {        
    private static String PID = null;
    
    static {
        try {
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            String jvmName = bean.getName();
            PID = jvmName.split("@")[0];
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    public static Log forClass(Class<?> clazz) {
        Log log = new Log(Configurator.system().build());
        log.defaultTag = clazz.getName();
        return log;
    }
    
    private String defaultTag;
    private Configuration config;
    private Set<Level> muted;
    
    public Log(Configuration config) {
        this.config = config;
        this.muted = Collections.synchronizedSet(new HashSet<>());
    }
    
    private void check(LogStream stream, Level level) {
        if(stream == null) {
            throw new NullPointerException(level + " stream is null");
        }
    }
    
    private void log(LogStream stream, Level level, LogRecord logRecord) {
        if(muted.contains(level)) { 
            return; 
        }
        
        synchronized(stream) {
            check(stream, level);
            stream.write(logRecord);
        }
    }
    
    private LogRecord newRecord(String tag, String message, Throwable error, Object data) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace(); 
        StackTraceElement stackTraceItem = stackTrace[stackTrace.length - 2];
        
        if(tag == null) {
            if(defaultTag == null) {
                tag = stackTraceItem.getClassName();
            } else {
                tag = defaultTag;
            }
        }
        
        return new LogRecord(Level.INFO, PID, tag, message, System.currentTimeMillis(), stackTraceItem, error, data);
    }

    public void mute(Level level) {
        muted.add(level);
    }
        
    public void unmute(Level level) {
        muted.remove(level);
    }
    
    public void i(String message) {
        i(null, message);
    }
    
    public void i(String tag, String message) {
        i(tag, message, null);
    }
    
    public void i(String tag, String message, Object data) {
        log(config.info(), Level.INFO, newRecord(tag, message, null, data));
    }
    
    public void i(String tag, String message, Throwable error) {
        log(config.info(), Level.INFO, newRecord(tag, message, error, null));
    }
    
    public void d(String message) {
        d(null, message);
    }
    
    public void d(String tag, String message) {
        d(tag, message, null);
    }
    
    public void d(String tag, String message, Object data) {
        log(config.debug(), Level.DEBUG, newRecord(tag, message, null, data));
    }
    
    public void d(String tag, String message, Throwable error) {
        log(config.debug(), Level.DEBUG, newRecord(tag, message, error, null));
    }
    
    public void e(String message) {
        e(null, message);
    }
    
    public void e(String tag, String message) {
        e(tag, message, null);
    }
    
    public void e(String tag, String message, Object data) {
        log(config.error(), Level.ERROR, newRecord(tag, message, null, data));
    }
    
    public void e(String tag, String message, Throwable error) {
        log(config.error(), Level.ERROR, newRecord(tag, message, error, null));
    }
    
    public void w(String message) {
        w(null, message);
    }
    
    public void w(String tag, String message) {
        w(tag, message, null);
    }
    
    public void w(String tag, String message, Object data) {
        log(config.warn(), Level.WARN, newRecord(tag, message, null, data));
    }
    
    public void w(String tag, String message, Throwable error) {
        log(config.warn(), Level.WARN, newRecord(tag, message, error, null));
    }
}
