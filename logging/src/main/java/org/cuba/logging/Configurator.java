package org.cuba.logging;

import static org.cuba.logging.Log.Level.DEBUG;
import static org.cuba.logging.Log.Level.ERROR;
import static org.cuba.logging.Log.Level.INFO;
import static org.cuba.logging.Log.Level.WARN;

import java.io.PrintStream;

public class Configurator {
    private ConfigurationImpl conf;
    
    public Configurator() {
        conf = new ConfigurationImpl();
    }
    
    private void checkPrintStream(PrintStream stream, Log.Level level) {
        if(stream == null) {
            throw new NullPointerException(level + " Print stream is null");
        }
    }
    
    public Configurator info(PrintStream info) {
        checkPrintStream(info, INFO);
        conf.info = info;
        return this;
    }
    
    public Configurator debug(PrintStream debug) {
        checkPrintStream(debug, DEBUG);
        conf.debug = debug;
        return this;
    }
    
    public Configurator warn(PrintStream warn) {
        checkPrintStream(warn, WARN);
        conf.warn = warn;
        return this;
    }
    
    public Configurator error(PrintStream error) {
        checkPrintStream(error, ERROR);
        conf.error = error;
        return this;
    }
    
    public Configurator pattern(String pattern) {
        if(pattern == null) {
            throw new NullPointerException("Pattern is null");
        }
        if(pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern can not be empty");
        }
        conf.pattern = pattern;
        return this;
    }
    
    public Configurator dateFormat(String dateFormat) {
        if(dateFormat == null) {
            throw new NullPointerException("Date format is null");
        }
        if(dateFormat.isEmpty()) {
            throw new IllegalArgumentException("Date format can not be empty");
        }
        conf.dateFormat = dateFormat;
        return this;
    }
    
    public Configuration build() {
        return conf;
    }
    
    private class ConfigurationImpl implements Configuration, Cloneable {
        private PrintStream info, error, warn, debug;
        private String pattern, dateFormat;
        
        @Override
        public PrintStream info() { return info; }

        @Override
        public PrintStream error() { return error; }

        @Override
        public PrintStream warn() { return warn; }

        @Override
        public PrintStream debug() { return debug; }

        @Override
        public String pattern() { return pattern; }

        @Override
        public String dateFormat() { return dateFormat; }

        @Override
        public void dispose() {
            info.close();
            info = null;
            
            error.close();
            error = null;
            
            warn.close();
            warn = null;
            
            debug.close();
            debug = null;
        }       
    }
}
