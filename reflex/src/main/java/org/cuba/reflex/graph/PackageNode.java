/**
 * 
 */
package org.cuba.reflex.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents node of packages hierarchy graph
 * <p>Each node contains all types, stored in package
 * <p>For example:
 * <ul>
 *     <li>Package node for 'org' contains a few Package nodes: cuba, kllbff and other</li>
 *     <li>Package node for 'cuba' contains a few Package nodes: utils, exceptions, reflex, io, etc.</li>
 *     <li>Package node for 'reflex' contains a two Package nodes: graph and scanner and few types: ParameterizedType and Reflex</li>
 * </code></pre>
 * 
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class PackageNode {
    private String name;
    private Map<String, PackageNode> nodes;
    private List<Class<?>> types;
    
    /**
     * Creates node of specified package name
     * 
     * @param name name of package
     */
    public PackageNode(String name) {
        this.name = name;
        this.nodes = new HashMap<>();
        this.types = new ArrayList<>();
    }
    
    /**
     * Returns associated package name
     * 
     * @return package name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Creates new PackageNode of specified package name and
     * adds it into list of this node's children
     * 
     * @param name name of subpackage
     * @return PackageNode associated with specified package name
     */
    public PackageNode addPackage(String name) {
        PackageNode node = new PackageNode(name);
        nodes.put(name, node);
        return node;
    }
    
    /**
     * Returns collection of this node's children
     * 
     * @return collection of this node's children
     */
    public Collection<PackageNode> getPackages() {
        return nodes.values();
    }
    
    /**
     * Returns node associated with specified subpackage name
     * 
     * @param name name of subpackage
     * @return node associated with specified subpackage name
     */
    public PackageNode getPackage(String name) {
        return nodes.get(name);
    }
    
    /**
     * Adds type into list of this node's declared classes
     * 
     * @param type declared in this package class
     */
    public void addType(Class<?> type) {
        types.add(type);
    }
    
    /**
     * Returns list of declared in this package classes
     * 
     * @return list of declared in this package classes
     */
    public List<Class<?>> getTypes() {
        return types;
    }
    
    @Override
    public String toString() {
        return name + " -> " + types;
    }
}
