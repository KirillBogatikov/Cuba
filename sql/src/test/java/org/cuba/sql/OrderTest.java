package org.cuba.sql;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OrderTest {
    
    @Test(timeout = 20L)
    public void oneColumnNoTable() {
        Order order = new Order(null);
        order.by("a");
        
        assertEquals("order by a", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void oneColumnOneTable() {
        Order order = new Order(null);
        order.by("t", "a");
        
        assertEquals("order by t.a", order.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void twoColumnsOneTable() {
        Order order = new Order(null);
        order.by("table", "b", "c");
        
        assertEquals("order by table.b, table.c", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void twoColumnsTwoTables() {
        Order order = new Order(null);
        order.by("t", "a").by("t2", "b");
        
        assertEquals("order by t.a, t2.b", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void oneAsc() {
        Order order = new Order(null);
        order.by("t", "a").asc();
        
        assertEquals("order by t.a asc", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void twoAsc() {
        Order order = new Order(null);
        order.by("t", "a").asc().by("b").asc();
        
        assertEquals("order by t.a asc, b asc", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void oneDesc() {
        Order order = new Order(null);
        order.by("col").desc();
        
        assertEquals("order by col desc", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void twoDesc() {
        Order order = new Order(null);
        order.by("col").desc().by("table", "id").desc();
        
        assertEquals("order by col desc, table.id desc", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void oneDescOneAsc() {
        Order order = new Order(null);
        order.by("a").desc().by("n").asc();
        
        assertEquals("order by a desc, n asc", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void oneAscOneDesc() {
        Order order = new Order(null);
        order.by("a11").asc().by("ng4we").desc();
        
        assertEquals("order by a11 asc, ng4we desc", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void ascAlias() {
        Order order = new Order(null);
        order.by("c1").asc();
        order.by("c2").up();
        order.by("c3").ascending();
        
        assertEquals("order by c1 asc, c2 asc, c3 asc", order.build().toString().toLowerCase());
    }

    @Test(timeout = 20L)
    public void descAlias() {
        Order order = new Order(null);
        order.by("cd0").desc();
        order.by("cd9").down();
        order.by("cd5").descending();
        
        assertEquals("order by cd0 desc, cd9 desc, cd5 desc", order.build().toString().toLowerCase());
    }

}
