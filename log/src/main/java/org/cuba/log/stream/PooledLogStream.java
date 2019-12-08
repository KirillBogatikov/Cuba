package org.cuba.log.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides Observable-styled interface to<br>
 * share {@link LogRecord} between a few {@link LogStream LogStreams}. 
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class PooledLogStream extends LogStream {
    private List<LogStream> subscribers;
    
    /**
     * Constructs new synchronized list for storing {@link LogStream stream} 
     */
    public PooledLogStream() {
        subscribers = Collections.synchronizedList(new ArrayList<>());
    }
    
    /**
     * Adds new {@link LogStream stream} into this pool or<br>
     * do nothing if it already added.
     * 
     * @param stream {@link LogStream LogStream} instance
     */
    public void register(LogStream stream) {
        if(subscribers.contains(stream)) {
            return;
        }
        subscribers.add(stream);
    }

    /**
     * Removes {@link LogStream stream} from this pool or<br>
     * do nothing if it does not added.
     * 
     * @param stream {@link LogStream LogStream} instance
     */
    public void unregister(LogStream stream) {
        subscribers.remove(stream);
    }

    /**
     * Closes all registered streams
     */
    @Override
    public void close() throws Exception {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        
        for(LogStream stream : subscribers) {
            try {
                stream.close();            
            } catch(Exception e) {
                count++;
                builder.append("\n").append(e);
            }
        }
        
        if(count > 0) {
            throw new RuntimeException("Occurred " + count + " exceptions: " + builder);
        }
    }

    @Override
    public void write(LogRecord record) {
        for(LogStream stream : subscribers) {
            stream.write(record);
        }
    }

}
