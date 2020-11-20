package ru.bmstu.akkaApp.Test;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.japi.pf.ReceiveBuilder;

public class TestPackageActor extends AbstractActor{
    private ActorSelection testPerformerRouter = getContext().actorSelection("/user/testActor");

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(TestPackageMessage.class, msg -> {
                    for (Test test: msg.getTests()) {
                        testPerformerRouter.tell(new TestMessage(msg.getPackageID(), msg.getJsScript(), msg.getFunctionName(), test), self());
                    }
                })
                .build();
    }
}
