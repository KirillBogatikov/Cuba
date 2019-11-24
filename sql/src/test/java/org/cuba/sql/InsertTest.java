package org.cuba.sql;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InsertTest {

    @Test(timeout = 15L, expected = IllegalStateException.class)
    public void testNoTable() {
        Insert insert = new Insert();
        insert.set("column", "value");
        insert.build();
    }

    @Test(timeout = 15L, expected = IllegalStateException.class)
    public void testNoValues() {
        Insert insert = new Insert();
        insert.into("my");
        insert.build();
    }
    
    @Test(timeout = 35L)
    public void testSimple() {
        Insert insert = new Insert();
        insert.into("myTable")
              .set("a", 11)
              .build();
        
        assertEquals("insert into mytable (a) values (11)", insert.build().toString().toLowerCase());
    }
    
    @Test(timeout = 35L)
    public void testComplex() {
        Insert insert = new Insert();
        insert.into("myTable")
              .set("a", 11)
              .set("b", 12)
              .set("c", 13)
              .build();
        
        assertEquals("insert into mytable (a, b, c) values (11, 12, 13)", insert.build().toString().toLowerCase());
    }

    @Test(timeout = 15L, expected = NullPointerException.class)
    public void testTableNull() {
        Insert insert = new Insert();
        insert.into(null);
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testTableEmpty() {
        Insert insert = new Insert();
        insert.into("");
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testTableIncorrect() {
        Insert insert = new Insert();
        insert.into("1table");
    }

    @Test(timeout = 15L, expected = NullPointerException.class)
    public void testColumnNull() {
        Insert insert = new Insert();
        insert.set(null, 11);
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testColumnEmpty() {
        Insert insert = new Insert();
        insert.set("", 12);
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testColumnIncorrect() {
        Insert insert = new Insert();
        insert.set("1column", 13);
    }

    @Test(timeout = 30L)
    public void testValueNumber() {
        Insert insert = new Insert();
        insert.into("my")
              .set("integer", 7)
              .set("double", 12.164)
              .set("float", 9.14733f)
              .set("long", 781164845266666L);

        assertEquals("insert into my (integer, double, float, long) values (7, 12.164, 9.14733, 781164845266666)",
                     insert.build().toString().toLowerCase());
    }


    @Test(timeout = 30L)
    public void testValueObjects() {
        Insert insert = new Insert();
        insert.into("my")
              .set("string", "'some string'")
              .set("boolean", false)
              .set("object", new Object() {
                  @Override
                  public String toString() {
                      return "calloftostring-#11";
                  }
              });

        assertEquals("insert into my (string, boolean, object) values ('some string', false, calloftostring-#11)",
                     insert.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueNull() {
        Insert insert = new Insert();
        insert.into("my").set("test", null);

        assertEquals("insert into my (test) values (null)", insert.build().toString().toLowerCase());
    }
}
