package ru.bmstu.ProxyApp;

import org.zeromq.*;
import ru.bmstu.ProxyApp.Proxy;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        ZContext conn = new ZContext();
        Proxy server = new Proxy(conn);
    }
}
