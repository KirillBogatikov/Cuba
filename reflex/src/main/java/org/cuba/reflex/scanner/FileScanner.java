package org.cuba.reflex.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cuba.log.Log;

/**
 * Implementation of Scanner for FileSystem: directories with compiled classes
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class FileScanner implements Scanner {
    private static final String TAG = FileScanner.class.getSimpleName();
    
    private Log log;
    private File root;
    private List<Class<?>> preloaded;
    private List<String> indexedPaths;
    private Set<ClassLoader> loaders;
    private int pathIndex;
    
    public FileScanner(Log log) {
        this.log = log;
    }

    @Override
    public void use(Set<ClassLoader> loaders) {
        this.loaders = loaders;
        log.d(TAG, "Will be used " + loaders.size() + " class loaders: ", loaders);
    }
    
    @Override
    public void open(String path) throws IOException {
        root = new File(path);
        preloaded = new ArrayList<>();
        index(root, indexedPaths = new ArrayList<>());
        pathIndex = 0;
    }

    @Override
    public int preload(int count) throws IOException {
        int loaded = 0;
        
        while(pathIndex < indexedPaths.size() && loaded < count) {
            Class<?> clazz = load();
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
        if(pathIndex >= indexedPaths.size()) {
            return null;
        }
        
        Class<?> clazz = load();
        if(clazz == null) {
            return next();
        }
        return clazz;
    }

    @Override
    public void close() throws IOException {
        preloaded.clear();
        preloaded = null;
        
        indexedPaths.clear();
        indexedPaths = null;
        
        loaders = null;
    }
    
    @Override
    public boolean ready() {
        return loaders != null;
    }
    
    private void index(File parent, List<String> container) {
        log.d(TAG, "Indexing " + parent);
        File[] files = parent.listFiles();
        if(files == null || files.length == 0) {
            log.d(TAG, "No files");
            return;
        }
        
        for(File file : files) {
            if(file.isDirectory()) {
                index(file, container);
            } else {
                String name = file.getAbsolutePath();
                if(name.endsWith(".class") && !name.contains("$")) {
                    log.d(TAG, "Found bytecode file " + name);
                    container.add(name);
                }
            }
        }
    }
    
    private Class<?> load() {
        String path = indexedPaths.get(pathIndex++);
        path = path.replace(root.getAbsolutePath(), ""); //remove root directory path
        path = path.substring(1, path.length() - 6); //remove .class extension
        path = path.replaceAll("[/\\\\]", ".");
        
        
        Class<?> clazz = null;
        for(ClassLoader loader : loaders) {
            try {
                clazz = loader.loadClass(path);
                break;
            } catch(ClassNotFoundException e) {
                log.e(TAG, "Class not found", e);
            }
        }
        return clazz;
    }

    @Override
    public String toString() {
        return FileScanner.class.getSimpleName() + "[" + root + "]";
    }
}