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
    private static final long TIME_EPSILON = 5000;
    private static final int DEALER_SLOT = 0;

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

    private void handler() {
        long time = System.currentTimeMillis();
        while (!Thread.currentThread().isInterupted()) {
            data.poll(1);

            if (System.currentTimeMillis() - time > TIME_EPSILON) {
                ZMsg msg = new ZMsg();
                msg.add("RELOAD " + startPos + " " + endPos);
                msg.send(socket);
                System.out.println("ready message sent");
                time = System.currentTimeMillis();
            }

            if (data.pollin(DEALER_SLOT)) {
                handlerDealer();
            }
        }
    }

    private void handlerDealer() {

    }

    private void connect() {
        socket.connect(ADDRESS);
        data = conn.createPoller(1);
        data.register(socket, ZMQ.Poller.POLLIN);
    }
}