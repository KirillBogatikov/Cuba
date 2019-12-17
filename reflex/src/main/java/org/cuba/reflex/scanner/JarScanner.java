package org.cuba.reflex.scanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.cuba.log.Log;

/**
 * Implements Scanner for loading classes from JAR file
 * 
 * @see JarInputStream
 * @see JarEntry
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class JarScanner implements Scanner {
    private static final String TAG = JarScanner.class.getSimpleName();
    
    private Log log;
    private String path;
    private JarInputStream jarStream;
    private List<Class<?>> preloaded;
    private Set<ClassLoader> loaders;

    public JarScanner(Log log) {
        this.log = log;
    }
    
    @Override
    public void use(Set<ClassLoader> loaders) {
        this.loaders = loaders;
        log.d(TAG, "Using loaders: ", loaders);
    }
    
    @Override
    public void open(String path) throws IOException {
        this.path = path;
        log.d(TAG, "Opening JAR: ", path);
        jarStream = new JarInputStream(new FileInputStream(path));
        preloaded = new ArrayList<>();
    }

    @Override
    public int preload(int count) throws IOException {
        int loaded = 0;
        JarEntry entry;
        while(loaded < count && (entry = jarStream.getNextJarEntry()) != null) {
            Class<?> clazz = loadClass(entry);
            if(clazz == null) {
                continue;
            }
            
            preloaded.add(clazz);
            loaded++;
        }
        return loaded;
    }

    @Override
    public Class<?> next() throws IOException {
        if(preloaded.size() > 0) {
            return preloaded.remove(0);
        }
        
        JarEntry entry = jarStream.getNextJarEntry();
        if(entry == null) {
            log.d(TAG, "No entries");
            return null;
        }
        
        Class<?> clazz = loadClass(entry);
        if(clazz == null) {
            log.d(TAG, "Trying parse next entry");
            return next();
        }
        return clazz;
    }

    @Override
    public void close() throws IOException {
        loaders = null;
        jarStream.close();
    }
    
    @Override
    public boolean ready() {
        return loaders != null;
    }

    private Class<?> loadClass(JarEntry entry) {
        if(entry.isDirectory()) {
            return null;
        }
        
        String name = entry.getName();
        if(!name.endsWith(".class") || name.contains("$")) {
            return null;
        }
        name = name.replaceAll("[/\\\\]", ".");
        name = name.substring(0, name.length() - 6);
        
        Class<?> clazz = null;
        for(ClassLoader loader : loaders) {
            try {
                clazz = loader.loadClass(name);
                break;
            } catch(ClassNotFoundException | NoClassDefFoundError e) {
                log.e(TAG, "Class not found", e);
            }
        }
        return clazz;
    }

    @Override
    public String toString() {
        return JarScanner.class.getSimpleName() + "[" + path + "]";
    }
}