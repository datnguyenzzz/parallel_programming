package ru.bmstu.ProxyApp.Proxy; 

import org.zeromq.*;

import ru.bmstu.ProxyApp.Client;
import ru.bmstu.ProxyApp.Cache;

public class Proxy {

    private ZContext conn;
    private ZMQ.Socket frontend,backend;

    private long time;
    private static final String SPACE = " ";
    private static final int FRONTEND_SLOT = 0;
    private static final int BACKEND_SLOT = 1;

    public Proxy(ZContext conn) {
        this.conn = conn;

        this.frontend = this.conn.createSocket(SocketType.ROUTER);
        this.backend = this.conn.createSocket(SocketType.ROUTER);

        this.frontend.setHWM(0);
        this.backend.setHWM(0);

        this.frontend.bind(Client.ADDRESS);
        this.backend.bind(Cache.ADDRESS);
        handle();
    }

    private void handle() {
        ZMQ.Poller pollers = conn.createPoller(2);

        pollers.register(frontend, ZMQ.Poller.POLLIN);
        pollers.register(backend, ZMQ.Poller.POLLIN);

        time = System.currentTimeMillies();

        while (!Thread.currentThread().isInterrupted()) {
            items.poll(1);

            while (!Thread.currentThread().isInterrupted()) {

                if (items.pollin(FRONTEND_SLOT)) {
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    System.out.println("Received msg from frontend");

                    if (msg) {
                        handleClientMsg(msg);
                    } else {
                        break;
                    }
                }

                if (items.pollin(BACKEND_SLOT)) {
                    ZMsg msg = Zmsg.recvMsg(backend);

                    if (msg) {
                        handleCacheMsg(msg);
                    } else {
                        break;
                    }
                }
            }

        }
    }

    private void handleClientMsg(ZMsg msg) {
        String[] data = msg.getLast().toString().split(SPACE);

        switch(data[0]) {
            case "PUT": {
                receivePutSignal(data,msg);
            }
            case "GET": {
                receiveGetSignal(data,msg);
            }
            default: {
                error(frontend,"error: ",msg);
            }
        }
    }

    private void handleCacheMsg(ZMsg msg) {

    }
}