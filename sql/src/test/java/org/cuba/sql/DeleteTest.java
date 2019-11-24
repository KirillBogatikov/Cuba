package org.cuba.sql;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeleteTest {

    @Test(timeout = 15L, expected = IllegalStateException.class)
    public void testNoTable() {
        Delete delete = new Delete();
        delete.build();
    }
    @Test(timeout = 15L, expected = NullPointerException.class)
    public void testTableNull() {
        Delete delete = new Delete();
        delete.from(null);
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testTableEmpty() {
        Delete delete = new Delete();
        delete.from("");
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testTableIncorrect() {
        Delete delete = new Delete();
        delete.from("1table");
    }
    
    @Test(timeout = 35L)
    public void testSimple() {
        Delete delete = new Delete();
        delete.from("table");
        
        assertEquals("delete from table", delete.build().toString().toLowerCase());
    }
    
    @Test(timeout = 35L)
    public void testComplex() {
        Delete delete = new Delete();
        delete.from("table")
              .where()
              .column("a").equals().value(11).and()
              .column("b").less().value(12);
        
        assertEquals("delete from table where a=11 and b<12", delete.build().toString().toLowerCase());
    }
}
