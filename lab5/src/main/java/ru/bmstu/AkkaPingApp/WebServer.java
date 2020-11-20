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

import akka.pattern.PatternsCS;
import akka.routing.RoundRobinPool;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import org.asynchttpclient.AsyncHttpClient; //REST api
import org.asynchttpclient.Dsl; //init client

import ru.bmstu.AkkaPingApp.Store.StoreActor;
import ru.bmstu.AkkaPingApp.Ping.PingRequest;
import ru.bmstu.AkkaPingApp.Ping.PingResult;

/**
 * http://localhost:8080/?testUrl=http://rambler.ru&count=20
 *
 */

public class WebServer {

    //--------Constant-------------------

    private ActorRef storeActor;

    private final String URL_PARAM = "testUrl";
    private final String COUNT_PARAM = "count";
    private final String STORE_ACTOR = "storeActor";
    private static final String domain = "localhost";
    private static final int port = 8080;
    private static final long NANO_TO_MS_FACTOR = 1_000_000L;
    //-----------------------------------

    private WebServer(final ActorSystem system) {
        storeActor = system.actorOf(Props.create(StoreActor.class),STORE_ACTOR);
    }

    private Flow<HttpRequest, HttpResponse, NotUsed> getHttpFlow(ActorMaterializer materializer) {
        return Flow
                  .of(HttpRequest.class)
                  .map((request) -> {
                      Query reqQuery = request.getUri().query();
                      String testUrl = reqQuery.getOrElse(URL_PARAM, "");
                      int pingTimes = Integer.parseInt(reqQuery.getOrElse(COUNT_PARAM, "-1"));

                      return new PingRequest(testUrl, pingTimes);
                  })
                  .mapAsync(PARALLELISM, (pingRequest) -> PatternsCS.ask(storeActor, pingRequest, 5000)
                            .thenCompose((result) -> {
                                PingResult ansRequest = (PingResult) result;

                                return (ansRequest.getAverageResponseTime() == -1)
                                ? pingSource(pingRequest, materializer) //not ping to url yet, so ping it
                                : CompletableFuture.completedFuture(ansRequest);
                            })
                  )
                  .map((result) -> {
                      //result - PingResult
                      storeActor.tell(result, ActorRef.noSender());

                      return HttpResponse.create()
                                         .withStatus(StatusCodes.OK)
                                         .withEntity(HttpEntities.create(
                                              result.getTestUrl() + " " + result.getAverageResponseTime()
                                         ))
                  });
    }

    private CompletionStage<PingResult> pingSource(PingRequest pingRequest, ActorMaterializer materializer) {
        //It's Source
        return Source.from(Collections.singletonList(pingRequest))
                     .toMat(pingSink(), Keep.right())
                     .run(materializer)
                     .thenApply((sumTime) -> new PingResult(
                          pingRequest.getTestUrl(),
                          sumTime / pingRequest.getCount() / NANO_TO_MS_FACTOR
                     ));
    }

    public static void main( String[] args ) {
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        WebServer server = new WebServer(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> httpFlow = getHttpFlow(materializer);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle (
            httpFlow,
            ConnectHttp.toHost(domain, port),
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
