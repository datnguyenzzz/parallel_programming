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

import ru.bmstu.anonymizer.Store.StoreActor;

import akka.stream.javadsl.Flow;

import import org.apache.zookeeper.*;


public class ZookeeperService {

    private ZooKeeper zooKeeper;
    private ActorRef storeActor;

    private static final String ZK_BASE_URL = "127.0.0.1:2181";
    private static final String ROOT = "/servers";
    private static final String NODE = "/servers/s";
    private static final int SESSION_TIMEOUT = 3000;

    public ZookeeperService(ActorRef storeActor) throws IOException {
        this.storeActor = storeActor;
        this.zooKeeper = createZooKeeper();
        watchServers();
    }

}
