package org.cuba.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Where<E extends Expression> implements Expression {
    private E parent;
    private Item item;
    private List<Item> items;
    
    public Where(E parent) {
        this.parent = parent;
        items = new ArrayList<>(3);
    }
    
    public Where() {
        this(null);
    }
    
    public Where<E> column(String name) {
        if(item != null) {
            throw new IllegalStateException("Column already specified");
        }
        item = new Item();
        item.first = name;
        return this;
    }
    
    public Where<E> equals() {
        return op("=");
    }
    
    public Where<E> like() {
        return op(" LIKE ");
    }
    
    public Where<E> less() {
        return op("<");
    }
    
    public Where<E> lessOrEquals() {
        return op("<=");
    }
    
    public Where<E> moreOrEquals() {
        return op(">=");
    }
    
    public Where<E> more() {
        return op(">");
    }
    
    public Where<E> between(Object min, Object max) {
        op(" BETWEEN ");
        item.second = String.format("%s AND %s", String.valueOf(min), String.valueOf(max));      
        return this;
    }
    
    public Where<E> in(Object... values) {
        if(values == null) {
            throw new NullPointerException("Values is null");
        }
        if(values.length == 0) {
            throw new IllegalArgumentException("No values");
        }
        
        op(" IN ");
        
        StringJoiner joiner = new StringJoiner(", ");
        for(Object o : values) {
            joiner.add(String.valueOf(o));
        }
        
        item.second = String.format("(%s)", joiner);
        return this;
    }
    
    public Where<E> operation(String operation) {
        if(operation == null) {
            throw new NullPointerException("Operation is null");
        }
        return op(operation);
    }
    
    private Where<E> op(String o) {
        checkState();
        item.operation = o;
        return this;
    }
    
    public Where<E> value(Object value) {
        checkState();
        
        if(item.second != null) {
            throw new IllegalStateException("Value already specified");
        }
        
        item.second = String.valueOf(value);
        return this;
    }
    
    public Where<E> and() {
        checkState();
        item.link = "AND";
        commit();
        return this;
    }
    
    public Where<E> or() {
        checkState();
        item.link = "OR";
        commit();
        return this;
    }

    @Override
    public CharSequence build() {
        if(item != null) {
            commit();
        }
        
        if(items.isEmpty()) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder();
        
        if(!(parent instanceof Where)) {
            builder.append("WHERE ");
        }
        
        for(int i = 0; i < items.size() - 1; i++) {
            Item item = items.get(i);
            builder.append("(");
            
            if(item.where == null) {
               builder.append(item.first)
                      .append(item.operation)
                      .append(item.second);
            } else {
                builder.append(item.where.build());
            }
            
            builder.append(") ").append(item.link).append(" ");
        }
        
        Item last = items.get(items.size() - 1);
        builder.append("(")
               .append(last.first)
               .append(last.operation)
               .append(last.second)
               .append(")");

        return builder;
    }
    
    public Where<Where<E>> begin() {
        if(item != null) {
            throw new IllegalStateException("Condition already opened");
        }
        
        item = new Item();
        item.where = new Where<Where<E>>(this);
        return item.where;
    }
    
    public E end() {
        return parent;
    }
    
    private void checkState() throws IllegalStateException {
        if(item == null) {
            throw new IllegalStateException("No opened condition");
        }
    }
    
    private void commit() {
        items.add(item);
        item = null;
    }

    private class Item {
        public String first;
        public String second;
        public String operation;
        public String link;
        public Where<Where<E>> where;
    }
}
