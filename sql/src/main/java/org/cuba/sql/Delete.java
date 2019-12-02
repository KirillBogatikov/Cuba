package org.cuba.sql;

import org.cuba.sql.common.Expression;
import org.cuba.sql.common.Where;
import org.cuba.utils.SqlUtils;

public class Delete implements Expression {
    private String table;
    private Where<Delete> where;
    
    public Delete() {
        where = new Where<>(this, true);
    }

    public Delete from(String table) {
        SqlUtils.checkTableName(table);
        this.table = table;
        return this;
    }
    
    public Where<Delete> where() {
        return where;
    }
    
    @Override
    public CharSequence build() {
        if(table == null) {
            throw new IllegalStateException("Table name should be specified");
        }
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append(table);
        
        if(!where.isEmpty()) {
           builder.append(" ").append(where.build());
        }
        return builder;
    }

    @Override
    public boolean isEmpty() {
        return table == null;
    }
}
