package ru.bmstu.ProxyApp.Client;

import org.zeromq.*;

public class Client {
    public String ADDRESS = "tcp://localhost:2000";

    private static ZContext context; 
    private static ZMQ.Socket socket;

    public static void main(String args[]) {
        System.out.println("Client is trying connect to server");
        
    }
}