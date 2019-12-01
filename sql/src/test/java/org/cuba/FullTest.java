package org.cuba;

import org.cuba.sql.ConditionTest;
import org.cuba.sql.DeleteTest;
import org.cuba.sql.GroupTest;
import org.cuba.sql.InsertTest;
import org.cuba.sql.OrderTest;
import org.cuba.sql.UpdateTest;
import org.cuba.sql.WhereTest;
import org.cuba.utils.SqlUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
    SqlUtilsTest.class, ConditionTest.class, WhereTest.class, 
    OrderTest.class, GroupTest.class,
    UpdateTest.class, InsertTest.class, DeleteTest.class })
public class FullTest {
    
}
