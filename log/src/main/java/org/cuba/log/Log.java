package org.cuba.log;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cuba.log.stream.LogRecord;
import org.cuba.log.stream.LogStream;

/**
 * Provides very simple and useful tool for logging events.
 * <p>
 * Log workflow is next:
 * <ol>
 *     <li>You call any logging method:
 *         <ul>
 *             <li>{@link #d(String)},</li>
 *             <li>{@link #d(String, String)},</li>
 *             <li>{@link #d(String, String, Object)}</li>
 *             <li>{@link #d(String, String, Throwable)}</li>
 *             <li>{@link #e(String)},</li>
 *             <li>{@link #e(String, String)},</li>
 *             <li>{@link #e(String, String, Object)}</li>
 *             <li>{@link #e(String, String, Throwable)}</li>
 *             <li>{@link #i(String)},</li>
 *             <li>{@link #i(String, String)},</li>
 *             <li>{@link #i(String, String, Object)}</li>
 *             <li>{@link #i(String, String, Throwable)}</li>
 *             <li>{@link #w(String)},</li>
 *             <li>{@link #w(String, String)},</li>
 *             <li>{@link #w(String, String, Object)}</li>
 *             <li>{@link #w(String, String, Throwable)}</li>
 *         </ul>
 *     </li>
 *     <li>Log retrieves from {@link Configuration} {@link LogStream} for log level</li>
 *     <li>Log collect info about event and pack it into {@link LogRecord}</li>
 *     <li>Log checks {@link Level} for enabled mutation</li>
 *     <li>If stream for {@link Level} was muted, Log stops the logging</li>
 *     <li>Otherwise Log check stream and writes {@link LogRecord} into it</li>
 * </ol>
 * <p>
 * You can manage logging by {@link #mute(Level) muting} and {@link #unmute(Level) unmuting} streams. 
 * <p>
 * Cuba Log - is very extensible and customizable logging tool. You can use simple unified interface of<br>
 * Log class in some places, but specify streams at start point. You can implement custom LogStream, for example,<br>
 * to send logs into database or file. 
 * 
 * @author Kirill Bogatikov
 * @version 1.2
 * @since 1.0.0
 */
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
    
    private void log(LogStream stream, Level level, String tag, String message, Throwable error, Object data) {
        if(muted.contains(level)) { 
            return; 
        }
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace(); 
        StackTraceElement stackTraceItem = stackTrace[stackTrace.length - 2];
        
        if(tag == null) {
            if(defaultTag == null) {
                tag = stackTraceItem.getClassName();
            } else {
                tag = defaultTag;
            }
        }
        
        LogRecord record = new LogRecord(level, PID, tag, message, System.currentTimeMillis(), stackTraceItem, error, data);
        
        synchronized(stream) {
            check(stream, level);
            stream.write(record);
        }
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
        log(config.info(), Level.INFO, tag, message, null, data);
    }
    
    public void i(String tag, String message, Throwable error) {
        log(config.info(), Level.INFO, tag, message, error, null);
    }
    
    public void d(String message) {
        d(null, message);
    }
    
    public void d(String tag, String message) {
        d(tag, message, null);
    }
    
    public void d(String tag, String message, Object data) {
        log(config.debug(), Level.DEBUG, tag, message, null, data);
    }
    
    public void d(String tag, String message, Throwable error) {
        log(config.debug(), Level.DEBUG, tag, message, error, null);
    }
    
    public void e(String message) {
        e(null, message);
    }
    
    public void e(String tag, String message) {
        e(tag, message, null);
    }
    
    public void e(String tag, String message, Object data) {
        log(config.error(), Level.ERROR, tag, message, null, data);
    }
    
    public void e(String tag, String message, Throwable error) {
        log(config.error(), Level.ERROR, tag, message, error, null);
    }
    
    public void w(String message) {
        w(null, message);
    }
    
    public void w(String tag, String message) {
        w(tag, message, null);
    }
    
    public void w(String tag, String message, Object data) {
        log(config.warn(), Level.WARN, tag, message, null, data);
    }
    
    public void w(String tag, String message, Throwable error) {
        log(config.warn(), Level.WARN, tag, message, error, null);
    }
}
