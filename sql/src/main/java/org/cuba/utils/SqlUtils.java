package org.cuba.utils;

public class SqlUtils {

    public static void checkColumnName(String name) throws NullPointerException, IllegalArgumentException {
        if(name == null) {
            throw new NullPointerException("Column name is null");
        }
        if(name.isEmpty()) {
            throw new IllegalArgumentException("Column name is empty");
        }
        
        char first = name.charAt(0);
        if(Character.isDigit(first)) {
            throw new IllegalArgumentException("Column name can not starts with number");
        }
    }
    
    public static void checkTableName(String name) throws NullPointerException, IllegalArgumentException {
        if(name == null) {
            throw new NullPointerException("Table name is null");
        }
        if(name.isEmpty()) {
            throw new IllegalArgumentException("Table name is empty");
        }
        
        char first = name.charAt(0);
        if(Character.isDigit(first)) {
            throw new IllegalArgumentException("Table name can not starts with number");
        }
    }
}
