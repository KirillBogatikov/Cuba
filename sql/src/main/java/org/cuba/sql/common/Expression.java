package org.cuba.sql.common;

public interface Expression {
    public CharSequence build();
    public boolean isEmpty();
}
