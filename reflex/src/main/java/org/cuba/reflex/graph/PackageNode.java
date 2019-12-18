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
 * @author Kirill Bogatikov
 * @version 1.0
 * @since 1.1.0
 */
public class PackageNode {
    private String name;
    private Map<String, PackageNode> nodes;
    private List<Class<?>> types;
    
    public PackageNode(String name) {
        this.name = name;
        this.nodes = new HashMap<>();
        this.types = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public PackageNode addPackage(String name) {
        PackageNode node = new PackageNode(name);
        nodes.put(name, node);
        return node;
    }
    
    public Collection<PackageNode> getPackages() {
        return nodes.values();
    }
    
    public PackageNode getPackage(String name) {
        return nodes.get(name);
    }
    
    public void addType(Class<?> type) {
        types.add(type);
    }
    
    public List<Class<?>> getTypes() {
        return types;
    }
    
    @Override
    public String toString() {
        return name + " -> " + types;
    }
}
