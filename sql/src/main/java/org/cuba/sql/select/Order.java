package org.cuba.sql.select;

import java.util.ArrayList;
import java.util.List;

import org.cuba.sql.common.Expression;
import org.cuba.utils.SqlUtils;

public class Order implements Expression {
    private static final String DIRECTION_DESC = "DESC";
    public static final String DIRECTION_ASC = "ASC";
    private List<Item> columns;
    private Item column;
    private Select parent;

    public Order(Select parent) {
        columns = new ArrayList<>();
        this.parent = parent;
    }
    
    public Order by(String column) {
        byColumn(null, column, false);
        return this;
    }
    
    public Order by(String table, String... columns) {
        switch(columns.length) {
            case 0: {
                throw new IllegalArgumentException("No columns");
            } 
            case 1: {
                byColumn(table, columns[0], true);
            } break;
            default: {
                for(String column : columns) {
                    byColumn(table, column, true);
                }
            } break;
        }
        return this;
    }
        
    public Order asc() {
        checkState();
        direction(DIRECTION_ASC);
        commit();
        return this;
    }
    
    public Order desc() {
        checkState();
        direction(DIRECTION_DESC);
        commit();
        return this;
    }
    
    public Order ascending() {
        checkState();
        direction(DIRECTION_ASC);
        commit();
        return this;
    }
    
    public Order descending() {
        checkState();
        direction(DIRECTION_DESC);
        commit();
        return this;
    }
    
    public Order up() {
        checkState();
        direction(DIRECTION_ASC);
        commit();
        return this;
    }
    
    public Order down() {
        checkState();
        direction(DIRECTION_DESC);
        commit();
        return this;
    }
    
    public Order defaultTable(String table) {
        for(Item item : columns) {
            if(item.table == null) {
                item.table = table;
            }
        }
        return this;
    }

    public Select end() {
        return parent;
    }
    
    @Override
    public CharSequence build() {
        if(column != null) {
            commit();
        }
        
        if(isEmpty()) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder("ORDER BY ");
        for(int i = 0; i < columns.size(); i++) {
            Item item = columns.get(i);
            if(item.table != null) {
                builder.append(item.table).append(".");
            }
            builder.append(item.column);
            if(item.direction != null) {
                builder.append(" ").append(item.direction);
            }
            
            if(i < columns.size() - 1) {
                builder.append(", ");
            }
        }
        return builder;
    }

    @Override
    public boolean isEmpty() {
        return columns.isEmpty();
    }
    
    private void byColumn(String table, String column, boolean hasTable) {
        if(this.column != null) {
            commit();
        }
                
        if(hasTable) {
            SqlUtils.checkTableName(table);
        }
        SqlUtils.checkColumnName(column);        
        Item item = new Item(table, column);
        
        if(columns.contains(item)) {
            String message = "Column ";
            if(hasTable) {
                message += table + ".";
            }
            message += column + " already is specified";
            throw new IllegalArgumentException(message);
        }

        this.column = item;
    }
    
    private void checkState() {
        if(column == null) {
            throw new IllegalStateException("Column does not specified");
        }
    }
    
    private void direction(String direction) {
        column.direction = direction;
    }
    
    private void commit() {
        columns.add(column);
        column = null;
    }

    private class Item {
        public String table;
        public String column;
        public String direction;
        
        public Item(String table, String column) {
            this.table = table;
            this.column = column;
        }
        
        public boolean equals(Object o) {
            if(o != null && o instanceof Item) {
                Item item = (Item)o;
                
                boolean equals = true;
                if(table != null) {
                    equals = table.equals(item.table);
                }
                equals &= column.equals(item.column);
                
                return equals;
            }
            return false;
        }
    }
}
