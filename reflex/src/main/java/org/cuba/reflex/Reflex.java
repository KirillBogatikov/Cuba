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

/**
 * <h3>ReFlex - Reflection flexible tool</h3>
 * <p>Reflex is big tool, allows you to retrieve more information about<br>
 *    classes, interfaces and other entities, available from your app by JVM classpath.
 * <p>So, Reflex can fairly rapid process all classes from JRE. On average, it takes<br>
 *    1 millisecond to process one class.
 * 
 * <h4>Data storage</h4>
 * <p>All instances of Reflex class uses commont static thread-safe blocking context.<br>
 *    Reflex stores all known classes in one big synchronized list called <i>"repository"</i>.<br>
 *    Also, for perfomance improvement each class indexed in a few indexes:
 *    <ul>
 *        <li>interfaces index - contains only interfaces,</li>
 *        <li>abstract index - contains only abstract classes,</li>
 *        <li>full name index compares each class with it's full name (for example, java.lang.Number - full name of {@link Number} class,</li>
 *        <li>simple name index compares a few classes with their simple names (for example, Number - simple name of {@link Number java.lang.Number} class,</li>
 *        <li>annotated index compares Annotation and classes, annotated with it,</li>
 *        <li>childs index compares class and it's childs</li>
 *    </ul>
 *    
 * <h4>Perfomance</h4> 
 * <p>So, every Reflex instance can has different {@link Depth} values. So, if depth of current instance is bigger than<br>
 *    context's depth, all classes will be reloaded by current instance. At the same time, access to context will be locked until<br>
 *    current instance doesn't release it after finishing classes loading and indexing.
 * 
 * <h4>Logs</h4>
 * <p>Work of Reflex tool is very complex and unstable. Therefore, it uses agressive logging on {@link org.cuba.log.Level#DEBUG}
 * <p>Warning! Count of log records is greater the deeper the search and the more classes are available 
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
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
    
    /**
     * Creates Reflex with specified depth and log instance
     * 
     * @param depth depth of classes search
     * @param log instance of {@link Log} used to logging events
     */
    public Reflex(Depth depth, Log log) {       
        if(depth == null) {
            throw new NullPointerException("Depth is null");
        }
        if(log == null) {
            throw new NullPointerException("Log is null");
        }
        
        this.depth = depth;
        this.log = log;
        this.searchInProcess = new AtomicBoolean(false);
        
        loaders = new HashSet<>();
        loaders.add(Thread.currentThread().getContextClassLoader());
        loaders.add(Reflex.class.getClassLoader());
    }
    
    /**
     * Adds new class loader into this reflex
     * 
     * @param loader new class loader
     */
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
    
    /**
     * Removes class loader from reflex 
     * 
     * @param loader early added class loader
     */
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
     
    /**
     * Returns all known classes
     * <p>If classes repository is empty or current depth bigger than context depth, starts classes search
     * 
     * @return list of all known classes
     */
    public List<Class<?>> all() {
        findClasses();
        
        List<Class<?>> copy = new ArrayList<>();
        copy.addAll(repository);
        return copy;
    }
    
    /**
     * Returns all known classes sorted with {@code comparator}
     * <p>If classes repository is empty or current depth bigger than context depth, starts classes search
     * 
     * @param comparator implementation of {@link Comparator}
     * @return list of all known classes sorted with {@code comparator}
     */
    public List<Class<?>> all(Comparator<Class<?>> comparator) {
         List<Class<?>> copy = all();
        copy.sort(comparator);
        return copy;
    }
    
    /**
     * Returns classes annotated by specified {@code annotation}
     * <p>If classes repository is empty or current depth bigger than context depth, starts classes search
     * 
     * @param annotation annotation class
     * @return list of classes annotated by specified {@code annotation}
     */
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
    
    /**
     * Returns classes which extends specified {@code type}
     * <p>If classes repository is empty or current depth bigger than context depth, starts classes search
     * 
     * @param type parent class
     * @return list of classes which extends specified {@code type}
     */
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
    
    /**
     * Returns parent class of specified {@code type} or null if it can not be found
     * <p>If classes repository is empty or current depth bigger than context depth, starts classes search
     * 
     * @param type child class
     * @return parent class of specified {@code type} or null if it can not be found
     */
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
        
        return null;
    }
    
    /**
     * Returns all interfaces known this Reflex
     * <p>If classes repository is empty or current depth bigger than context depth, starts classes search
     * 
     * @return list of all interfaces
     */
    public List<Class<?>> interfaces() {
        findClasses();
        
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(int i : interfacesIndex) {
            classes.add(repository.get(i));
        }
                
        return classes;
    }
    
    /**
     * Returns all abstract classes known this Reflex
     * <p>If classes repository is empty or current depth bigger than context depth, starts classes search
     * 
     * @return list of all abstract classes
     */
    public List<Class<?>> abstractClasses() {
        findClasses();
        
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(int i : abstractIndex) {
            classes.add(repository.get(i));
        }
                
        return classes;
    }
    
    /**
     * Locks static context and starts search.
     * <p>At first step, this method checks necessity of class search.<br>
     *    If repository depth is null - repository is raw and this instance is first, starts search<br>
     *    If repository depth is low than depth of this instance - repository is not full, starts search<br>
     *    otherwise - search ignored, context will be released.
     * <p>Next step - parsing class path. Class path items - classes directories, dependencies,<br>
     *    system libraries will be received from class loaders.
     * <p>Next step - clearing repository and indicies. Then, it starts scanning of each class path item<br>
     * <p>Final step - releasing locked context
     */
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