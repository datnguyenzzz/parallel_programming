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

import ru.bmstu.anonymizer.Store.StoreActor;
import ru.bmstu.anonymizer.ZookeeperService;

import import org.apache.zookeeper.*;

/**
 * http://localhost:8080/? url=http://rambler.ru&count=20
 *
 */
public class WebServer {

    private static ActorRef storeActor;

    private static final String STORE_ACTOR = "storeActor";

    private static final String URL_PARAM = "url";
    private static final String COUNT_PARAM = "count";
    private static final String domain = "localhost";
    private static final int port = 8080;
    private static final Duration TIMEOUT = Duration.ofMillis(3000);

    private WebServer(final ActorSystem system) {
        storeActor = system.actorOf(Props.create(StoreActor.class), STORE_ACTOR);
        initZooKeeper();
    }

    private static void initZooKeeper() throws IOException, KeeperException, InterruptedException {
        ZookeeperService zkService = new ZookeeperService(storeActor);
        String serverURL = "http://" + domain + ":" port;
        System.out.println("create zoo server at ",serverURL);
        zkService.createServer(serverURL);
    }

    public Route createRoute() {
        return get(() ->
            parameter(URL_PARAM, urlParam ->
                parameter(COUNT_PARAM, countParam -> {
                    int count = Integer.parseInt(countParam);

                    return count == 0 ?
                              completeWithFuture(fetch(urlParam)) :
                              completeWithFuture(redirect(urlParam, count));
                })
            )
        );
    }

    private static CompletionStage<HttpResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    private static CompletionStage<HttpResponse> redirect(String url, int count) {
      return PatternsCS.ask(actorStore, new GetRandomServerMessage(), TIMEOUT)
                     .thenCompose(serverURL -> fetch(createRedirectUrl((String) serverUrl, url, count )));
    }

    private static String createRedirectUrl(String serverUrl, String queryUrl, int count) {
        return Uri.create(serverUrl)
                  .query(Query.create(
                            Pair.create(URL_PARAM, queryUrl),
                            Pair.create(COUNT_PARAM, Integer.toString(count - 1))
                  ))
                  .toString();
    }

    public static void main( String[] args ) {
        System.out.println( "Start create server");

        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        WebServer server = new WebServer(system);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
              createRoute().flow(system,materializer);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
              routeFlow,
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
