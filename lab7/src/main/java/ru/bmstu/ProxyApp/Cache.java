package ru.bmstu.ProxyApp.Cache;

import org.zeromq.*;
import java.util.HashMap;

public class Cache {
    private final ZContext conn;

    private HashMap<Integer,String> cache;

    public Cache(ZContext conn) {
        this.conn = conn;

        cache = new HashMap<>();
    }
}