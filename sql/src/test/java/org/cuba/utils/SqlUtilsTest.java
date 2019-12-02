package org.cuba.utils;

import java.util.HashMap;

import org.junit.Test;

public class SqlUtilsTest {
    
    @Test(timeout = 15L, expected = NullPointerException.class)
    public void columnNameNull() {
        SqlUtils.checkColumnName(null);
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void columnNameEmpty() {
        SqlUtils.checkColumnName("");
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void columnNameIncorrect() {
        SqlUtils.checkColumnName("1column");
    }

    @Test(timeout = 15L, expected = NullPointerException.class)
    public void tableNameNull() {
        SqlUtils.checkTableName(null);
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void tableNameEmpty() {
        SqlUtils.checkTableName("");
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void tableNameIncorrect() {
        SqlUtils.checkTableName("1column");
    }
    
    @Test(timeout = 15L, expected = IllegalStateException.class)
    public void writeExpressionNoTable() {
        SqlUtils.checkWriteExpression(null, new HashMap<>());
    }
    
    @Test(timeout = 15L, expected = IllegalStateException.class)
    public void writeExpressionNoValues() {
        SqlUtils.checkWriteExpression("table", new HashMap<>());
    }
}
