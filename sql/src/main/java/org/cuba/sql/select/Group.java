package org.cuba.sql.select;

import java.util.ArrayList;
import java.util.List;

import org.cuba.sql.common.Expression;
import org.cuba.sql.common.Where;
import org.cuba.utils.SqlUtils;

public class Group implements Expression {
    private Select parent;
    private List<Item> columns;
    private Where<Group> having;

    public Group(Select parent) {
        this.parent = parent;
        columns = new ArrayList<>();
        having = new Where<>(this, false);
    }
    
    public Group by(String column) {
        byColumn(null, column, false);
        return this;
    }
    
    public Group by(String table, String... columns) {
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
    
    public Where<Group> having() {
        return having;
    }
    
    public Select end() {
        return parent;
    }
    
    @Override
    public CharSequence build() {
        if(isEmpty()) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder("GROUP BY ");
        for(int i = 0; i < columns.size(); i++) {
            Item item = columns.get(i);
            if(item.table != null) {
                builder.append(item.table).append(".");
            }
            builder.append(item.column);
            
            if(i < columns.size() - 1) {
                builder.append(", ");
            }
        }
        
        CharSequence havingClause = having.build();
        if(havingClause.length() > 0) {
            builder.append(" HAVING ").append(havingClause);
        }
        
        return builder;        
    }
    
    @Override
    public boolean isEmpty() {
        return columns.isEmpty();
    }
    
    private void byColumn(String table, String column, boolean hasTable) {
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

        columns.add(item);
    }

    private class Item {
        public String table;
        public String column;
        
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
