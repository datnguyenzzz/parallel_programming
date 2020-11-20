package ru.bmstu.akkaApp.Store;

import ru.bmstu.akkaApp.Test.Test;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class StoreActor extends AbstractActor{
    //STORE: <PackageID, ArrayList<Test>>
    private HashMap<Integer, ArrayList<Test>> store = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(StoreMessage.class, msg -> { //response message
                    if (!store.containsKey(msg.getPackageID()))
                        store.put(msg.getPackageID(), msg.getTests());
                    else {
                        ArrayList<Test> result = store.get(msg.getPackageID());
                        result.addAll(msg.getTests());
                        store.replace(msg.getPackageID(), result);
                    }
                    System.out.println("received test: " + msg);
                })
                .match(GetMessage.class, req -> {
                    sender().tell(
                        new StoreMessage(req.getPackageID(), store.get(req.getPackageID())),
                        self());
                })
                .build();
    }
}
