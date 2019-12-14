package org.cuba.sql.common;

import java.util.ArrayList;
import java.util.List;

import org.cuba.utils.SqlUtils;

public class Where<E extends Expression> implements Expression {
    private E parent;
    private boolean appendWhere;
    private Item item;
    private List<Item> items;
    
    public Where(E parent, boolean appendWhere) {
        this.parent = parent;
        this.appendWhere = appendWhere;
        items = new ArrayList<>(3);
    }
    
    public Where() {
        this(null, true);
    }
    
    public Condition<Where<E>> column(String name) {
        SqlUtils.checkColumnName(name);
        checkItem();        
        item = new Item();
        return item.condition = new Condition<>(this, name);
    }
    
    public Condition<Where<E>> column(String table, String name) {
        SqlUtils.checkTableName(table);
        SqlUtils.checkColumnName(name);
        checkItem();        
        item = new Item();
        return item.condition = new Condition<>(this, table + "." + name);
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
    
    public Where<Where<E>> begin() {
        checkItem();
        item = new Item();
        item.where = new Where<Where<E>>(this, false);
        return item.where;
    }
    
    public E end() {
        return parent;
    }    

    @Override
    public CharSequence build() {
        if(item != null) {
            commit();
        }

        if(isEmpty()) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder();
        
        if(appendWhere) {
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

    @Override
    public boolean isEmpty() {
        if(item != null && item.condition.second != null) {
            commit();
        }
        return items.isEmpty();
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
