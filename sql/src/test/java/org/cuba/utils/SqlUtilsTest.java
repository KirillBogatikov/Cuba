package org.cuba.utils;

import org.junit.Test;

public class SqlUtilsTest {
    
    @Test(timeout = 15L, expected = NullPointerException.class)
    public void testColumnNameNull() {
        SqlUtils.checkColumnName(null);
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testColumnNameEmpty() {
        SqlUtils.checkColumnName("");
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testColumnNameIncorrect() {
        SqlUtils.checkColumnName("1column");
    }

    @Test(timeout = 15L, expected = NullPointerException.class)
    public void testTableNameNull() {
        SqlUtils.checkTableName(null);
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testTableNameEmpty() {
        SqlUtils.checkTableName("");
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testTableNameIncorrect() {
        SqlUtils.checkTableName("1column");
    }
}
