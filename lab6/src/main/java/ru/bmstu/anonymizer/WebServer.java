package ru.bmstu.anonymizer;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;

import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import import org.apache.zookeeper.*;

/**
 * http://localhost:8080/? url=http://rambler.ru&count=20
 *
 */
public class WebServer {
    private static final String URL_PARAM = "url";
    private static final String COUNT_PARAM = "count";
    private static final String domain = "localhost";
    private static final int port = 8080;
    private static final Duration TIMEOUT = Duration.ofMillis(3000);

    public static void main( String[] args ) {
        System.out.println( "Start create server");

        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        WebServer server = new WebServer(system);

        final Flow<HttpRequest, HttpResponse, NotUsed> httpFlow = getHttpFlow(materializer);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
              httpFlow,
              ConnectHttp.toHost(domain,port),
              materializer
        );

        System.out.println("Server is started at http://localhost:8080");

        System.in.read();

        binding
            .thenCompose(ServerBinding::unbind)
            .thenAccept(unbound -> system.terminate());

        System.out.println("Server off");
    }
}
