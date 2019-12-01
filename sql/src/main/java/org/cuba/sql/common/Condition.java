package org.cuba.sql.common;

import java.util.StringJoiner;

import org.cuba.utils.SqlUtils;

public class Condition<E extends Expression> implements Expression {
    protected E parent;
    protected String first;
    protected String operation;
    protected String second;
    protected boolean negative;
    protected boolean brackets;

    public Condition(E parent) {     
        this.parent = parent;
    }
    
    public Condition(E parent, String first) {
        SqlUtils.checkColumnName(first);      
        this.parent = parent;
        this.first = first;
    }

    public Condition<E> not() {
        if(operation != null) {
            throw new IllegalStateException("Operation already specified");
        }
        
        negative = true;
        return this;
    }
    
    public Condition<E> equals() {
        return op("=");
    }
    
    public Condition<E> like() {
        return op(" LIKE ");
    }
    
    public Condition<E> less() {
        return op("<");
    }
    
    public Condition<E> lessOrEquals() {
        return op("<=");
    }
    
    public Condition<E> more() {
        return op(">");
    }
    
    public Condition<E> moreOrEquals() {
        return op(">=");
    }
    
    public E between(Object min, Object max) {
        if(min == null) {
            throw new NullPointerException("Min is null");
        }        
        if(max == null) {
            throw new NullPointerException("Max is null");
        }
        
        op(" BETWEEN ");
        
        second = String.format("%s AND %s", String.valueOf(min), String.valueOf(max));  
        brackets = true;
        return parent;
    }
    
    public E in(Object... values) {
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
        
        second = String.format("(%s)", joiner);
        return parent;
    }
    
    public Condition<E> operation(String operation) {
        if(operation == null) {
            throw new NullPointerException("Operation is null");
        }
        if(operation.isEmpty()) {
            throw new IllegalArgumentException("Operation is empty");
        }
        brackets = true;
        return op(operation);
    }
    
    private Condition<E> op(String o) {
        if(first == null) {
            throw new IllegalStateException("Column does not specified");
        }
        if(operation != null) {
            throw new IllegalStateException("Operation already specified");
        }
        operation = o;
        return this;
    }
    
    public E value(Object value) {
        if(operation == null) {
            throw new IllegalStateException("Operation does not specified");
        }
        if(second != null) {
            throw new IllegalStateException("Value already specified");
        }
        
        second = String.valueOf(value);
        return parent;
    }
    
    public E column(String name) {
        column(null, name, false);
        return parent;
    }
    
    public E column(String table, String name) {
        column(table, name, true);
        return parent;
    }
    
    @Override
    public CharSequence build() {
        if(operation == null) {
            throw new IllegalStateException("Operation does not specified");
        }
        if(second == null) {
            throw new IllegalStateException("Value does not specified");
        }
        
        StringBuilder builder = new StringBuilder();
        if(negative) {
            builder.append("NOT");
        }
        
        if(negative || brackets) {
            builder.append("(");
        }
        
        builder.append(first)
               .append(operation)
               .append(second);
        
        if(negative || brackets) {
            builder.append(")");
        }
        return builder;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
    
    public boolean isCompleted() {
        return operation != null && second != null;
    }
    
    protected void column(String table, String column, boolean hasTable) {
        if(operation == null) {
            throw new IllegalStateException("Operation does not specified");
        }
        if(second != null) {
            throw new IllegalStateException("Value already specified");
        }
        
        if(hasTable) {
            SqlUtils.checkTableName(table);
            SqlUtils.checkColumnName(column);
            second = table + "." + column;
        } else {
            SqlUtils.checkColumnName(column);
            second = column;
        }
    }
}
