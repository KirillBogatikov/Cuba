package org.cuba.sql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.cuba.utils.SqlUtils;

public class Update implements Expression {
    private String table;
    private Where<Update> where;
    private Map<String, String> values;

    public Update() {
        where = new Where<>(this);
        values = new LinkedHashMap<>();
    }
    
    public Update table(String table) {
        SqlUtils.checkTableName(table);
        this.table = table;
        return this;
    }
    
    public Update set(String name, Object value) {
        SqlUtils.checkColumnName(name);
        if(values.containsKey(name)) {
            throw new IllegalStateException("Value for column '" + name + "' already specified");
        }
        
        values.put(name, String.valueOf(value));
        return this;
    }

    public Where<Update> where() {
        return where;
    }

    @Override
    public CharSequence build() {
        SqlUtils.checkTableName(table);
        if(values.isEmpty()) {
            throw new IllegalStateException("No values to set");
        }
        
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append(table)
               .append(" SET ");
        
        StringJoiner joiner = new StringJoiner(", ");
        Set<String> columns = values.keySet();
        for(String column : columns) {
            joiner.add(String.format("%s=%s", column, values.get(column)));
        }
        
        builder.append(joiner);
        
        CharSequence whereClause = where.build();
        if(whereClause.length() > 0) {
            builder.append(" ").append(whereClause);
        }
        
        return builder;
    }
}
