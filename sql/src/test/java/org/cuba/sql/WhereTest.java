package org.cuba.sql;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WhereTest {
    
    @Test(timeout = 15L)
    public void testNotEquals() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").not().equals().value("'hello'");        
        assertEquals("where not(a='hello')", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testNotLike() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").not().like().value("'hello'");        
        assertEquals("where not(a like 'hello')", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testNotBetween() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").not().between(10, 15);        
        assertEquals("where not(a between 10 and 15)", where.build().toString().toLowerCase());
    }
        
    @Test(timeout = 15L)
    public void testNotIn() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").not().in(10, 12, 14, 16, 18, 20);        
        assertEquals("where not(a in (10, 12, 14, 16, 18, 20))", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testLike() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").like().value("'hello'");        
        assertEquals("where (a like 'hello')", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testEquals() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").equals().value("B");        
        assertEquals("where (a=b)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testMore() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").more().value(11);        
        assertEquals("where (a>11)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testLess() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").less().value(11);        
        assertEquals("where (a<11)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testBetween() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").between(11, 50);        
        assertEquals("where (a between 11 and 50)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 30L)
    public void testIn() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").in(11, 50, 43, "'CC'", true);        
        assertEquals("where (a in (11, 50, 43, 'cc', true))", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testMoreOrEquals() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").moreOrEquals().value(343);        
        assertEquals("where (a>=343)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testLessOrEquals() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").lessOrEquals().value(343);        
        assertEquals("where (a<=343)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testCustom() {
        Where<Expression> where = new Where<Expression>();
        where.column("mycolumn").operation(" custom ").value(12);        
        assertEquals("where (mycolumn custom 12)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testColumnAndColumn() {
        Where<Expression> where = new Where<Expression>();
        where.column("mycolumn").equals().column("other");        
        assertEquals("where (mycolumn=other)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L)
    public void testColumnAndColumnByValue() {
        Where<Expression> where = new Where<Expression>();
        where.column("mycolumn").equals().value("other");        
        assertEquals("where (mycolumn=other)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 30L)
    public void testAnd() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").lessOrEquals().value(777).and()
             .column("B").moreOrEquals().value("'hello'");        
        assertEquals("where (a<=777) and (b>='hello')", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 20L)
    public void testOr() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").lessOrEquals().value(777).or()
             .column("B").moreOrEquals().value("'hello'");        
        assertEquals("where (a<=777) or (b>='hello')", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 30L)
    public void testComplex() {
        Where<Expression> where = new Where<Expression>();
        where.column("columnA").equals().value(777).and()
             .column("columnB").less().value("'hello'").or()
             .column("columnC").like().value("'lolkek'");        
        assertEquals("where (columna=777) and (columnb<'hello') or (columnc like 'lolkek')", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 40L)
    public void testNested() {
        Where<Expression> where = new Where<Expression>();
        where.column("a").equals().value(1).and()
             .begin()
             .column("b").less().value(2).or()
             .column("c").more().value(3)
             .end().or()
             .column("d").like().value(4);
                     
        assertEquals("where (a=1) and ((b<2) or (c>3)) or (d like 4)", where.build().toString().toLowerCase());
    }
    
    @Test(timeout = 15L, expected = NullPointerException.class)
    public void testColumnNull() {
        Where<Expression> where = new Where<Expression>();
        where.column(null);
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testColumnEmpty() {
        Where<Expression> where = new Where<Expression>();
        where.column("");
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testColumnIncorrect() {
        Where<Expression> where = new Where<Expression>();
        where.column("1test");
    }
    
    @Test(timeout = 15L, expected = NullPointerException.class)
    public void testOperationNull() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").operation(null);
    }
    
    @Test(timeout = 15L, expected = IllegalArgumentException.class)
    public void testOperationEmpty() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").operation("");
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testColumnAfterColumn() {
        Where<Expression> where = new Where<Expression>();
        where.column("A").column("B");
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testColumnAfterValue() {
        Where<Expression> where = new Where<Expression>();
        where.column("C").equals().value(11).column("D");
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testColumnAfterColumnAndOperation() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").equals().column("other").column("D");
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testOperationBeforeColumn() {
        Where<Expression> where = new Where<Expression>();
        where.equals();
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testOperationAfterOperation() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").equals().equals();
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testOperationAfterValue() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").equals().value(11).less();
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testOperationAfterColumn() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").equals().column("other").less();
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testValueFirst() {
        Where<Expression> where = new Where<Expression>();
        where.value(11);
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testValueAfterColumn() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").value(11);
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void testValueAfterValue() {
        Where<Expression> where = new Where<Expression>();
        where.column("column").equals().value(11).value(12);
    }
    
    @Test(timeout = 15L)
    public void testValueNumber() {
        Where<Expression> where = new Where<Expression>();
        where.column("integer").equals().value(11).and()
             .column("long").less().value(8888882828282832L).and()
             .column("double").equals().value(1.45).and()
             .column("float").equals().value(132.77f);             
        
        assertEquals("where (integer=11) and (long<8888882828282832) and (double=1.45) and (float=132.77)",
                     where.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void testValueObjects() {
        Where<Expression> where = new Where<Expression>();
        where.column("string").equals().value("'text'").and()
             .column("boolean").equals().value(true).and()
             .column("object").equals().value(new Object() {
                 @Override
                 public String toString() {
                     return "test-object[111]";
                 }
             });

        assertEquals("where (string='text') and (boolean=true) and (object=test-object[111])",
                     where.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueNull() {
        Where<Expression> where = new Where<Expression>();
        where.column("test").equals().value(null);

        assertEquals("where (test=null)", where.build().toString().toLowerCase());
    }
}
