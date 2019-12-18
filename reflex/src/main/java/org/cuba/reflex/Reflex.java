package org.cuba.reflex;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cuba.log.Log;
import org.cuba.reflex.scanner.FileScanner;
import org.cuba.reflex.scanner.JarScanner;
import org.cuba.reflex.scanner.Scanner;
import org.cuba.utils.TypeUtils;

public class Reflex {    
    public static String getPathToRtJar() {
        String home = System.getenv("JAVA_HOME");
        if(home == null) {
            home = System.getenv("JDK_HOME");
        }
        if(home == null) {
            return null;
        }
        
        File jar = new File(home, "lib/rt.jar");
        if(!jar.exists()) {
            jar = new File(home, "jre/lib/rt.jar");
        }
        if(!jar.exists()) {
            return null;
        }
        
        return jar.getAbsolutePath();
    }
    
    private static final String TAG = Reflex.class.getSimpleName();
    private static final Object LOCK = new Object();
    
    private static Depth repositoryDepth;
    private static List<Class<?>> repository;
    private static List<Integer> interfacesIndex;
    private static List<Integer> abstractIndex;
    private static Map<String, Integer> fullNameIndex;
    private static Map<String, List<Integer>> simpleNameIndex;
    private static Map<Class<? extends Annotation>, List<Integer>> annotatedIndex;
    private static Map<Class<?>, List<Integer>> childsIndex;
    
    static {
        repository = Collections.synchronizedList(new ArrayList<>());
        interfacesIndex = Collections.synchronizedList(new ArrayList<>());
        abstractIndex = Collections.synchronizedList(new ArrayList<>());
        
        fullNameIndex = new ConcurrentHashMap<>();
        simpleNameIndex = new ConcurrentHashMap<>();
        annotatedIndex = new ConcurrentHashMap<>();
        childsIndex = new ConcurrentHashMap<>();
    }

    private Depth depth;
    private Log log;
    private AtomicBoolean searchInProcess;
    private Set<ClassLoader> loaders;
    
    public Reflex(Depth depth, Log log) {        
        this.depth = depth;
        this.log = log;
        this.searchInProcess = new AtomicBoolean(false);
        
        loaders = new HashSet<>();
        loaders.add(Thread.currentThread().getContextClassLoader());
        loaders.add(Reflex.class.getClassLoader());
    }
    
    public void addLoader(ClassLoader loader) {
        if(loader == null) {
            throw new NullPointerException("Loader is null");
        }
        if(searchInProcess.get()) {
            throw new IllegalStateException("Search in process! Can not add loader now");
        }
        if(loaders.contains(loader)) {
            throw new IllegalArgumentException("Loader already added");
        }
        log.d(TAG, "Registering new ClassLoader");
        loaders.add(loader);
    }
    
    public void removeLoader(ClassLoader loader) {
        if(loader == null) {
            throw new NullPointerException("Loader is null");
        }
        if(searchInProcess.get()) {
            throw new IllegalStateException("Search in process! Can not add loader now");
        }
        if(!loaders.contains(loader)) {
            throw new IllegalArgumentException("Loader not found");
        }
        log.d(TAG, "Unregistering new ClassLoader");
        loaders.remove(loader);
    }
     
    public List<Class<?>> all() {
        findClasses();
        
        List<Class<?>> copy = new ArrayList<>();
        copy.addAll(repository);
        return copy;
    }
    
   public List<Class<?>> all(Comparator<Class<?>> comparator) {
       findClasses();
       
       List<Class<?>> copy = new ArrayList<>();
       copy.addAll(repository);
       copy.sort(comparator);
       return copy;
   }
    
    public List<Class<?>> annotatedBy(Class<? extends Annotation> annotation) {
        if(annotation == null) {
            throw new NullPointerException("Annotation class is null");
        }
        
        findClasses();
        
        List<Class<?>> classes = new ArrayList<>();
        List<Integer> indicies = annotatedIndex.get(annotation);
        if(indicies == null) {
            indicies = new ArrayList<>();
            for(int i = 0; i < repository.size(); i++) {
                Class<?> type = repository.get(i);
                
                if(type.equals(annotation)) {
                    continue;
                }
                
                if(type.isAnnotationPresent(annotation)) {
                    classes.add(type);
                    indicies.add(i);
                }
            }
            annotatedIndex.put(annotation, indicies);
        } else {
            for(int index : indicies) {
                classes.add(repository.get(index));
            }
        }
        
        return classes;
    }
    
    public List<Class<?>> childs(Class<?> type) {
        if(type == null) {
            throw new NullPointerException("Parent class is null");
        }
        
        findClasses();
                
        List<Class<?>> classes = new ArrayList<>();
        List<Integer> indicies = childsIndex.get(type);
        if(indicies == null) {
            indicies = new ArrayList<>();
            for(int i = 0; i < repository.size(); i++) {
                Class<?> clazz = repository.get(i);
                if(type.equals(clazz)) {
                    continue;
                }
                
                if(TypeUtils.isSubclass(clazz, type)) {
                    indicies.add(i);
                    classes.add(clazz);
                }
            }
            childsIndex.put(type, indicies);
        } else {
            for(int index : indicies) {
                classes.add(repository.get(index));
            }
        }
        return classes;
    }
    
