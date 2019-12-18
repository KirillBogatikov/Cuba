/**
 * 
 */
package org.cuba.reflex.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.0
 *
 */
public class Graph {
    private PackageNode root;
    
    public Graph() {
        this.root = new PackageNode("");
    }
    
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
        i(node, repository);        
        return repository;
    }
    
    private void i(PackageNode parent, List<Class<?>> repository) {
        for(PackageNode node : parent.getPackages()) {
            i(node, repository);
            repository.addAll(node.getTypes());
        }
    }
}
