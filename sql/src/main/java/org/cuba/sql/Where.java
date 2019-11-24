package org.cuba.sql;

import java.util.ArrayList;
import java.util.List;

import org.cuba.utils.SqlUtils;

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
    
    public Condition<Where<E>> column(String name) {
        SqlUtils.checkColumnName(name);
        checkItem();        
        item = new Item();
        return item.condition = new Condition<>(this, name);
    }
    
    public Where<E> and() {
        checkState();
        item.link = " AND ";
        commit();
        return this;
    }
    
    public Where<E> or() {
        checkState();
        item.link = " OR ";
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
        
        for(int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if(item.where == null) {
                builder.append(item.condition.build());
            } else {
                builder.append("(")
                       .append(item.where.build())
                       .append(")");
            }
            
            if(i != items.size() - 1) {
                builder.append(item.link);
            }
        }

        return builder;
    }
        
    public Where<Where<E>> begin() {
        checkItem();
        item = new Item();
        item.where = new Where<Where<E>>(this);
        return item.where;
    }
    
    public E end() {
        return parent;
    }
    
    private void checkItem() throws IllegalStateException {
        if(item != null) {
            if(item.condition.isCompleted() && item.link == null) {
                throw new IllegalStateException("Link AND/OR must be specified before begin new condition");
            }     
            throw new IllegalStateException("Condition already opened");
        }
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
        public Condition<Where<E>> condition;
        public String link;
        public Where<Where<E>> where;
    }
}
