package org.cuba.logging;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Log {
    public static enum Level {
        INFO, WARN, ERROR, DEBUG
    }
    
    public static final String PATTERN_PID_PARAM     = "%pid",
                               PATTERN_TAG_PARAM     = "%tag",
                               PATTERN_LEVEL_PARAM   = "%lvl",
                               PATTERN_DATE_PARAM    = "%dat",
                               PATTERN_MESSAGE_PARAM = "%msg";
    
    private static Integer PID = null;
    
    static {
        try {
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            String jvmName = bean.getName();
            PID = Integer.valueOf(jvmName.split("@")[0]);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    
    private static Log defaultLog;
    
    public static Log defaultLog() {
        if(defaultLog == null) {
            defaultLog = new Log(Configurator.defaultConfigurator().build(), Log.class.getName());
        }
        return defaultLog;
    }
    
    private Configuration config;
    private String defaultTag;
    private SimpleDateFormat simpleDateFormat;
    private Set<Level> muted;
    
    public Log(Configuration config, String defaultTag) {
        this.config = config;
        this.defaultTag = defaultTag;
        
        String dateFormat = config.dateFormat();
        if(dateFormat == null) {
            dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
        }
        this.simpleDateFormat = new SimpleDateFormat(dateFormat);
        this.muted = new HashSet<>();
    }
    
    private void check(PrintStream stream, Level level) {
        if(stream == null) {
            throw new NullPointerException(level + " Print stream is null");
        }
        if(stream.checkError()) {
            throw new IllegalStateException(level + " Print stream is closed");
        }
    }
    
    private String timestamp() {
        String dateFormat = config.dateFormat();
        if(dateFormat == null) {
            dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
        }
        simpleDateFormat.applyPattern(dateFormat);
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }
    
    private void log(PrintStream stream, String pattern, Level level, String tag, String message, Throwable t) {
        if(muted.contains(level)) { return; }
        check(stream, level);
        String value = pattern.replace(PATTERN_PID_PARAM, PID == null ? "<unknown>" : PID.toString())
                              .replace(PATTERN_TAG_PARAM, tag)
                              .replace(PATTERN_LEVEL_PARAM, level.toString())
                              .replace(PATTERN_DATE_PARAM, timestamp())
                              .replace(PATTERN_MESSAGE_PARAM, message);
        stream.println(value);
        if(t != null) {
            t.printStackTrace(stream);
        }
    }
    
    public void mute(Level level) {
        muted.add(level);
    }
    
    public void unmute(Level level) {
        muted.remove(level);
    }
    
    public void i(String message) {
        i(defaultTag, message);
    }
    
    public void i(String tag, String message) {
        i(tag, message, null);
    }
    
    public void i(String tag, String message, Throwable t) {
        log(config.info(), config.pattern(), Level.INFO, tag, message, t);
    }
    
    public void d(String message) {
        d(defaultTag, message);
    }
    
    public void d(String tag, String message) {
        d(tag, message, null);
    }
    
    public void d(String tag, String message, Throwable t) {
        log(config.debug(), config.pattern(), Level.DEBUG, tag, message, t);
    }
    
    public void e(String message) {
        e(defaultTag, message);
    }
    
    public void e(String tag, String message) {
        e(tag, message, null);
    }
    
    public void e(String tag, String message, Throwable t) {
        log(config.error(), config.pattern(), Level.ERROR, tag, message, t);
    }
    
    public void w(String message) {
        w(defaultTag, message);
    }
    
    public void w(String tag, String message) {
        w(tag, message, null);
    }
    
    public void w(String tag, String message, Throwable t) {
        log(config.warn(), config.pattern(), Level.WARN, tag, message, t);
    }
}
