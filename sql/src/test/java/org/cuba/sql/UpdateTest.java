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

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testTableIncorrect() {
        Update update = new Update();
        update.table("1table");
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

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testColumnIncorrect() {
        Update update = new Update();
        update.table("1column");
    }

    @Test(timeout = 30L)
    public void testValueNumber() {
        Update update = new Update();
        update.table("my")
              .set("integer", 11)
              .set("double", 33.882)
              .set("float", 11.34556f)
              .set("long", 111111111111111111L);

        assertEquals("update my set integer=11, double=33.882, float=11.34556, long=111111111111111111",
                     update.build().toString().toLowerCase());
    }


    @Test(timeout = 30L)
    public void testValueObjects() {
        Update update = new Update();
        update.table("my")
              .set("string", "'text'")
              .set("boolean", true)
              .set("object", new Object() {
                  @Override
                  public String toString() {
                      return "tostringcall-@4";
                  }
              });

        assertEquals("update my set string='text', boolean=true, object=tostringcall-@4",
                     update.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueNull() {
        Update update = new Update();
        update.table("my").set("test", null);

        assertEquals("update my set test=null", update.build().toString().toLowerCase());
    }
}
