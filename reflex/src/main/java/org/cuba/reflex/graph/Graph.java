/**
 * 
 */
package org.cuba.reflex.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents graph structure for effective storing, processing and searching classes in complex<br>
 * packages hierarchy
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class Graph {
    private PackageNode root;
    
    /**
     * Creates empty graph
     */
    public Graph() {
        this.root = new PackageNode("");
    }
    
    /**
     * Fills this graph from specified classes repository
     * 
     * @param repository classes list for indexing into this graph
     */
    public void indexClasses(List<Class<?>> repository) {
        for(Class<?> type : repository) {
            String[] nameParts = type.getName().split("\\.");
            
            PackageNode typeNode = root;
            for(int i = 0; i < nameParts.length - 1; i++) {
                String pkg = nameParts[i];
                
                PackageNode node = typeNode.getPackage(pkg);
                if(node == null) {
                    node = typeNode.addPackage(pkg);
                }
                
                typeNode = node;
            }
            
            typeNode.addType(type);
        }
    }
    
    /**
     * Returns all classes declared in specified package 
     * <p>This method returns only classes contained in the<br>
     * specified package, but not in its subpackages
     * 
     * @param pkg package
     * @return list of classes declared in package
     */
    public List<Class<?>> inPackage(Package pkg) {
        String[] nameParts = pkg.getName().split("\\.");
        
        PackageNode node = root;
        for(String part : nameParts) {
            node = node.getPackage(part);
        }
        
        if(node == null) {
            throw new IllegalArgumentException(pkg + " not found in Graph");
        }
        return node.getTypes();
    }
    
    /**
     * Returns all classes declared in specified package and its subpackages
     * 
     * @param pkg package
     * @return list of classes declared in package and subpackages
     */
    public List<Class<?>> inPackages(Package pkg) {
        String[] nameParts = pkg.getName().split("\\.");
        
        PackageNode node = root;
        for(String part : nameParts) {
            node = node.getPackage(part);
        }
        
        if(node == null) {
            throw new IllegalArgumentException(pkg + " not found in Graph");
        }
        
        List<Class<?>> repository = new ArrayList<>();
        indexIntoNode(node, repository);        
        return repository;
    }
    
    private void indexIntoNode(PackageNode parent, List<Class<?>> repository) {
        for(PackageNode node : parent.getPackages()) {
            indexIntoNode(node, repository);
            repository.addAll(node.getTypes());
        }
    }
}
