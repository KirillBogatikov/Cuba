package org.cuba.sql.select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.cuba.sql.common.Expression;
import org.cuba.sql.common.Where;
import org.cuba.utils.SqlUtils;

public class Select implements Expression {
    private Map<String, Columns> target;
    private Where<Select> where;
    private Group group;
    private Order order;
    private Map<String, Join> joins;
    private boolean fetchAll;
    private boolean distinct;
    private Integer offset, limit;

    public Select() {      
        target = new LinkedHashMap<>();
        joins = new LinkedHashMap<>();
        where = new Where<>(this, true);
        group = new Group(this);
        order = new Order(this);        
    }
    
    public Select distinct() {
        if(distinct) {
            throw new IllegalStateException("Select already is distinct");
        }
        distinct = true;
        return this;
    }
    
    public Select offset(int offset) {
        if(this.offset != null) {
            throw new IllegalStateException("Offset already specified");
        }
        this.offset = offset;
        return this;
    }
    
    public Select limit(int limit) {
        if(this.limit != null) {
            throw new IllegalStateException("Limit already specified");
        }
        this.limit = limit;
        return this;
    }
    
    public Select from(String table) {
        SqlUtils.checkTableName(table);
        Set<String> tables = target.keySet();
        if(tables.contains(table)) {
            throw new IllegalArgumentException("Table '" + table + "' already is specified");
        }
        target.put(table, new Columns());
        return this;
    }
    
    public Select from(String... tables) {
        switch(tables.length) {
            case 0: throw new IllegalArgumentException("No tables");
            case 1: return from(tables[0]);
            default: {
                for(String table : tables) {
                    from(table);
                }
            }
        }   
        return this;
    }
    
    public Select all(String table) {
        column(table, "*", false);  
        Columns cols = target.get(table);
        cols.all = true;
        return this;
    }
    
    public Select all() {
        checkFetchAll();
        checkAnyColumn();
        fetchAll = true;
        return this;
    }
    
    public Select column(String table, String column) {
        column(table, column, false);
        return this;
    }
    
    public Select columns(String table, String... columns) {
        switch(columns.length) {
            case 0: throw new IllegalArgumentException("No columns");
            case 1: column(table, columns[0], false); break;
            default: {
                for(String column : columns) {
                    column(table, column, false);
                }
            }
        }
        return this;
    }
    
    public Select column(String column) {
        column(null, column, true);
        return this;
    }
    
    public Where<Select> leftJoin(String table) {
        return join("LEFT", table);
    }
    
    public Where<Select> rightJoin(String table) {
        return join("RIGHT", table);
    }
    
    public Where<Select> fullJoin(String table) {
        return join("FULL", table);
    }
    
    public Where<Select> innerJoin(String table) {
        return join("INNER", table);
    }
    
    public Where<Select> where() {
        return where;
    }
    
    public Group group() {
        return group;
    }
    
    public Order order() {
        return order;
    }

    @Override
    public CharSequence build() {
        StringBuilder builder = new StringBuilder("SELECT ");
                
        if(distinct) {
            builder.append("DISTINCT ");
        }
        
        Set<String> tables = target.keySet();
        StringJoiner tablesJoiner = new StringJoiner(", ");
        for(String table : tables) {
            if(table == null) {
                continue;
            }
            tablesJoiner.add(table);
        }
                
        builder.append(buildColumns())
               .append(" FROM ")
               .append(tablesJoiner);
        
        if(!joins.isEmpty()) {
            builder.append(" ").append(buildJoins());
        }
        
        if(!where.isEmpty()) {
            builder.append(" ").append(where.build());
        }
        
        if(!group.isEmpty()) {
            builder.append(" ").append(group.build());
        }
        
        if(!order.isEmpty()) {
            builder.append(" ").append(order.build());
        }
        
        if(limit != null) {
            builder.append(" LIMIT ").append(limit);
        }
        if(offset != null) {
            builder.append(" OFFSET ").append(offset);
        }
        
        return builder;
    }
    
    @Override
    public boolean isEmpty() {
        return target.isEmpty();
    }
    
    private CharSequence buildJoins() {
        StringBuilder joinsBuilder = new StringBuilder();
        Set<String> tables = joins.keySet(); 
        for(String table : tables) {
            Join join = joins.get(table);
            joinsBuilder.append(join.type).append(" JOIN ")
                        .append(table).append(" ON ");
            
            CharSequence onClause = join.on.build();
            joinsBuilder.append(onClause);
        }
        return joinsBuilder;
    }
    
    private CharSequence buildColumns() {
        if(fetchAll) {
            return "*";
        }
        
        Set<String> tables = target.keySet();
        StringJoiner columnJoiner = new StringJoiner(", ");
        for(String table : tables) {
            Columns columns = target.get(table);
            
            if(columns.all) {
                columnJoiner.add((table == null ? "" : table + ".") + "*");
                continue;
            }
            
            for(String column : columns) {
                columnJoiner.add((table == null ? "" : table + ".") + column);
            }
        }
        
        return columnJoiner.toString();
    }
    
    private void checkFetchAll() {
        if(fetchAll) {
            throw new IllegalStateException("A selection of all columns from all tables already is specified");
        }
    }
    
    private void checkAnyColumn() {
        if(!target.isEmpty()) {
            Set<String> tables = target.keySet();
            for(String table : tables) {
                Columns columns = target.get(table);
                if(!columns.columns.isEmpty()) {
                    throw new IllegalStateException("Some columns selection already is specified");                    
                }
            }
        }
    }
    
    private void column(String table, String column, boolean noTable) {
        checkFetchAll();
        if(!noTable) {
            SqlUtils.checkTableName(table);
        }
        SqlUtils.checkColumnName(column);
        
        Columns columns = target.get(table);
        if(columns == null) {
            columns = new Columns();
            target.put(table, columns);
        }
        
        if(columns.all) {
            throw new IllegalStateException("A selection of all columns from '" + table + "' already is specified");
        }
        if(columns.has(column)) {
            throw new IllegalArgumentException("Column " + table + "." + column + " already is specified");
        }
        
        columns.add(column);
    }
    
    private Where<Select> join(String type, String table) {
        if(joins.containsKey(table)) {
            throw new IllegalArgumentException("Table '" + table + "' already joined");
        }
        Join join = new Join();
        joins.put(table, join);
        join.type = type;
        return join.on = new Where<>(this, false);
    }

    private class Columns implements Iterable<String> {
        public boolean all;
        public List<String> columns;
        
        public Columns() {
            this.columns = new ArrayList<>(3);
        }
        
        public boolean has(String column) {
            return columns.contains(column);
        }
        
        public void add(String column) {
            columns.add(column);
        }

        @Override
        public Iterator<String> iterator() {
            return columns.iterator();
        }
    }
    
    private class Join {
        public String type;
        public Where<Select> on;
    }
}
