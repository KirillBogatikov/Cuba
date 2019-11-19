package org.cuba.log;

import static org.cuba.log.Level.DEBUG;
import static org.cuba.log.Level.ERROR;
import static org.cuba.log.Level.INFO;
import static org.cuba.log.Level.WARN;

import org.cuba.log.stream.LogPrintStream;
import org.cuba.log.stream.LogStream;

public class Configurator {
    public static final Configurator defaultConfigurator() {
        Configurator conf = new Configurator();
        return conf.info(new LogPrintStream(System.out))
                   .warn(new LogPrintStream(System.err))
                   .error(new LogPrintStream(System.err))
                   .debug(new LogPrintStream(System.out));
    }
    
    private ConfigurationImpl conf;
    
    public Configurator() {
        conf = new ConfigurationImpl();
    }
    
    private void checkStream(Object stream, Level level) {
        if(stream == null) {
            throw new NullPointerException(level + " Stream is null");
        }
    }
    
    public Configurator info(LogStream info) {
        checkStream(info, INFO);
        conf.info = info;
        return this;
    }
    
    public Configurator debug(LogStream debug) {
        checkStream(debug, DEBUG);
        conf.debug = debug;
        return this;
    }
    
    public Configurator warn(LogStream warn) {
        checkStream(warn, WARN);
        conf.warn = warn;
        return this;
    }
    
    public Configurator error(LogStream error) {
        checkStream(error, ERROR);
        conf.error = error;
        return this;
    }
    
    public Configuration build() {
        return conf;
    }
    
    private class ConfigurationImpl implements Configuration, Cloneable {
        private LogStream info, error, warn, debug;
        
        @Override
        public LogStream info() { return info; }

        @Override
        public LogStream error() { return error; }

        @Override
        public LogStream warn() { return warn; }

        @Override
        public LogStream debug() { return debug; }

        @Override
        public void dispose() throws Exception {
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
