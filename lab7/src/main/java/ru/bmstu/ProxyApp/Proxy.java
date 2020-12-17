package ru.bmstu.ProxyApp.Proxy; 

import org.zeromq.*;

public class Proxy {

    private ZContext conn;
    private ZMQ.Socket frontend,backend;

    private long time;

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
            items.poll(1);
            
        }
    }
}