package ru.bmstu.AkkaPingApp;

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

    private static ActorRef storeActor;
    private static AsyncHttpClient httpClient = Dsl.asyncHttpClient();

    private static final String URL_PARAM = "testUrl";
    private static final String COUNT_PARAM = "count";
    private static final String STORE_ACTOR = "storeActor";
    private static final String domain = "localhost";
    private static final int port = 8080;
    private static final long NANO_TO_MS = 1_000_000L;
    private static final int PARALLEL = 5;
    //-----------------------------------

    private WebServer(final ActorSystem system) {
        storeActor = system.actorOf(Props.create(StoreActor.class),STORE_ACTOR);
    }

    private static Flow<HttpRequest, HttpResponse, NotUsed> getHttpFlow(ActorMaterializer materializer) {
        return Flow.of(HttpRequest.class)
                   .map((request) -> {
                      Query reqQuery = request.getUri().query();
                      String testUrl = reqQuery.getOrElse(URL_PARAM, "");
                      int pingTimes = Integer.parseInt(reqQuery.getOrElse(COUNT_PARAM, "-1"));

                      return new PingRequest(testUrl, pingTimes);
                   })
                   .mapAsync(PARALLEL, (pingRequest) -> PatternsCS.ask(storeActor, pingRequest, 5000)
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
                                              "Test URL: " + result.getTestUrl() + " - Ping average time: " + result.getAverageResponseTime() + " ms"
                                         ));
                  });
    }

    private static CompletionStage<PingResult> pingSource(PingRequest pingRequest, ActorMaterializer materializer) {
        //It's Source. ping to url. Collections[stime1/count, stime2/count,...]
        return Source.from(Collections.singletonList(pingRequest))
                     .toMat(pingSink(), Keep.right())
                     .run(materializer)
                     .thenApply((sumTime) -> new PingResult(
                          pingRequest.getTestUrl(),
                          sumTime / pingRequest.getCount() / NANO_TO_MS
                     ));
    }

    private static Sink<PingRequest, CompletionStage<Long>> pingSink() {
        //turn source value to client return. Sum all time in Collections ----> create httpClient get
        return Flow.<PingRequest>create()
                   .mapConcat((pingRequest) -> Collections.nCopies(pingRequest.getCount(),pingRequest.getTestUrl()))
                   .mapAsync(PARALLEL, (url) -> {
                      long startTime = System.nanoTime();

                      return httpClient.prepareGet(url)
                                       .execute()
                                       .toCompletableFuture()
                                       .thenApply((response) -> System.nanoTime() - startTime);
                   })
                   .toMat(Sink.fold(0L, Long::sum), Keep.right());
    }

    public static void main( String[] args ) throws IOException {
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
