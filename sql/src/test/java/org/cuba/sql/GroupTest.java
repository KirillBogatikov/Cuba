package org.cuba.sql;

import static org.junit.Assert.assertEquals;

import org.cuba.sql.select.Group;
import org.junit.Test;

public class GroupTest {

    @Test(timeout = 15L)
    public void oneColumn() {
        Group group = new Group(null);
        group.by("column");
        assertEquals("group by column", group.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void twoColumns() {
        Group group = new Group(null);
        group.by("a").by("b");
        assertEquals("group by a, b", group.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void oneColumnOneTable() {
        Group group = new Group(null);
        group.by("T1", "a");
        assertEquals("group by t1.a", group.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void twoColumnsOneTable() {
        Group group = new Group(null);
        group.by("T1", "a", "b");
        assertEquals("group by t1.a, t1.b", group.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void twoColumnsTwoTables() {
        Group group = new Group(null);
        group.by("T1", "a").by("T2", "b");
        assertEquals("group by t1.a, t2.b", group.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void oneColumnHaving() {
        Group group = new Group(null);
        group.by("t", "a").having().column("a").equals().value("b");
        assertEquals("group by t.a having a=b", group.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void twoColumnsHaving() {
        Group group = new Group(null);
        group.by("tableA", "c1").by("tableB", "y").having().column("somec").equals().value("11");
        assertEquals("group by tablea.c1, tableb.y having somec=11", group.build().toString().toLowerCase());
    }


}
