package org.cuba.logging;

import java.io.PrintStream;

public interface Configuration {
    public PrintStream info();
    public PrintStream error();
    public PrintStream warn();
    public PrintStream debug();
    
    public void dispose();
    
    public String pattern();
    public String dateFormat();
}
