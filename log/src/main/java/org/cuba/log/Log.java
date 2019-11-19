package org.cuba.log;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashSet;
import java.util.Set;

import org.cuba.log.stream.LogRecord;
import org.cuba.log.stream.LogStream;

public class Log {        
    private static String PID = null;
    private static String TAG = Log.class.getSimpleName();
    
    static {
        try {
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            String jvmName = bean.getName();
            PID = jvmName.split("@")[0];
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    
    private static Log defaultLog;
    
    public static Log defaultLog() {
        if(defaultLog == null) {
            defaultLog = new Log(Configurator.defaultConfigurator().build());
        }
        return defaultLog;
    }
    
    private Configuration config;
    private Set<Level> softMuted;
    private Set<Level> hardMuted;
    
    public Log(Configuration config) {
        this.config = config;
        this.softMuted = new HashSet<>();
        this.hardMuted = new HashSet<>();
    }
    
    private void check(LogStream stream, Level level) {
        if(stream == null) {
            throw new NullPointerException(level + " stream is null");
        }
    }
    
    private void log(LogStream stream, Level level, LogRecord logRecord) {
        if(softMuted.contains(level)) { 
            return; 
        }
        if(hardMuted.contains(level)) {
            throw new IllegalStateException(level + " stream is muted");
        }
        
        check(stream, level);
        stream.write(logRecord);
    }
    
    private LogRecord newRecord(String tag, String message, Throwable error, Object data) {
        if(tag == null) {
            tag = TAG;
        }
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace(); 
        return new LogRecord(Level.INFO, PID, tag, message, System.currentTimeMillis(), stackTrace[stackTrace.length - 2], error, data);
    }

    public void mute(Level level) {
        mute(level, true);
    }
    
    public void mute(Level level, boolean soft) {
        if(soft) {
            softMuted.add(level);
        } else {
            hardMuted.add(level);
        }
    }
    
    public void unmute(Level level) {
        softMuted.remove(level);
        hardMuted.remove(level);
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
