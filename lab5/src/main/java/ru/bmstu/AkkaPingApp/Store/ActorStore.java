package ru.bmstu.AkkaPingApp.Store;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.Map;
import java.util.HashMap;

import ru.bmstu.AkkaPingApp.Ping.PingResult;
import ru.bmstu.AkkaPingApp.Ping.PingRequest;

public class StoreActor extends AbstractActor{
    //STORE: <test URL,sum average time pings>
    private HashMap<String, Long> store = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(StoreActor.class, msg -> { //new store
                      Long result = store.getOrDefault(msg.getTestUrl(),-1L);
                      sender().tell(new PingResult(msg.getTestUrl(), result), self());
                })
                .match(PingResult.class, req -> {
                      store.put(req.getTestUrl(), req.getAverageResponseTime())
                })
                .build();
    }
}