    public Class<?> parent(Class<?> type) {
        if(type == null) {
            throw new NullPointerException("Child class is null");
        }
        
        findClasses();
        
        Set<Class<?>> classes = childsIndex.keySet();
        for(Class<?> clazz : classes) {
            if(TypeUtils.isSubclass(type, clazz) && !TypeUtils.hasModifier(clazz, Modifier.INTERFACE)) {
                return clazz;
            }
        }
               
        for(Class<?> clazz : repository) {
            if(type.equals(clazz)) {
                continue;
            }
            
            if(TypeUtils.isSubclass(type, clazz) && !TypeUtils.hasModifier(clazz, Modifier.INTERFACE)) {
                childs(clazz);
                return clazz;
            }
        }
        
        throw new RuntimeException("Parent of " + type + " is not found...");
    }
    
    public List<Class<?>> interfaces() {
        findClasses();
        
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(int i : interfacesIndex) {
            classes.add(repository.get(i));
        }
                
        return classes;
    }
    
    public List<Class<?>> abstractClasses() {
        findClasses();
        
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(int i : abstractIndex) {
            classes.add(repository.get(i));
        }
                
        return classes;
    }
    
    public void findClasses() {      
        synchronized(LOCK) {
            if(repositoryDepth == null) {
                log.d(TAG, "Reflex API is not indexed");
            } else if(depth.level > repositoryDepth.level) {
                log.d(TAG, "Indexed Reflex API less than depth of this Reflex instance");
            } else {
                log.d(TAG, "Reflex API is already fully indexed");
                return;
            }

            log.d(TAG, "Reflex API indexing in process");
            searchInProcess.set(true);
            
            long time = System.currentTimeMillis();            
            Set<String> paths = parseClasspath();
            
            clearIndicies();
            scanAll(paths);
            
            time = System.currentTimeMillis() - time;
            log.d(TAG, "Time spent: " + time + " ms");
            repositoryDepth = depth;
            searchInProcess.set(false);
        }
    }
    
    private void clearIndicies() {
        repository.clear();
        interfacesIndex.clear();
        abstractIndex.clear();
        fullNameIndex.clear();
        simpleNameIndex.clear();
        annotatedIndex.clear();
        childsIndex.clear();
    }
    
    private void scanAll(Set<String> classpath) {
        JarScanner jarScanner = new JarScanner(log);
        jarScanner.use(loaders);
        
        FileScanner fileScanner = new FileScanner(log);
        fileScanner.use(loaders);
        
        log.d(TAG, "Scanning in all URLs");
        for(String path : classpath) {
            log.d(TAG, "Scanning classes in", path);
            
            boolean system = path.contains("/lib/ext");
            if(system && depth.level < Depth.SYSTEM_LIBRARIES.level) {
                log.d(TAG, "Reflex has depth " + depth + ", system library will be ignored");
                continue;
            }
            
            Scanner scanner;
            if(path.endsWith(".jar")) {
                if(depth.level < Depth.DEPENDENCIES.level) {
                    log.d(TAG, "Reflex has depth " + depth + ", dependency will be ignored");
                    continue;
                }
                
                log.d(TAG, "JAR found, will be used JarScanner");
                scanner = jarScanner;
            } else {
                log.d(TAG, "File found, will be used FileScanner");
                scanner = fileScanner;
            }

            boolean failed = false;
            try {
                log.d(TAG, "Opening scanner");
                scanner.open(path);
                log.d(TAG, "Preloading classes");
                scanner.preload(500);
            } catch(IOException e) {
               log.e(TAG, "Failed to prepare scanner", e);
               failed = true;
            }
            
            if(!failed) {
                scan(scanner, system);
            }
        }
        
        log.i(TAG, "Total classes: " + repository.size());
    }
    
    private void scan(Scanner scanner, boolean system) {
        log.d(TAG, "Scanning", scanner);
        int count = 0;
        while(true) {
            try {
                Class<?> clazz = scanner.next();
                if(clazz == null) {
                    log.d(TAG, "Found " + count + " classes");
                    return;
                }

                indexClass(clazz);
                log.d(TAG, "Found " + clazz + ": ");
                count++;
            } catch (IOException e) {
                log.e(TAG, "Failed to scan", e);
            }
        }
    }
    
    private void indexClass(Class<?> clazz) {
        int index = repository.size();
        repository.add(clazz);
        if(TypeUtils.hasModifier(clazz, Modifier.INTERFACE)) {
            interfacesIndex.add(index);
        }
        if(TypeUtils.hasModifier(clazz, Modifier.ABSTRACT)) {
            abstractIndex.add(index);
        }
        fullNameIndex.put(clazz.getName(), index);
        addToIndex(simpleNameIndex, clazz.getSimpleName(), index);
    }
    
    private <K> void addToIndex(Map<K, List<Integer>> map, K key, int index) {
        List<Integer> list = map.get(key);
        if(list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        list.add(index);
    }
    
    private Set<String> parseClasspath() {
        log.d(TAG, "Parsing classpath");
        HashSet<String> classpath = new HashSet<>();
        for(ClassLoader loader : loaders) {
            log.d(TAG, "Searching paths in ", loader);
            while(loader != null) {
                if(loader instanceof URLClassLoader) {
                    URLClassLoader urlLoader = (URLClassLoader)loader;
                    log.d(TAG, "Found URLClassLoader parent: ", urlLoader);
                    for(URL url : urlLoader.getURLs()) {
                        classpath.add(url.toExternalForm().substring(6));
                    }
                }
                loader = loader.getParent();
            }
        }
        
        if(depth == Depth.JRE_CLASSES) {
            classpath.add(getPathToRtJar());
        }
        
        log.i(TAG, "Final classpath:", classpath);
        return classpath;
    }
    
    public static enum Depth {
        PROJECT          (1), 
        DEPENDENCIES     (2), 
        SYSTEM_LIBRARIES (3),
        JRE_CLASSES      (4);
        
        public final int level;
        
        private Depth(int level) {
            this.level = level;
        }
    }
}