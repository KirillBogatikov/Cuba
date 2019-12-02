package org.cuba.sql.select;

import static org.junit.Assert.assertEquals;

import org.cuba.sql.select.Select;
import org.junit.Test;

public class SelectTest {

    @Test(timeout = 30L)
    public void oneColumnOneTable() {
        Select select = new Select();
        select.column("column").from("table");
        assertEquals("select column from table", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void twoColumnsOneTable() {
        Select select = new Select();
        select.column("column1").column("column2").from("table");
        assertEquals("select column1, column2 from table", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void twoColumnsOneTableOneCall() {
        Select select = new Select();
        select.columns("table1", "column1", "column2");
        assertEquals("select table1.column1, table1.column2 from table1", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void twoColumnsTwoTables() {
        Select select = new Select();
        select.column("table1", "column1").column("table2", "column2");
        assertEquals("select table1.column1, table2.column2 from table1, table2", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void allOneCall() {
        Select select = new Select();
        select.all().from("tbl", "tbl1", "tbl2");
        assertEquals("select * from tbl, tbl1, tbl2", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void allFewCalls() {
        Select select = new Select();
        select.all().from("tbl").from("tbl1").from("tbl2");
        assertEquals("select * from tbl, tbl1, tbl2", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void allFromOneTable() {
        Select select = new Select();
        select.all("tbl");
        assertEquals("select tbl.* from tbl", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void distinct() {
        Select select = new Select();
        select.distinct().from("mytable").all();
        assertEquals("select distinct * from mytable", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void orderBy() {
        Select select = new Select();
        select.columns("table", "one", "two", "three")
              .order().by("table", "one").asc();
        assertEquals("select table.one, table.two, table.three from table order by table.one asc", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void groupBy() {
        Select select = new Select();
        select.columns("table", "one", "two", "three")
              .group().by("table", "one").having().column("min(table.one)").less().value(11);
        assertEquals("select table.one, table.two, table.three from table group by table.one having min(table.one)<11", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void where() {
        Select select = new Select();
        select.columns("table", "one", "two", "three")
              .where().column("table", "column").lessOrEquals().value(11);
        assertEquals("select table.one, table.two, table.three from table where table.column<=11", select.build().toString().toLowerCase());
    }
    
    @Test(timeout = 30L)
    public void offsetAndLimit() {
        Select select = new Select();
        select.all().from("table")
              .offset(123).limit(543);
        assertEquals("select * from table limit 543 offset 123", select.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void complex() {
        Select select = new Select();
        select.columns("table", "one", "two", "three")
              .all("users")
              .column("x").from("tabfab")
              .where().column("table", "column").equals().value(11).end()
              .order().by("users", "id").asc().by("x").descending().end()
              .group().by("tabfab.x").end()
              .limit(10).offset(3);
        assertEquals("select table.one, table.two, table.three, users.*, x from table, users, tabfab "
                + "where table.column=11 "
                + "group by tabfab.x "
                + "order by users.id asc, x desc "
                + "limit 10 offset 3", select.build().toString().toLowerCase());
    }
    
    
}
