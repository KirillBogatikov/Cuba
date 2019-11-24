package org.cuba.sql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.cuba.utils.SqlUtils;

public class Insert implements Expression {
    private String table;
    private Map<String, String> values;
    
    public Insert() {
        values = new LinkedHashMap<>();
    }
    
    public Insert into(String table) {
        SqlUtils.checkTableName(table);
        if(this.table != null) {
            throw new IllegalStateException("Table already specified");
        }
        this.table = table;
        return this;
    }
    
    public Insert set(String column, Object value) {
        SqlUtils.checkColumnName(column);
        if(values.containsKey(column)) {
            throw new IllegalStateException("Value for column '" + column + "' already specified");
        }
        
        values.put(column, String.valueOf(value));
        return this;
    }

    @Override
    public CharSequence build() {
        SqlUtils.checkWriteExpression(table, values);
        
        StringBuilder builder = new StringBuilder("INSERT INTO ");
        builder.append(table).append(" (");
        
        StringJoiner columnsJoiner = new StringJoiner(", ");
        StringJoiner valuesJoiner = new StringJoiner(", ");
        Set<String> columns = values.keySet();
        for(String column : columns) {
            columnsJoiner.add(column);
            valuesJoiner.add(values.get(column));
        }
        
        builder.append(columnsJoiner)
               .append(") VALUES (")
               .append(valuesJoiner)
               .append(")");
                
        return builder;
    }

}
