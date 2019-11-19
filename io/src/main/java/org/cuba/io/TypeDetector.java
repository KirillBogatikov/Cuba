package org.cuba.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.cuba.reflex.ParameterizedType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Detects mime-type of data or extension for file on the<br>
 * base of data signature - first few bytes. Typically to detect<br>
 * the type enough 4-6 bytes
 * <p>Count of supported file types: 337</p>
 *  
 * @author Kirill Bogatikov
 * @version 1.0
 */
public class TypeDetector {
    protected static ConcurrentHashMap<String, List<List<Byte>>> knownTypes;
    protected static FileNameMap fileNameMap;
    
    static {
        knownTypes = new ConcurrentHashMap<>();
        fileNameMap = URLConnection.getFileNameMap();
        
        try(InputStream stream = TypeDetector.class.getClassLoader().getResourceAsStream("known_types.json");
            Reader reader = new InputStreamReader(stream)) {
           
            GsonBuilder builder = new GsonBuilder();
            builder.setLenient();
            builder.registerTypeAdapter(Byte.class, new JsonDeserializer<Byte>() {
                @Override
                public Byte deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return Integer.valueOf(json.getAsString().replace("0x", ""), 16).byteValue();
                }
            });
            Gson gson = builder.create();
            
            Type signatureType = new ParameterizedType(null, List.class, Byte.class);
            Type signaturesListType = new ParameterizedType(null, List.class, signatureType);
            Type hashMapType = new ParameterizedType(null, ConcurrentHashMap.class, String.class, signaturesListType);
                       
            knownTypes = gson.fromJson(reader, hashMapType);
        } catch(IOException ioe) {
            System.err.println("Cannot load known_types.json. Caused by ");
            ioe.printStackTrace();
        }
    }
    
    /**
     * Do nothing
     */
    public TypeDetector() {
        
    }
    
    /**
     * Returns length of the shortest signature
     * 
     * @return length of the shortest signature
     */
    public int minSignatureLength() {
        int min = Integer.MAX_VALUE;
        for(String extension : knownTypes.keySet()) {
            for(List<Byte> signature : knownTypes.get(extension)) {
                if(signature.size() < min) {
                    min = signature.size();
                }
            }
        }
        return min;
    }

    /**
     * Returns length of the longest signature
     * 
     * @return length of the longest signature
     */
    public int maxSignatureLength() {
        int max = -Integer.MAX_VALUE;
        for(String extension : knownTypes.keySet()) {
            for(List<Byte> signature : knownTypes.get(extension)) {
                if(signature.size() > max) {
                    max = signature.size();
                }
            }
        }
        return max;
    }
    
    /**
     * Returns MIME-type for specified file name<br>
     * Returns null if MIME-type is unknown
     * 
     * @param fileName name of file with path to it, but including file extension
     * @return MIME-type for specified file name or null
     */
    public String mimeType(String fileName) {
        if(!fileName.contains(".")) {
            fileName = "file." + fileName;
        }
        return fileNameMap.getContentTypeFor(fileName);
    }
    
    /**
     * Returns MIME-type for data from specified bytes<br>
     * Returns null if MIME-type is unknown
     * 
     * @see TypeDetector#extension(byte[])
     * @see TypeDetector#mimeType(String)
     *     
     * @param array array of bytes whose content mime-type will be detected
     * @return MIME-type for data or null
     */
    public String mimeType(byte[] array) {
        return mimeType(extension(array));
    }
    
    /**
     * Returns MIME-type for data from specified stream<br>
     * Returns null if MIME-type is unknown
     * <p>This method uses {@link ForthBackInputStream} for reading data<br>
     * into stream buffer and work without loss on the part of caller and with better performance</p>
     * 
     * @see TypeDetector#extension(ForthBackInputStream)
     * @see TypeDetector#mimeType(String)
     * 
     * @param stream specified ForthBackInputStream stream
     * @return MIME-type for data or null
     * @throws IOException thrown if some error occured at reading
     */
    public String mimeType(ForthBackInputStream stream) throws IOException {
        return mimeType(extension(stream));
    }
    
    /**
     * Returns MIME-type for data stored in specified file<br>
     * Returns null if MIME-type is unknown
     * <p>This method guarantees greater accuracy than {@link #mimeType(String)}<br>
     * because {@link #mimeType(String)} uses the file extension and this method uses file contents</p>
     * 
     * @see TypeDetector#extension(File)
     * @see TypeDetector#mimeType(String)
     * 
     * @param file specified file, must be exists and readable
     * @return MIME-type for data or null
     * @throws IOException thrown if some error occured at reading
     */
    public String mimeType(File file) throws IOException {
        return mimeType(extension(file));
    }
    
    /**
     * Returns file extension for data from specified byte array<br>
     * or null if type is unknown, e. g. "mp3" for [0x49, 0x44, 0x33, ...]
     * 
     * @param array specified bytes array
     * @return file extension for data or null
     */
    public String extension(byte[] array) {
        if(array == null) {
            throw new NullPointerException("Byte array is null");
        }
        
        int lastMatchLength = 0;
        String lastMatchExtension = null;
        for(String extension : knownTypes.keySet()) {
            for(List<Byte> signature : knownTypes.get(extension)) {
                int length = matchLength(signature, array);
                if(length >= lastMatchLength) {
                    lastMatchLength = length;
                    lastMatchExtension = extension;
                }
            }
        }
        
        return lastMatchExtension;
    }
    
    /**
     * Returns file extension for data from specified {@link ForthBackInputStream} stream<br>
     * or null if type is unknown, e. g. "mp3" for stream's data [0x49, 0x44, 0x33, ...]
     * 
     * @param stream specified stream
     * @return file extension for data or null if it unknown
     * @throws IOException if an error occured at reading data 
     */
    public String extension(ForthBackInputStream stream) throws IOException {
        int max = maxSignatureLength();
        stream.mark(max);
        
        byte[] array = new byte[max];
        stream.read(array);
        stream.reset();
        
        return extension(array);
    }
    
    /**
     * Returns file extension for data from specified file<br>
     * or null if type is unknown, e. g. "mp3" for file's data [0x49, 0x44, 0x33, ...]
     * 
     * @param file specified file, must be exists and readable
     * @return file extension for data or null if it unknown
     * @throws IOException if an error occured at reading data 
     */
    public String extension(File file) throws IOException {
        try(FileInputStream stream = new FileInputStream(file)) {
            int max = maxSignatureLength();
            
            byte[] array = new byte[max];
            stream.read(array);
            
            return extension(array);
        }
    }
    
    /**
     * Returns count of bytes matching the value in two arrays<br>
     * The comparison is made to the end of the shortest of the<br>
     * two arrays using the method {@link Byte#compare(byte, byte)}
     * 
     * @param a first byte array
     * @param b second byte array
     * @return count of bytes matching the value
     */
    private int matchLength(List<Byte> a, byte[] b) {
        int length = Math.min(a.size(), b.length);
        for(int i = 0; i < length; i++) {
            if(Byte.compare(a.get(i), b[i]) != 0) {
                return i;
            }
        }
        return length;
    }
}
