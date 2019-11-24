package org.cuba;

import org.cuba.sql.UpdateTest;
import org.cuba.sql.WhereTest;
import org.cuba.utils.SqlUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SqlUtilsTest.class, WhereTest.class, UpdateTest.class })
public class FullTest {
    
}
