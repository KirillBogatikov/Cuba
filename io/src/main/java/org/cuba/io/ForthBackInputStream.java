package org.cuba.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Allows you to use part of the content from the parent<br>
 * stream multiple times without having to re-read it<br>
 * This Stream is one of the simplest markable stream implementation<br> 
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 */
public class ForthBackInputStream extends InputStream {
    private InputStream original;
    private byte[] buffer;
    private int bufferOffset = 0;
    
    /**
     * Wraps another InputStream to read data from it
     * 
     * @param original data source input stream
     */
    public ForthBackInputStream(InputStream original) {
        if(original == null) {
            throw new NullPointerException("Stream is null");
        }
        
        this.original = original;
    }
    
    /**
     * This method can return one byte from different sources:
     * <ul>
     *     <li>if stream does not marked or cleared, returns byte from original input stream</li>
     *     <li>if stream is marked, but buffer is empty, fills buffer and then read first byte from it</li>
     *     <li>if stream is marked and buffer already filled, returns next byte from buffer</li>
     * </ul>
     */
    @Override
    public int read() throws IOException {
        if(buffer == null) {
            return original.read();
        }
        if(bufferOffset >= buffer.length) {
            buffer = null;
            return original.read();
        }
        if(bufferOffset == -1) {
            int read = 0, count;
            byte[] part = new byte[4096];
            while((count = original.read(part, 0, buffer.length - read)) != -1 && read < buffer.length) {
                System.arraycopy(part, 0, buffer, read, count);
                read += count;
            }
            bufferOffset = 0;
        }
        return buffer[bufferOffset++];
    }

    /**
     * Prepares buffer of specified size and and indicates the reading<br>
     * method to the need to preserve the data in the buffer
     */
    @Override
    public void mark(int bufferSize) {
        clear();
        if(buffer == null) {
            buffer = new byte[bufferSize];
        } else if(bufferSize > buffer.length) {
            buffer = Arrays.copyOf(buffer, bufferSize);
        }
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void reset() {
        if(buffer == null) {
            throw new IllegalStateException("Not marked or cleared");
        }
        bufferOffset = 0;
    }
    
    /**
     * Clears buffer, remove it from memory and remove mark.<br>
     * After calling this method you can not use reset(), all read data will be<br>
     * read from source stream and not stored in buffer 
     */
    public void clear() {
        buffer = null;
        bufferOffset = -1;
    }
}
