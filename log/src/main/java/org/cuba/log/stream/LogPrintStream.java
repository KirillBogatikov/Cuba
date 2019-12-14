package org.cuba.log.stream;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of {@link LogStream} for simple wrapping {@link PrintStream}.
 * <p>
 * Writes log record into new line of specified {@link PrintStream}.<br>
 * So, you can change printing format by specifying own implementation of Formatter.
 * 
 * @author Kirill Bogatikov
 * @version 1.1
 * @since 1.1.0
 */
public class LogPrintStream extends LogStream {
    private PrintStream target;
    private Formatter formatter;
    
    public LogPrintStream(PrintStream target, Formatter formatter) {
        if(target == null) {
            throw new NullPointerException("Target print stream is null");
        }
        if(target.checkError()) {
            throw new IllegalStateException("Target print stream is closed");
        }
        
        this.target = target;
        setFormatter(formatter);
    }
    
    public LogPrintStream(PrintStream target) {
        this(target, null);
    }
    
    /**
     * Applies specified formatter or use default implementation.
     * <p>
     * If <code>formatter</code> is null, this method replaces<br>
     * old formatter by default implementation.<br>
     * Otherwise use <code>formatter</code> in {@link #write(LogRecord)} method
     * 
     * @param formatter specified {@link Formatter} implementation
     */
    public void setFormatter(Formatter formatter) {
        synchronized(formatter) {
            if(formatter == null) {
                this.formatter = new FormatterImpl();
            } else {
                this.formatter = formatter;
            }
        }
    }
        
    @Override
    public void write(LogRecord record) {
        String value;
        try {
            synchronized(formatter) {
                value = formatter.format(record);
            }
        } catch(Throwable t) {
            throw new RuntimeException("Formatting of LogRecord failed ", t);
        }
         
        target.println(value);
        Throwable throwable = record.getError();
        if(throwable != null) {
            throwable.printStackTrace(target);
        }
        target.flush();
    }

    /**
     * Closes specified {@link PrintStream}
     */
    @Override
    public void close() throws Exception {
        target.close();
    }
    
    /**
     * Allows you to get a string containing all the<br>
     * necessary information based on the record
     * 
     * @author Kirill Bogatikov
     * @version 1.0
     * @since 1.1
     */
    public static interface Formatter {
        /**
         * Returns string representation of specified record.
         * <p>
         * <b>Warning!</b>
         * This method shouldn't returns null. If this your implementation<br>
         * returns null, right behavior of LogPrintStream is not guaranteed.
         * <p>
         * Also, do not stringify {@link LogRecord#getError() log's error} in this method,<br>
         * because LogPrintStream already prints it into specified stream.
         * 
         * @param record log record, always has value and never is null
         * @return string representation of specified record.
         */
        public String format(LogRecord record);
    }
    
    private static class FormatterImpl implements Formatter {
        private SimpleDateFormat dateFormat;
        
        public FormatterImpl() {
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        }
        
        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            
            builder.append("[").append(record.getLevel()).append("]")
                   .append(" ").append(dateFormat.format(new Date(record.getTime()))).append(" ")
                   .append(" ").append(record.getProcessId()).append(" ")
                   .append("[").append(record.getTag()).append("]: ")
                   .append(record.getMessage());
            
            Object data = record.getData();
            if(data != null) {
                builder.append("\n").append(data);
            }
            
            data = record.getError();
            if(data != null) {
                builder.append("\nCaused by: ").append(data);
            }
            
            return builder.toString();
        }
        
    }
}
