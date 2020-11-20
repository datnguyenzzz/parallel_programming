package ru.bmstu.AkkaPingApp;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;

import static akka.http.javadsl.server.Directives.*;

import ru.bmstu.akkaApp.Store.GetMessage;
import ru.bmstu.akkaApp.Store.StoreActor;
import ru.bmstu.akkaApp.Test.TestActor;
import ru.bmstu.akkaApp.Test.TestPackageActor;
import ru.bmstu.akkaApp.Test.TestPackageMessage;

import akka.pattern.PatternsCS;
import akka.routing.RoundRobinPool;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.*;
import org.asynchttpclient.*; //REST api

/**
 * Hello world!
 *
 */
public class WebServer
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
