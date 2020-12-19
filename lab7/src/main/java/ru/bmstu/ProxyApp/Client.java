package ru.bmstu.ProxyApp;

import org.zeromq.*;

import java.util.Scanner;

public class Client {
    public static String ADDRESS = "tcp://localhost:2000";

    private static ZContext conn; 
    private static ZMQ.Socket socket;

    public static void main(String args[]) {
        System.out.println("Client is trying connect to server");
        try {

            conn = new ZContext();
            socket = conn.createSocket(SocketType.REQ);
            socket.connect(ADDRESS);
            Scanner in = new Scanner(System.in);
            while (true) {
                String clientMsg = in.nextLine();

                ZMsg zmsgReq = new ZMsg();
                ZMsg zmsgRes = new ZMsg();

                if (clientMsg.contains("PUT") || clientMsg.contains("GET")) {
                    
                    zmsgReq.add(clientMsg);
                    zmsgReq.send(socket);
                    zmsgRes = ZMsg.recvMsg(socket);

                    if (zmsgReq != null) {
                        System.out.println("RESPONSE FROM PROXY: " + zmsgRes.popString());
                    } else {
                        System.out.println("NO RESPONSE FROM PROXY");
                    }

                } else { 
                    System.out.println("Client message only support PUT/GET method");
                }

            }


        } catch (ZMQException ex) {
            ex.printStackTrace();
        }
    }
}