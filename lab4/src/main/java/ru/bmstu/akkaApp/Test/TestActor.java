package ru.bmstu.akkaApp.Test;

import ru.bmstu.akkaApp.Store.Message;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.japi.pf.ReceiveBuilder;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;

public class TestActor extends AbstractActor{
    private final String LANGUAGE = "js";
    private ActorSelection storeActor = getContext().actorSelection("/user/storeActor");

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(TestMessage.class, msg -> storeActor.tell(new StoreMessage(msg.getPackageID(),
                                                                                  runTest(msg.getJsScript(), msg.getTest().getTestName(), msg.getTest().getParams(),
                                                                                          msg.getFunctionName(), msg.getTest().getExpectedResult())
                                                                                  ),
                                                                 self())
                )
                .build();
    }

    private ArrayList<Test> runTest(String program, String testName, ArrayList<Integer> params, String functionName, String expectedResult) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(LANGUAGE);
        engine.eval(program);
        Invocable invocable = (Invocable) engine;
        String results = invocable.invokeFunction(functionName, params.toArray()).toString();
        ArrayList<Test> res = new ArrayList<>();
        Test test = new Test(testName, expectedResult, params, results.equals(expectedResult));
        res.add(test);
        return res;
    }
}
