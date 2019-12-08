package org.cuba.log.stream;

import org.cuba.log.Level;

/**
 * <p>
 * Represents one log record item. Every log method's call leads to
 * instantiation of LogRecord item, which will be processed by LogStream.
 * 
 * <p>
 * LogRecord stores all necessary information about the log event:
 * <ul>
 *     <li>log level ({@link Level Level enum});</li>
 *     <li>process id ({@link String});</li>
 *     <li>tag ({@link String});</li>
 *     <li>message ({@link String}, if it exists);</li>
 *     <li>date/time in milliseconds;</li>
 *     <li>information about {@link java.lang.reflect.Method method} from which logged event by {@link StackTraceElement}</li>
 *     <li>{@link Throwable throwable} instance, if event logs error;</li>
 *     <li>data {@link Object object} provided by event</li>
 * </ul> 
 * 
 * @see Level
 * @see StackTraceElement
 * @see LogStream
 * @see Throwable
 * @see Log
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class LogRecord {
    private Level level;
    private String processId;
    private String tag;
    private String message;
    private long time;
    private StackTraceElement callerInfo;
    private Throwable error;
    private Object data;
    
    /**
     * Constructs new LogRecord from given data.
     * 
     * @param level level of event
     * @param processId id of event's process
     * @param tag event's tag
     * @param message log message
     * @param time date/time of event
     * @param callerInfo information about method, which logs event
     * @param error error provided by event or null
     * @param data object provided by event or null
     */
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

    /**
     * Returns level of event as one of {@link Level level enum}.
     * 
     * @return level of event
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * Returns stringified process's id of event.
     * 
     * @return id of event's process
     */
    public String getProcessId() {
        return processId;
    }
    
    /**
     * Returns tag of event.
     * 
     * @return event's tag
     */
    public String getTag() {
        return tag;
    }
    
    /**
     * Returns message provided with event.
     * 
     * @return event's message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Returns time and date when event was logged
     * 
     * @return time/date in milliseconds
     */
    public long getTime() {
        return time;
    }
    
    /**
     * Returns info about event's method.
     * <p>
     * For example, for this situation:
     * <pre><code>
     * 1 package org.pkg;
     * 2 
     * 3 public class MyClass {
     * 4    public void myMethod() {
     * 5         log.e("TAG", "FAIL", new NullPointerException());
     * 6     }
     * 7 }
     * </code></pre>
     * this method returns {@link StackTraceElement} about fifth line of MyClass's file,
     * in particular about method <code>org.pkg.MyClass.myMethod</code>
     * 
     * @see StackTraceElement StackTraceElement for more information about representing stack trace item
     * 
     * @return info about event's method
     */
    public StackTraceElement getCallerInfo() {
        return callerInfo;
    }
    
    /**
     * Returns error provided by event or null if it was not specified.
     * 
     * @return error provided by event or null
     */
    public Throwable getError() {
        return error;
    }
    
    /**
     * Returns object provided by event and casted to choosen type or null if 
     * data object was not specified.
     * 
     * <p>
     * <b>Warning!</b><br>
     * This method uses unsafe casting for resources savings. Type cast checking is very<br>
     * slow and rich operation. For this reason checking assigned to you.<br>
     * If you don't know type of data, using {@link Object} is recommended.
     *  
     * @return object provided by event and casted to choosen type or null
     */
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T)data;
    }
}
