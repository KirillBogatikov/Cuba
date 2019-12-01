package org.cuba.sql;

import static org.junit.Assert.assertEquals;

import org.cuba.sql.common.Condition;
import org.cuba.sql.common.Expression;
import org.junit.Test;

public class ConditionTest {

    @Test(timeout = 15L)
    public void equals() {
        Condition<Expression> condition = new Condition<Expression>(null, "a");
        condition.equals().value(-10);
        assertEquals("a=-10", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void notEquals() {
        Condition<Expression> condition = new Condition<Expression>(null, "a");
        condition.not().equals().value(12);
        assertEquals("not(a=12)", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void less() {
        Condition<Expression> condition = new Condition<Expression>(null, "column");
        condition.less().value(10);
        assertEquals("column<10", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void notLess() {
        Condition<Expression> condition = new Condition<Expression>(null, "column");
        condition.not().less().value(2);
        assertEquals("not(column<2)", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void lessOrEquals() {
        Condition<Expression> condition = new Condition<Expression>(null, "id");
        condition.lessOrEquals().value(-9);
        assertEquals("id<=-9", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void notLessOrEquals() {
        Condition<Expression> condition = new Condition<Expression>(null, "id");
        condition.not().lessOrEquals().value(25);
        assertEquals("not(id<=25)", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void more() {
        Condition<Expression> condition = new Condition<Expression>(null, "col");
        condition.more().value(1);
        assertEquals("col>1", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void notMore() {
        Condition<Expression> condition = new Condition<Expression>(null, "col");
        condition.not().more().value(-247);
        assertEquals("not(col>-247)", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void moreOrEquals() {
        Condition<Expression> condition = new Condition<Expression>(null, "number");
        condition.moreOrEquals().value(770);
        assertEquals("number>=770", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void notMoreOrEquals() {
        Condition<Expression> condition = new Condition<Expression>(null, "number");
        condition.not().moreOrEquals().value(-17);
        assertEquals("not(number>=-17)", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void like() {
        Condition<Expression> condition = new Condition<Expression>(null, "name");
        condition.like().value("'Ivan'");
        assertEquals("name like 'ivan'", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void notLike() {
        Condition<Expression> condition = new Condition<Expression>(null, "name");
        condition.not().like().value("'Ivan'");
        assertEquals("not(name like 'ivan')", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void between() {
        Condition<Expression> condition = new Condition<Expression>(null, "mark");
        condition.between(1, 5);
        assertEquals("(mark between 1 and 5)", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void in() {
        Condition<Expression> condition = new Condition<Expression>(null, "array");
        condition.in(1, 2, 3, "'t'", true);
        assertEquals("array in (1, 2, 3, 't', true)", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void custom() {
        Condition<Expression> condition = new Condition<Expression>(null, "a");
        condition.operation(" custom ").value("b");
        assertEquals("(a custom b)", condition.build().toString().toLowerCase());
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void valueBeforeOperation() {
        Condition<Expression> condition = new Condition<Expression>(null, "a");
        condition.value(11);
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void valueAfterValue() {
        Condition<Expression> condition = new Condition<Expression>(null, "a");
        condition.equals()
                 .value("1");
        condition.value("2");
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void columnBeforeOperation() {
        Condition<Expression> condition = new Condition<Expression>(null, "a");
        condition.column("b");
    }
    
    @Test(timeout = 40L, expected = IllegalStateException.class)
    public void operationAfterOperation() {
        Condition<Expression> condition = new Condition<Expression>(null, "a");
        condition.equals().less();
    }

    @Test(timeout = 15L)
    public void testValueInteger() {
        Condition<Expression> condition = new Condition<Expression>(null, "integer");
        condition.equals().value(11);
        assertEquals("integer=11", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueLong() {
        Condition<Expression> condition = new Condition<Expression>(null, "long");
        condition.equals().value(111111111111111111L);
        assertEquals("long=111111111111111111", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueDouble() {
        Condition<Expression> condition = new Condition<Expression>(null, "double");
        condition.equals().value(1.009);
        assertEquals("double=1.009", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueFloat() {
        Condition<Expression> condition = new Condition<Expression>(null, "float");
        condition.equals().value(1.009f);
        assertEquals("float=1.009", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueString() {
        Condition<Expression> condition = new Condition<Expression>(null, "string");
        condition.equals().value("'text'");
        assertEquals("string='text'", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueBoolean() {
        Condition<Expression> condition = new Condition<Expression>(null, "boolean");
        condition.equals().value(true);
        assertEquals("boolean=true", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 30L)
    public void testValueObjects() {
        Condition<Expression> condition = new Condition<Expression>(null, "object");
        condition.equals().value(new Object() {
            @Override
            public String toString() {
                return "test-object[111]";
            }
        });

        assertEquals("object=test-object[111]", condition.build().toString().toLowerCase());
    }

    @Test(timeout = 15L)
    public void testValueNull() {
        Condition<Expression> condition = new Condition<Expression>(null, "test");
        condition.equals().value(null);

        assertEquals("test=null", condition.build().toString().toLowerCase());
    }
}
