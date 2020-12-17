package ru.bmstu.ProxyApp.Proxy; 

import org.zeromq.*;

public class Proxy {

    private ZContext conn;
    private ZMQ.Socket frontend,backend;

    private long time;
    private static final String SPACE = " ";

    public Proxy(ZContext conn) {
        this.conn = conn;

        this.frontend = this.conn.createSocket(SocketType.ROUTER);
        this.backend = this.conn.createSocket(SocketType.ROUTER);

        this.frontend.setHWM(0);
        this.backend.setHWM(0);

        this.frontend.bind("tcp://localhost:2000");
        this.backend.bind("tcp://localhost:2001");
        handle();
    }

    private void handle() {
        ZMQ.Poller pollers = conn.createPoller(2);

        pollers.register(frontend, ZMQ.Poller.POLLIN);
        pollers.register(backend, ZMQ.Poller.POLLIN);

        time = System.currentTimeMillies();

        while (!Thread.currentThread().isInterrupted()) {
            //0 - frontend
            //1 - backend
            items.poll(1);

            while (!Thread.currentThread().isInterrupted()) {

                if (items.pollin(0)) {
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    System.out.println("Received msg from frontend");

                    if (msg) {
                        handleClientMsg(msg);
                    } else {
                        break;
                    }
                }

                if (items.pollin(1)) {
                    ZMsg msg = Zmsg.recvMsg(backend);

                    if (msg) {
                        handleProxyMsg(msg);
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

    private void handleProxyMsg(ZMsg msg) {

    }
}