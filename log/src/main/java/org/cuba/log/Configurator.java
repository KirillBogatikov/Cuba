package org.cuba.log;

import static org.cuba.log.Level.DEBUG;
import static org.cuba.log.Level.ERROR;
import static org.cuba.log.Level.INFO;
import static org.cuba.log.Level.WARN;

import org.cuba.log.stream.LogPrintStream;
import org.cuba.log.stream.LogStream;

/**
 * Provides access to build {@link Configuration} step-by-step.
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0.0
 */
public class Configurator {
    /**
     * Returns {@link Configurator} with pre-built {@link LogPrintStream LogPrintStreams}
     * based on {@link System#out} and {@link System#err}.
     * <p>
     * <ul>
     *     <li>{@link Level#INFO} and {@link Level#DEBUG} -> {@link System#out}</li>
     *     <li>{@link Level#WARN} and {@link Level#ERROR} -> {@link System#err}</li>
     * </ul>
     * 
     * @return {@link Configurator} with pre-built {@link LogPrintStream LogPrintStreams} based on {@link System#out} and {@link System#err}
     */
    public static final Configurator system() {
        Configurator conf = new Configurator();
        return conf.info(new LogPrintStream(System.out))
                   .debug(new LogPrintStream(System.out))
                   .warn(new LogPrintStream(System.err))
                   .error(new LogPrintStream(System.err));
    }
    
    private ConfigurationImpl conf;
    
    public Configurator() {
        conf = new ConfigurationImpl();
    }
    
    private void checkStream(LogStream stream, Level level) {
        if(stream == null) {
            throw new NullPointerException("Stream for " + level + " is null");
        }
    }

    /**
     * Specifies {@link LogStream} for {@link Level#INFO} log level.
     * 
     * @throws NullPointerException if specified stream is null
     * @param info stream
     * @return pointer to this instance
     */
    public Configurator info(LogStream info) {
        checkStream(info, INFO);
        conf.info = info;
        return this;
    }

    /**
     * Specifies {@link LogStream} for {@link Level#DEBUG} log level.
     * 
     * @throws NullPointerException if specified stream is null
     * @param debug stream
     * @return pointer to this instance
     */
    public Configurator debug(LogStream debug) {
        checkStream(debug, DEBUG);
        conf.debug = debug;
        return this;
    }

    /**
     * Specifies {@link LogStream} for {@link Level#WARN} log level.
     * 
     * @throws NullPointerException if specified stream is null
     * @param warn stream
     * @return pointer to this instance
     */
    public Configurator warn(LogStream warn) {
        checkStream(warn, WARN);
        conf.warn = warn;
        return this;
    }
    
    /**
     * Specifies {@link LogStream} for {@link Level#ERROR} log level.
     * 
     * @throws NullPointerException if specified stream is null
     * @param error stream
     * @return pointer to this instance
     */
    public Configurator error(LogStream error) {
        checkStream(error, ERROR);
        conf.error = error;
        return this;
    }
    
    /**
     * Returns {@link Configuration} instance which provides
     * specified {@link LogStream LogStreams}
     * 
     * @return {@link Configuration} instance
     */
    public Configuration build() {
        checkStream(conf.info, INFO);
        checkStream(conf.debug, DEBUG);
        checkStream(conf.warn, WARN);
        checkStream(conf.error, ERROR);
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
