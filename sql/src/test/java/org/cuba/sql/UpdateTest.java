package org.cuba.sql;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpdateTest {
    
    @Test(timeout = 35L)
    public void testSimple() {
        Update update = new Update();
        update.table("myTable")
              .set("a", 11)
              .where().column("c").equals().value(13).end()
              .build();
        
        assertEquals("update mytable set a=11 where (c=13)", update.build().toString().toLowerCase());
    }
    
    @Test(timeout = 35L)
    public void testComplex() {
        Update update = new Update();
        update.table("myTable")
              .set("a", 11).set("b", 12).set("c", "'kek'")
              .where().column("c").equals().value(13).end()
              .build();
        
        assertEquals("update mytable set a=11, b=12, c='kek' where (c=13)", update.build().toString().toLowerCase());
    }
    
    @Test(timeout = 35L, expected = NullPointerException.class)
    public void testTableNull() {
        Update update = new Update();
        update.table(null);
    }
    
    @Test(timeout = 35L, expected = IllegalArgumentException.class)
    public void testTableEmpty() {
        Update update = new Update();
        update.table("");
    }
    
    @Test(timeout = 35L, expected = NullPointerException.class)
    public void testColumnNull() {
        Update update = new Update();
        update.set(null, 11);
    }
    
    @Test(timeout = 35L, expected = IllegalArgumentException.class)
    public void testColumnEmpty() {
        Update update = new Update();
        update.set("", 12);
    }
}
