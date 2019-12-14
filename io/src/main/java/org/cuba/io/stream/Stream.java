package org.cuba.io.stream;

import java.io.IOException;

public abstract class Stream {
    /**
     * Writes a sequece of bytes to some storage.<br>
     * The method does not guarantee to write all bytes from the array,<br>
     * so the number of successfully written bytes will be returned as a result.<br>
     * Calling this method is equivalent to calling next code:
     * <pre><code>
     *      stream.write(bytes, 0, bytes.length);
     * </code></pre>
     * 
     * @param bytes array to write
     * @return number of written bytes or -1 if EOF found
     * @throws IOException if some exception at writing occurred
     */
    public abstract int write(byte[] bytes) throws IOException;
    
    /**
     * Writes a sequece of bytes to some storage.<br>
     * The method does not guarantee to write all bytes from the array,<br>
     * so the number of successfully written bytes will be returned as a result.<br>
     * 
     * @param bytes array to write
     * @param offset array offset
     * @param limit max count of written bytes or -1 if EOF found
     * @throws IOException if some exception at writing occurred
     */
    public abstract int write(byte[] bytes, int offset, int limit) throws IOException;
    
    /**
     * Reads a sequece of bytes from some storage.<br>
     * The method does not guarantee to read all bytes to the array,<br>
     * so the number of successfully read bytes will be returned as a result.<br>
     * Calling this method is equivalent to calling next code:
     * <pre><code>
     *      stream.read(bytes, 0, bytes.length);
     * </code></pre>
     * 
     * @param bytes array to store read bytes
     * @return number of read bytes or -1 if EOF found
     * @throws IOException if some exception at writing occurred
     */
    public abstract int read(byte[] bytes) throws IOException;
    
    /**
     * Reads a sequece of bytes from some storage.<br>
     * The method does not guarantee to read all bytes to the array,<br>
     * so the number of successfully read bytes will be returned as a result.<br>
     * 
     * @param bytes array to store read bytes
     * @return number of read bytes
     * @throws IOException if some exception at writing occurred
     * 
     * @param bytes array to store read bytes
     * @param offset array offset
     * @param limit max count of read bytes
     * @return count of read bytes or -1 if EOF found
     * @throws IOException if some exception at writing occurred
     */
    public abstract int read(byte[] bytes, int offset, int limit) throws IOException;
}
