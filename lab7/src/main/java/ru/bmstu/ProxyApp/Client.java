package ru.bmstu.ProxyApp.Client;

import org.zeromq.*;

import java.util.Scanner;

public class Client {
    public String ADDRESS = "tcp://localhost:2000";

    private static ZContext context; 
    private static ZMQ.Socket socket;

    public static void main(String args[]) {
        System.out.println("Client is trying connect to server");
        try {

            context = new ZContext();
            socket = context.createSocket(SocketType.REQ);
            socket.connect(ADDRESS);
            Scanner in = new Scanner(System.in);
            while (true) {
                String clientMsg = in.nextLine();

                

            }


        } catch (ZMQException ex) {
            ex.printStackTrace();
        }
    }
}