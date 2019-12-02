package org.cuba.sql;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpdateTest {

    @Test(timeout = 15L, expected = IllegalStateException.class)
    public void noTable() {
        Update update = new Update();
        update.set("column", "value");
        update.build();
    }

    @Test(timeout = 15L, expected = IllegalStateException.class)
    public void noValues() {
        Update update = new Update();
        update.table("my");
        update.build();
    }
    
    @Test(timeout = 35L)
    public void simple() {
        Update update = new Update();
        update.table("myTable")
              .set("a", 11)
              .where().column("c").equals().value(13).end()
              .build();
        
        assertEquals("update mytable set a=11 where c=13", update.build().toString().toLowerCase());
    }
    
    @Test(timeout = 35L)
    public void complex() {
        Update update = new Update();
        update.table("myTable")
              .set("a", 11).set("b", 12).set("c", "'kek'")
              .where().column("c").equals().value(13).end()
              .build();
        
        assertEquals("update mytable set a=11, b=12, c='kek' where c=13", update.build().toString().toLowerCase());
    }
    
    @Test(timeout = 35L, expected = NullPointerException.class)
    public void tableNull() {
        Update update = new Update();
        update.table(null);
    }
    
    @Test(timeout = 35L, expected = IllegalArgumentException.class)
    public void tableEmpty() {
        Update update = new Update();
        update.table("");
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void tableIncorrect() {
        Update update = new Update();
        update.table("1table");
    }
    
    @Test(timeout = 35L, expected = NullPointerException.class)
    public void columnNull() {
        Update update = new Update();
        update.set(null, 11);
    }
    
    @Test(timeout = 35L, expected = IllegalArgumentException.class)
    public void columnEmpty() {
        Update update = new Update();
        update.set("", 12);
    }

    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void columnIncorrect() {
        Update update = new Update();
        update.table("1column");
    }

    @Test(timeout = 30L)
    public void valueNumber() {
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
    public void valueObjects() {
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
    public void valueNull() {
        Update update = new Update();
        update.table("my").set("test", null);

        assertEquals("update my set test=null", update.build().toString().toLowerCase());
    }
}
