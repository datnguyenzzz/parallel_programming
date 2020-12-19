package ru.bmstu.ProxyApp.Cache;

import org.zeromq.*;

import java.util.HashMap;
import java.util.Scanner;

public class Cache {
    private final ZContext conn;

    private ZMQ.Socket socket;
    private ZMQ.Poller data;

    private HashMap<Integer,String> cache;
    private int startPos,endPos;

    public String ADDRESS = "tcp://localhost:2001";

    public Cache(ZContext conn) {
        this.conn = conn;

        cache = new HashMap<>();
        Scanner in  = new Scanner(System.in);
        startPos = in.nextInt();
        endPos = in.NextInt();

        for (int i=startPos; i<=endPos; i++) {
            cache.put(i, Integer.toString(i));
        }

        socket = conn.createSocket(SocketType.DEALER);
        socket.setHWM(0);
    }

    public static void main(String args[]) {
        try {
            ZContext conn = new ZContext();
            Cache cache = new Cache(conn);
            cache.connect();
            cache.handler();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void connect() {
        socket.connect(ADDRESS);
        data = conn.createPoller(1);
        data.register(socket, ZMQ.Poller.POLLIN);
    }
}