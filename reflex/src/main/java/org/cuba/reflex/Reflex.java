package org.cuba.reflex;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cuba.log.Log;
import org.cuba.reflex.scanner.FileScanner;
import org.cuba.reflex.scanner.JarScanner;
import org.cuba.reflex.scanner.Scanner;

public class Reflex {
    private static final String TAG = Reflex.class.getSimpleName();
    private Log log;
    private List<ClassLoader> loaders;
    private boolean agressive;
    
    public Reflex(boolean agressive, Log log) {
        this.log = log;
        
        loaders = new ArrayList<>();
        loaders.add(Thread.currentThread().getContextClassLoader());
        loaders.add(Reflex.class.getClassLoader());
        
        this.agressive = agressive;
    }
    
    public void addLoader(ClassLoader loader) {
        if(loader == null) {
            throw new NullPointerException("Loader is null");
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
        if(!loaders.contains(loader)) {
            throw new IllegalArgumentException("Loader not found");
        }
        log.d(TAG, "Unregistering new ClassLoader");
        loaders.remove(loader);
    }
    
    public List<Class<?>> annotatedBy(Class<? extends Annotation> annotation) {
        findClasses();
        
        List<Class<?>> list = new ArrayList<>();
        return list;
    }
    
    public void findClasses() {
        ClassStorage storage = ClassStorage.getInstance();
        
        if(storage.isRaw() || !storage.hasSystem() && agressive) {
            log.d(TAG, "Finding classes");
            long time = System.currentTimeMillis();
            Set<URL> urls = parseClasspath();
            scanAll(urls, storage);
            time = System.currentTimeMillis() - time;
            log.d(TAG, "Time spent: " + time + " ms");
        } else {
            log.d(TAG, "Classes already found");
        }
    }
    
    private void scanAll(Set<URL> urls, ClassStorage storage) {
        JarScanner jarScanner = new JarScanner();
        jarScanner.use(loaders);
        
        FileScanner fileScanner = new FileScanner(log);
        fileScanner.use(loaders);
        
        log.d(TAG, "Scanning in all URLs");
        for(URL url : urls) {
            log.d(TAG, "Scanning classes in", url);
            String classpath = url.toExternalForm().substring(6);
            log.d(TAG, "Cleared path: ", classpath);

            boolean system = classpath.contains("/lib/ext");
            if(!agressive && system) {
                log.d(TAG, "Reflex is not agressive, but found system library:", classpath);
                continue;
            }
            
            Scanner scanner;
            if(classpath.endsWith(".jar")) {
                log.d(TAG, "JAR found, will be used JarScanner");
                scanner = jarScanner;
            } else {
                log.d(TAG, "Directory found, will be used FileScanner");
                scanner = fileScanner;
            }

            try {
                log.d(TAG, "Opening class path");
                scanner.open(classpath);
                log.d(TAG, "Preloading 2500 classes");
                scanner.preload(2500);
            } catch(IOException e) {
               log.e(TAG, "Failed to prepare scanner", e);
            } finally {
                scan(scanner, storage, system);                
            }
        }
    }
    
    private void scan(Scanner scanner, ClassStorage storage, boolean system) {
        log.d(TAG, "Scanning", scanner);
        while(true) {
            try {
                Class<?> clazz = scanner.next();
                if(clazz == null) {
                    log.d(TAG, "All classes found");
                    return;
                }

                log.d(TAG, "Found " + clazz);
                storage.add(clazz, system);
            } catch (IOException e) {
                log.e(TAG, "Failed to scan", e);
            }
        }
    }
    
    private Set<URL> parseClasspath() {
        log.d(TAG, "Parsing classpath");
        Set<URL> urls = new HashSet<>();
        for(ClassLoader loader : loaders) {
            findURLs(loader, urls);
        }
        return urls;
    }
    
    private void findURLs(ClassLoader loader, Set<URL> urls) {
        log.d(TAG, "Searching URLs for ", loader);
        while(loader != null) {
            if(loader instanceof URLClassLoader) {
                URLClassLoader urlLoader = (URLClassLoader)loader;
                log.d(TAG, "Found URLClassLoader parent: ", urlLoader);
                List<URL> list = Arrays.asList(urlLoader.getURLs());
                log.d(TAG, "Found URLs: ", list);
                urls.addAll(list);
            }
            loader = loader.getParent();
        }
    }
}