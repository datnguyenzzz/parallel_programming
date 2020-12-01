package ru.bmstu.anonymizer;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

import ru.bmstu.anonymizer.Store.StoreActor;
import ru.bmstu.anonymizer.ZookeeperService;
import ru.bmstu.anonymizer.Messages.GetRandomServerMessage;


public class SystemServer extends AllDirectives {

    private static ActorRef storeActor;
    private Http http;

    private static final String STORE_ACTOR = "storeActor";

    private static final String URL_PARAM = "url";
    private static final String COUNT_PARAM = "count";
    private static final String domain = "localhost";
    private static final int port = 8080;
    private static final Duration TIMEOUT = Duration.ofMillis(3000);

    public SystemServer(final ActorSystem system, final Http http) {
        this.storeActor = system.actorOf(Props.create(StoreActor.class), STORE_ACTOR);
        this.http = http;
        initZooKeeper();
    }

    public static void initZooKeeper() throws IOException, KeeperException, InterruptedException {
        ZookeeperService zkService = new ZookeeperService(storeActor);
        String serverURL = "http://" + domain + ":" + port;
        System.out.println("create zoo server at " + serverURL);
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

    private CompletionStage<HttpResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<HttpResponse> redirect(String url, int count) {
      return Patterns.ask(storeActor, new GetRandomServerMessage(), TIMEOUT)
                     .thenCompose(serverURL -> fetch(createRedirectUrl((String) serverURL, url, count )));
    }

    private String createRedirectUrl(String serverUrl, String queryUrl, int count) {
        return Uri.create(serverUrl)
                  .query(Query.create(
                            Pair.create(URL_PARAM, queryUrl),
                            Pair.create(COUNT_PARAM, Integer.toString(count - 1))
                  ))
                  .toString();
    }
}
