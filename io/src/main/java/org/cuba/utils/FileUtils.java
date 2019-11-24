package org.cuba.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides methods similar to those of the java class.nio.Files,<br>
 * but using older tools such as InputStream, Outputstream File, etc.
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 */
public class FileUtils {
    /**
     * In modern systems used data storages with clasters of 4096 bytes
     */
    public static final int DEFAULT_BUFFER_SIZE = 4096; 
    /**
     * Larger buffer sizes result in unsafe memory usage. Class methods<br>
     * are not synchronized and can be called by different threads, resulting in memory overruns
     */
    public static final int MAX_BUFFER_SIZE = 65536;
    /**
     * Smaller buffer size results in lower performance
     */
    public static final int MIN_BUFFER_SIZE = 64;
    
    /**
     * Returns an array of bytes read from the selected file
     * <p>
     *     Calling of this method is similar to calling {@link #readAllBytes(File, int)}<br>
     *     method with {@link #DEFAULT_BUFFER_SIZE} argument 
     * </p>
     * 
     * @see #readAllBytes(File, int)
     * 
     * @param file specified exists file
     * @return an array of bytes read from the selected file
     * @throws IOException if an error occured at file reading
     */
    public static byte[] readAllBytes(File file) throws IOException {
        return readAllBytes(file, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Returns an array of bytes read from the selected file. Reading<br>
     * is performed using a buffer of the specified size
     * <p>
     *     Calling of this method is similar to calling {@link #readAllBytes(InputStream, int)}<br>
     *     method with created FileInputStream. But this method uses safety try-with-resources construction<br>
     *     and creates buffered stream
     * </p>
     * 
     * @see #readAllBytes(InputStream, int)
     * 
     * @param file specified exists file
     * @param bufferSize the size of the buffer used to read
     * @return an array of bytes read from the selected file
     * @throws IOException if an error occured at file reading
     */
    public static byte[] readAllBytes(File file, int bufferSize) throws IOException {
        try(FileInputStream fileStream = new FileInputStream(file);
            BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)) {
            
            return readAllBytes(bufferedStream, bufferSize);
        }
    }
    
    /**
     * Returns an array of bytes read from the given stream. Reading<br>
     * is performed using a buffer of the specified size
     * <p>
     *     This method provides decent safety: buffer size must be greater than<br>
     *     {@link #MIN_BUFFER_SIZE} and less than {@link #MAX_BUFFER_SIZE}. If one of <br>
     *     these conditions is not met an {@link IllegalArgumentException} will be thrown. <br>
     *     Also, stream must not be null. It will be checked defore resources allocated.<br>
     *     For storing read bytes used ByteArrayOutputStream, created in try-with-resources constrcution<br>
     *     It is neccessary to provide auto-close of stream is some exception thrown at reading
     * </p>
     * <p><b>Important!</b><br>This method <b>DOES NOT</b> close given stream after reading or when error occured</p>
     * 
     * @param inputStream given stream, must not be null
     * @param bufferSize the size of the buffer used to read 
     * @return an array of bytes read from the given stream
     * 
     * @throws IOException if an error occured at file reading
     * @throws IllegalArgumentException if <code>bufferSize</code> less than {@value #MIN_BUFFER_SIZE} or greater than {@value #MAX_BUFFER_SIZE}
     * @throws NullPointerException if <code>inputStream</code> is null
     */
    public static byte[] readAllBytes(InputStream inputStream, int bufferSize) throws IOException {
        if(bufferSize > MAX_BUFFER_SIZE) {
            throw new IllegalArgumentException("Size of buffer must be less than " + MAX_BUFFER_SIZE);
        }
        if(bufferSize < MIN_BUFFER_SIZE) {
            throw new IllegalArgumentException("Size of buffer must be greater than " + MIN_BUFFER_SIZE);
        }
        if(inputStream == null) {
            throw new NullPointerException("Stream is null");
        }
                         
        try(ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[bufferSize];
            int length;
            
            while((length = inputStream.read(buffer)) != -1) {
                byteArrayStream.write(buffer, 0, length);
            }
            
            return byteArrayStream.toByteArray();   
        }
    }
}
