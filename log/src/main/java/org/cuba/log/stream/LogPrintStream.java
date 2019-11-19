package org.cuba.log.stream;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogPrintStream extends LogStream {
    public static final String PATTERN_PID_PARAM           = "%pid";
    public static final String PATTERN_TAG_PARAM           = "%tag";
    public static final String PATTERN_LEVEL_PARAM         = "%lvl";
    public static final String PATTERN_TIME_PARAM          = "%tim";
    public static final String PATTERN_MESSAGE_PARAM       = "%msg";
    public static final String PATTERN_DATA_PARAM          = "%dat";
    public static final String PATTERN_CALLER_CLASS_PARAM  = "%cls";
    public static final String PATTERN_CALLER_METHOD_PARAM = "%mtd";
    
    private PrintStream target;
    private String pattern;
    private SimpleDateFormat timeFormat;
    
    public LogPrintStream(PrintStream target) {
        if(target == null) {
            throw new NullPointerException("Target print stream is null");
        }
        
        this.target = target;
        this.pattern = "%lvl %tim %pid [%tag]: %msg%dat";
        setTimeFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        if(target.checkError()) {
            throw new IllegalStateException("Target print stream is closed");
        }
    }
    
    public void setPattern(String pattern) {
        if(pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern can not be null or empty");
        }
        
        this.pattern = pattern;
    }
    
    public void setTimeFormat(String timeFormat) {
        if(timeFormat == null || timeFormat.isEmpty()) {
            throw new IllegalArgumentException("Format can not be null or empty");
        }
        
        this.timeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());
    }
    
    @Override
    public void write(LogRecord record) {
        String value = pattern.replaceAll(PATTERN_LEVEL_PARAM, String.valueOf(record.getLevel()));
        value = value.replaceAll(PATTERN_TAG_PARAM, String.valueOf(record.getTag()));
        value = value.replaceAll(PATTERN_PID_PARAM, String.valueOf(record.getProcessId()));
        value = value.replaceAll(PATTERN_MESSAGE_PARAM, String.valueOf(record.getMessage()));
        
        if(record.getError() != null) {
            record.getError().printStackTrace(target);
        } else {
            value = value.replaceAll(PATTERN_DATA_PARAM, "\n" + String.valueOf(record.getData()));    
        }
        
        value = value.replaceAll(PATTERN_TIME_PARAM, timeFormat.format(new Date(record.getTime())));  
        
        StackTraceElement element = record.getCallerInfo();
        value = value.replaceAll(PATTERN_CALLER_CLASS_PARAM, element.getClassName());
        value = value.replaceAll(PATTERN_CALLER_METHOD_PARAM, element.getMethodName());  
        
        target.println(value);
        target.flush();
    }

    @Override
    public void close() throws Exception {
        target.close();
    }
}
